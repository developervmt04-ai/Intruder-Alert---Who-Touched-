package com.example.thirdeye.data.localData

import android.content.Context
import androidx.core.content.edit
import com.example.thirdeye.constants.Constants

class ServicePrefs(context: Context) {


    val sharedPref= context.getSharedPreferences(Constants.SERVICE_PREF, Context.MODE_PRIVATE)


    fun setService(enable: Boolean){
        sharedPref.edit { putBoolean(Constants.SERVICE_RUNNING, enable) }

    }
    fun getService(): Boolean{

        return  sharedPref.getBoolean(Constants.SERVICE_RUNNING,false)
    }
}