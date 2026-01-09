package com.example.thirdeye.data.localData

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import com.example.thirdeye.constants.Constants.PREMIUM_KEY
import com.example.thirdeye.constants.Constants.PURCHASE_PREFS

class PurchasePrefs(context: Context) {

    val masterKey= MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val prefs= EncryptedSharedPreferences.create(
        PURCHASE_PREFS,
        masterKey,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM

    )
    fun setPurchased(purchased: Boolean){
        prefs.edit().putBoolean(PREMIUM_KEY,purchased)
    }

    fun isPremiumPurchased(): Boolean{
        return prefs.getBoolean(PREMIUM_KEY,false)
    }
}