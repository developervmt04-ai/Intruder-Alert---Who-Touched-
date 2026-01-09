package com.example.thirdeye.billing

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class BillingViewModel @Inject constructor(
    val repo : BillingRepository
): ViewModel() {



    val purchased= repo.isPurchased

    fun startBilling()=repo.startConnection {  }
    fun purchaseItem(sku:String,activity: FragmentActivity)=repo.purchase(sku,activity)
    fun setPremiumLocally(purchased: Boolean)=repo.setPremiumLocally(purchased)
}