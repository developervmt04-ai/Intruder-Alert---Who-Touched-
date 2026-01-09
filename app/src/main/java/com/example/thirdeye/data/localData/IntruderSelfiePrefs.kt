package com.example.thirdeye.data.localData

import android.content.Context
import com.example.thirdeye.constants.Constants.SELECTED_ATTEMPT
import com.example.thirdeye.constants.Constants.SELFIE_PREF
import androidx.core.content.edit
import com.example.thirdeye.constants.Constants.WRONG_ATTEMPTS

class  IntruderSelfiePrefs(context: Context) {

    val prefs=context.getSharedPreferences(SELFIE_PREF, Context.MODE_PRIVATE)



    fun setWrongTries(
        count: Int){
        this.prefs.edit { putInt(SELECTED_ATTEMPT, count) }

    }
    fun getWrongAttempts(): Int{
      return  prefs.getInt(SELECTED_ATTEMPT,1)
    }

    fun incrementAttempt(): Int{
        val attempts=prefs.getInt(WRONG_ATTEMPTS,0)+1
        prefs.edit{
            putInt(WRONG_ATTEMPTS,attempts)

        }

        return attempts
    }
    fun resetAttempts(){
        prefs.edit{
            putInt(WRONG_ATTEMPTS,0)
        }
    }





}