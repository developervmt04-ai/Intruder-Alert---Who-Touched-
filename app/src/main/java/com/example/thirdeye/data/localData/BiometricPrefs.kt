package com.example.thirdeye.data.localData

import android.content.Context
import com.example.thirdeye.constants.Constants

class BiometricPrefs(context: Context) {
    private val pref= context.applicationContext.getSharedPreferences(Constants.LOCK_KEY, Context.MODE_PRIVATE)

    fun isBiometricEnabled(): Boolean{
        return pref.getBoolean(Constants.LOCK_KEY,false)
    }
    fun setBiometricKeyEnabled(enabled: Boolean) {

        pref.edit().putBoolean(Constants.LOCK_KEY,enabled).apply()    }




}