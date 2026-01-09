package com.example.thirdeye

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ThirdEye(): Application() {


    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}