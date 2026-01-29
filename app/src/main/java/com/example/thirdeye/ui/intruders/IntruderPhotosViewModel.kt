package com.example.thirdeye.ui.intruders

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.thirdeye.R
import com.example.thirdeye.data.encryptedStorage.EncryptedStorageRepository
import com.example.thirdeye.data.models.IntrudersImages
import com.example.thirdeye.data.localData.LockImagePrefs
import com.example.thirdeye.data.localData.badgePrefs
import com.example.thirdeye.data.models.IntruderImageMetaData
import com.example.thirdeye.ui.widget.IntruderWidget
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@HiltViewModel
class IntruderPhotosViewModel @Inject constructor(
    application: Application,
    private val intruderRepo: IntruderRepo,
    private val lockedPref: LockImagePrefs,
    private val badgePrefs: badgePrefs
) : AndroidViewModel(application) {

    private val repo = EncryptedStorageRepository(application.applicationContext)

    private val _images = MutableStateFlow<List<IntrudersImages>>(emptyList())
    val images = _images.asStateFlow()
    private val IMAGE_LIMIT = 3


    val intruderOpened=MutableStateFlow(false)


    fun loadImages() {
        viewModelScope.launch(Dispatchers.IO) {

            val files = repo.listOfImages().sortedByDescending { it.lastModified() }


            if (files.size > 3) {
                val fourthImage = files[0]
                if (!lockedPref.isLocked(fourthImage.name) &&
                    !lockedPref.isManuallyUnlocked(fourthImage.name)
                ) {
                    lockedPref.addLocked(fourthImage.name)
                }
            }


            val imageList = files.map { file ->

                val metaFromDb = intruderRepo.getMeta(file.name)
                val savedTime = metaFromDb?.timeStamp ?: file.lastModified()


                val (bytes, timeStamp) = repo.readEncryptedImage(file, savedTime)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)


                val isLocked = lockedPref.isLocked(file.name)


                val metadata = IntruderImageMetaData(id = file.name, timeStamp = timeStamp)
                intruderRepo.addImageData(metadata)


                IntrudersImages(
                    id = file.name,
                    bitmap = bitmap,
                    isLocked = isLocked,
                    file = file,
                    timeStamp = timeStamp
                )
            }
            val latestImage = imageList.maxByOrNull { it.timeStamp }
            latestImage?.let { image ->
                val widgetBitmap = if (image.isLocked) {
                    BitmapFactory.decodeResource(application.resources, R.drawable.emptyicon)
                } else {
                    image.bitmap
                }

                IntruderWidget.updateWidgetDirect(
                    context = application,
                    state = if (image.isLocked) "Locked" else "Intrusion Detected",
                    dataTime = formatTime(image.timeStamp),
                    isLocked = image.isLocked,
                    bitmap = widgetBitmap
                )
            }





            _images.value = imageList
        }
    }


    fun deleteImage(file: File) {
        viewModelScope.launch(Dispatchers.IO) {

            repo.deleteImage(file)


            if (lockedPref.isLocked(file.name)) {
                lockedPref.removeLocked(file.name)
            }


            intruderRepo.deleteData(file.name)


            loadImages()
        }
    }

    fun unlockAllImages() {
        viewModelScope.launch {
            _images.value.forEach { lockedPref.removeLocked(it.id) }
            _images.update { list ->
                list.map { it.copy(isLocked = false) }
            }
        }
    }


    fun unlockImage(id: String) {
        viewModelScope.launch {

            lockedPref.removeLocked(id)
            lockedPref.addManualUnlock(id)


            _images.update { list ->
                list.map { if (it.id == id) it.copy(isLocked = false) else it }
            }
        }
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("HH:mm dd/MM/yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
    fun markImagesSeen(ids: List<String>) {
        viewModelScope.launch {
            badgePrefs.markIntruderSeen(ids)
            intruderOpened.value = true
        }
    }


    suspend fun isIntruderLimitReached(): Boolean {
        return withContext(Dispatchers.IO) {
            repo.listOfImages().size > IMAGE_LIMIT
        }
    }


}
