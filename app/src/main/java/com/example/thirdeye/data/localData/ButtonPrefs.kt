package com.example.thirdeye.data.localData

import android.content.Context
import androidx.core.content.edit
import com.example.thirdeye.constants.Constants.POWER_BTN_PREF

class ButtonPrefs(context: Context) {

    val  prefs= context.getSharedPreferences(POWER_BTN_PREF,Context.MODE_PRIVATE)


    fun enableButton(enable:Boolean){
        prefs.edit{
            putBoolean(POWER_BTN_PREF,enable)

        }


    }

    fun isButtonEnabled():Boolean{
        return prefs.getBoolean(POWER_BTN_PREF,false)


    }



}