package com.example.thirdeye.data.localData

import android.content.Context
import com.example.thirdeye.constants.Constants.BADGE_PREF
import com.example.thirdeye.constants.Constants.SEEN

class badgePrefs(context: Context) {


    private val prefs = context.getSharedPreferences(BADGE_PREF, Context.MODE_PRIVATE)

    fun markIntruderSeen(ids: List<String>) {
        val seenSet = prefs.getStringSet(SEEN, emptySet())!!.toMutableSet()
        seenSet.addAll(ids) // Add all IDs individually
        prefs.edit().putStringSet(SEEN, seenSet).apply()
    }

    fun getSeenIntruderIds(): Set<String> {
        return prefs.getStringSet(SEEN, emptySet()) ?: emptySet()
    }
}