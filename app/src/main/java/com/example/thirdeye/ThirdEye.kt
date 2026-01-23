package com.example.thirdeye

import android.app.Application
import com.example.thirdeye.billing.AdController
import com.example.thirdeye.billing.BillingManager
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ThirdEye(): Application() {
    @Inject lateinit var billingManager: BillingManager


    override fun onCreate() {
        super.onCreate()

        MobileAds.initialize(this)


        AdController.init(billingManager)
    }
}