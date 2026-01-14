package com.example.thirdeye.billing

import javax.inject.Inject

object AdController {

    private var billingManager: BillingManager? = null
    fun  init(manager: BillingManager){
        billingManager=manager
    }

    fun shouldShowAdd(): Boolean{
        return billingManager?.isPremium()!=true

    }
}