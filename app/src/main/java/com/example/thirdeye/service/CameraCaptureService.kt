package com.example.thirdeye.service

import android.Manifest
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import com.example.thirdeye.R
import com.example.thirdeye.data.encryptedStorage.EncryptedStorageRepository
import com.example.thirdeye.data.localData.DelayPrefs
import com.example.thirdeye.data.localData.RingtonePrefs
import com.example.thirdeye.data.localData.ServicePrefs
import com.example.thirdeye.notifications.Notifications
import com.example.thirdeye.ui.alarm.AlarmPlayer
import com.example.thirdeye.ui.widget.IntruderWidget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

class CameraCaptureService : Service() {

    companion object {
        const val NOTIF_ID = 1001
        var Instance: CameraCaptureService? = null
        const val ACTION_SERVICE_NOTIFICATION_SHOWN = "com.example.thirdeye.ACTION_SERVICE_NOTIFICATION_SHOWN"

        private val _state = MutableStateFlow(ServiceState())
        val state: StateFlow<ServiceState> = _state.asStateFlow()

        val notificationPosted = MutableStateFlow(false)

        data class ServiceState(
            val isRunning: Boolean = false,
            val isForeground: Boolean = false,
            val isCameraReady: Boolean = false,
            val error: String? = null
        )

        fun start(context: Context) {
            val intent = Intent(context, CameraCaptureService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        private lateinit var servicePref: ServicePrefs
    }

    private var cameraDevice: CameraDevice? = null
    private var captureSession: CameraCaptureSession? = null
    private var imageReader: ImageReader? = null
    private lateinit var bgThread: HandlerThread
    private lateinit var bgHandler: Handler
    private lateinit var player: AlarmPlayer
    @Volatile
    var isCameraReady = false
    val imageTimestamps = mutableMapOf<String, Long>()
    private lateinit var repo: EncryptedStorageRepository

    @RequiresPermission(Manifest.permission.CAMERA)
    override fun onCreate() {
        super.onCreate()
        Instance = this
        servicePref = ServicePrefs(this)
        servicePref.setService(true)

        // Set initial state
        _state.value = ServiceState(
            isRunning = true,
            isForeground = false,
            isCameraReady = false
        )

        player = AlarmPlayer(this)
        repo = EncryptedStorageRepository(applicationContext)
        Notifications.createChannels(this)

        // --- START FOREGROUND NOTIFICATION ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIF_ID, Notifications.persistentNotification(this))

            // Poll system until the notification is actually posted
            Thread {
                val manager = getSystemService(NotificationManager::class.java)
                while (true) {
                    val isActive = manager.activeNotifications.any { it.id == NOTIF_ID }
                    if (isActive) {
                        _state.value = _state.value.copy(isForeground = true)
                        notificationPosted.value = true
                        sendBroadcast(Intent(ACTION_SERVICE_NOTIFICATION_SHOWN))
                        break
                    }
                    Thread.sleep(50)
                }
            }.start()
        }
        // --- END FOREGROUND NOTIFICATION ---

        bgThread = HandlerThread("Camera2Background")
        bgThread.start()
        bgHandler = Handler(bgThread.looper)

        imageReader = ImageReader.newInstance(640, 480, ImageFormat.JPEG, 2)
        imageReader!!.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            val bytes = imageToByteArray(image)
            showNotification()

            // We consider camera ready as soon as first image arrives
            isCameraReady = true
            _state.value = _state.value.copy(isCameraReady = true)

            val (file, timeStamp) = repo.saveEncryptedImage(bytes)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            imageTimestamps[file.name] = timeStamp

            val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val timeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = dateFormatter.format(Date(timeStamp))
            val time = timeFormatter.format(Date(timeStamp))
            val dateTimeCombined = "$time\n$date"

            IntruderWidget.updateWidgetDirect(
                this,
                getString(R.string.intrusion_detected),
                dateTimeCombined,
                bitmap = bitmap
            )

            playIntrusionAlarm()
            image.close()
        }, bgHandler)

        openCamera()
    }

    fun scheduleIntruderCapture() {
        val delay = DelayPrefs(this).getCaptureDelay()
        bgHandler.postDelayed({
            if (cameraDevice != null && captureSession != null) {
                captureIntruderPhoto()
            }
        }, delay)
    }

    fun playIntrusionAlarm() {
        val pref = RingtonePrefs(this)
        if (!pref.isAlarmEnabled()) return
        val uri = pref.getAlarmTone()
        player.play(uri)
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private fun openCamera() {
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        val frontCameraId = manager.cameraIdList.first {
            manager.getCameraCharacteristics(it)
                .get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT
        }

        manager.openCamera(frontCameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                createSession()
            }

            override fun onDisconnected(camera: CameraDevice) {
                camera.close()
                _state.value = _state.value.copy(error = "Camera disconnected")
            }

            override fun onError(camera: CameraDevice, error: Int) {
                camera.close()
                _state.value = _state.value.copy(error = "Camera open error: $error")
            }
        }, bgHandler)
    }

    private fun createSession() {
        cameraDevice ?: return
        val surfaces = listOf(imageReader!!.surface)

        cameraDevice?.createCaptureSession(surfaces, object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                captureSession = session
                isCameraReady = true
                _state.value = _state.value.copy(isCameraReady = true)
                Log.d("CameraService", "Session configured → READY TO CAPTURE")
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                _state.value = _state.value.copy(error = "Capture session configuration failed")
                Log.e("CameraService", "Session config FAILED")
            }
        }, bgHandler)
    }

    fun showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).notify(
                101,
                Notifications.intrusionNotification(this)
            )
        }
    }

    private fun imageToByteArray(image: Image): ByteArray {
        val plane = image.planes[0]
        val buffer: ByteBuffer = plane.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return bytes
    }

    fun captureIntruderPhoto() {
        cameraDevice ?: return
        captureSession ?: return

        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        val characteristics = manager.getCameraCharacteristics(cameraDevice!!.id)
        val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
        val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING) ?: CameraCharacteristics.LENS_FACING_FRONT

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
        val deviceRotation = when (windowManager.defaultDisplay.rotation) {
            android.view.Surface.ROTATION_0 -> 0
            android.view.Surface.ROTATION_90 -> 90
            android.view.Surface.ROTATION_180 -> 180
            android.view.Surface.ROTATION_270 -> 270
            else -> 0
        }

        val jpegOrientation = if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            (sensorOrientation + deviceRotation) % 360
        } else {
            (sensorOrientation - deviceRotation + 360) % 360
        }

        val request = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)?.apply {
            addTarget(imageReader!!.surface)
            set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
            set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO)
            set(CaptureRequest.COLOR_CORRECTION_MODE, CaptureRequest.COLOR_CORRECTION_MODE_FAST)
            set(CaptureRequest.JPEG_ORIENTATION, jpegOrientation)
        }?.build()

        request?.let { captureSession?.capture(it, null, bgHandler) }
    }

    override fun onDestroy() {
        captureSession?.close()
        cameraDevice?.close()
        imageReader?.close()
        bgThread.quitSafely()

        _state.value = ServiceState()
        Instance = null
        servicePref.setService(false)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}
