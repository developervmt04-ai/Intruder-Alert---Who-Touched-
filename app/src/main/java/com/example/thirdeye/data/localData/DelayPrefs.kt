package com.example.thirdeye.data.localData

import android.content.Context
import com.example.thirdeye.constants.Constants.CAPTURE_DELAY
import com.example.thirdeye.constants.Constants.DEFAULT_DELAY_MS
import com.example.thirdeye.constants.Constants.DELAY_PREFS

class DelayPrefs(context: Context) {

    private val sharedPrefs = context.getSharedPreferences(DELAY_PREFS, Context.MODE_PRIVATE)

    fun setPictureDelay(delay: Long) {
        sharedPrefs.edit().putLong(CAPTURE_DELAY, delay).apply()
    }

    fun getCaptureDelay(): Long {
        return sharedPrefs.getLong(CAPTURE_DELAY, DEFAULT_DELAY_MS)
    }
}
