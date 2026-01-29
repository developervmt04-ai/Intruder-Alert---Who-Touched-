package com.example.thirdeye.billing

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.example.thirdeye.constants.Constants.SKU_REMOVE_ADS
import com.example.thirdeye.data.localData.PurchasePrefs

class BillingManager(

    private val context: Context,
    private val prefs: PurchasePrefs
) : PurchasesUpdatedListener {

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private var skuDetails: SkuDetails? = null

    fun startConnection(onReady: () -> Unit) {

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.w("BillingManager", "Billing service disconnected, will retry later.")

            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                if (p0.responseCode == BillingClient.BillingResponseCode.OK) {


                    restorePurchase()
                }
                onReady()

            }

            private fun restorePurchase() {
                Log.w("BillingManager", "Purchase restored.")

            }

        })


    }

    fun purchase(activity: FragmentActivity) {
        val details = skuDetails ?: return
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(details)
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }


    fun querySKuDetails(callBack: (SkuDetails?) -> Unit) {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(listOf(SKU_REMOVE_ADS))
            .setType(BillingClient.SkuType.INAPP)
            .build()
        billingClient.querySkuDetailsAsync(params) { _, list ->
            skuDetails = list?.firstOrNull()
            callBack(skuDetails)


        }


    }


    override fun onPurchasesUpdated(
        p0: BillingResult,
        p1: List<Purchase?>?
    ) {

        if (p0.responseCode == BillingClient.BillingResponseCode.OK && p1 != null) {


            for (purchase in p1) {

                if (purchase?.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    if (!purchase.isAcknowledged) {
                        acknowledgePurchase(purchase.purchaseToken)
                    }
                    prefs.setPurchased(true)

                }
            }

        } else if (p0.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d("BillingManager", "user canceled")


        }


    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val token = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.acknowledgePurchase(token) {}
    }

    fun restorePurchase() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { _, purchase ->
            val purchased = purchase.any {
                it.purchaseState == Purchase.PurchaseState.PURCHASED
            }
            prefs.setPurchased(purchased)

        }
    }

    fun isPremium(): Boolean {
        return prefs.isPremiumPurchased()

    }


}