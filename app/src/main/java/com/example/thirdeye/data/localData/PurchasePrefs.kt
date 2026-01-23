package com.example.thirdeye.data.localData

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.thirdeye.constants.Constants.PREMIUM_KEY
import com.example.thirdeye.constants.Constants.PURCHASE_PREFS
import androidx.core.content.edit

class PurchasePrefs(context: Context) {

    private val prefs: SharedPreferences = createSafePrefs(context)

    private fun createSafePrefs(context: Context): SharedPreferences {
        return try {
            createEncryptedPrefs(context)
        } catch (e: Exception) {
            context.deleteSharedPreferences(PURCHASE_PREFS)
            createEncryptedPrefs(context)
        }
    }

    private fun createEncryptedPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PURCHASE_PREFS,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun setPurchased(purchased: Boolean) {
        prefs.edit {
            putBoolean(PREMIUM_KEY, purchased)
        }
    }

    fun isPremiumPurchased(): Boolean {
        return prefs.getBoolean(PREMIUM_KEY, false)
    }
}
