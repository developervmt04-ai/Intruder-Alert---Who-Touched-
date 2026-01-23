package com.example.thirdeye.data.localData

import android.content.Context
import com.example.thirdeye.constants.Constants.KEY_IS_FIRST_LAUNCH
import com.example.thirdeye.constants.Constants.KEY_SELECTED_LANGUAGE
import com.example.thirdeye.constants.Constants.PREF_NAME
import androidx.core.content.edit

class SecurityPrefs(context: Context) {


    val sharedPref= context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)



    var isFirstLaunch: Boolean
        get() = sharedPref.getBoolean(KEY_IS_FIRST_LAUNCH,true)
        set(value) = sharedPref.edit { putBoolean(KEY_IS_FIRST_LAUNCH, value) }

    var selectedLanguage: String?
        get() = sharedPref.getString(KEY_SELECTED_LANGUAGE,"English")?:"English"
        set(value) = sharedPref.edit { putString(KEY_SELECTED_LANGUAGE, value) }

    var isLanguageSelected: Boolean
        get() = sharedPref.getBoolean("is_language_selected", false)
        set(value) = sharedPref.edit().putBoolean("is_language_selected", value).apply()









}