package com.example.thirdeye.data.localData

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.thirdeye.constants.Constants.APPLIED_ICON
import com.example.thirdeye.constants.Constants.ICON_PREFS

class IconPrefs(context: Context) {

    val sharedPreferences= context.getSharedPreferences(ICON_PREFS, Context.MODE_PRIVATE)

    fun saveAppliedIcon(iconRes:Int){

        sharedPreferences.edit().putInt(APPLIED_ICON,iconRes).apply()

    }
    fun getAppliedIcon():Int{

        return sharedPreferences.getInt(APPLIED_ICON,-1)
    }


}