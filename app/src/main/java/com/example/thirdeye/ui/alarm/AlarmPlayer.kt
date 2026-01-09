package com.example.thirdeye.ui.alarm

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper

class AlarmPlayer(private val context: Context) {

    private var player: MediaPlayer? = null
    private var originalVolume: Int = 0
    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val handler = Handler(Looper.getMainLooper())
    private var stopRunnable: Runnable? = null

    fun play(
        uri: Uri,
        playDurationMillis: Long = 5_000L,
        forceMaxVolume: Boolean = true
    ) {
        stop()

        if (forceMaxVolume) {
            originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0)
        }

        player = MediaPlayer().apply {
            setDataSource(context, uri)
            isLooping = true
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            )
            prepare()
            start()
        }


        stopRunnable = Runnable { stop() }
        handler.postDelayed(stopRunnable!!, playDurationMillis)
    }

    fun stop() {
        stopRunnable?.let { handler.removeCallbacks(it) }
        stopRunnable = null

        player?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        player = null

        audioManager.setStreamVolume(
            AudioManager.STREAM_ALARM,
            originalVolume,
            0
        )
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }
}
