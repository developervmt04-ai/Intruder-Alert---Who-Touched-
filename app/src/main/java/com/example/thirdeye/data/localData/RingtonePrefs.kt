package com.example.thirdeye.data.localData

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import com.example.thirdeye.constants.Constants.ALARM_ENABLED
import com.example.thirdeye.constants.Constants.ALARM_PREF
import com.example.thirdeye.constants.Constants.ALARM_TONE
import androidx.core.content.edit
import androidx.core.net.toUri

class RingtonePrefs(context: Context) {
    val pref=context.getSharedPreferences(ALARM_PREF, Context.MODE_PRIVATE)

    fun saveAlarmTone(uri:String){
        pref.edit {
            putString(ALARM_TONE, uri)
        }


    }
    fun getAlarmTone(): Uri{

      val saved=  pref.getString(ALARM_TONE, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString())

    return saved!!.toUri()
    }
    fun setAlarmEnabled(enabled: Boolean){
        pref.edit { putBoolean(ALARM_ENABLED, enabled) }


    }

    fun isAlarmEnabled(): Boolean=

        pref.getBoolean(ALARM_ENABLED,false)

}