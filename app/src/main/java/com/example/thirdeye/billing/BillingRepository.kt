package com.example.thirdeye.billing

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.example.thirdeye.data.localData.PurchasePrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class BillingRepository @Inject constructor(
    val prefs: PurchasePrefs,
    val client: BillingClient
): PurchasesUpdatedListener {

    private val _isPurchased= MutableStateFlow(prefs.isPremiumPurchased())
    val isPurchased=_isPurchased.asStateFlow()
    private var cachedSKUDetails: SkuDetails?=null



    fun startConnection(onReady:()-> Unit){

        client.startConnection(object : BillingClientStateListener{
            override fun onBillingServiceDisconnected() {

            }

            override fun onBillingSetupFinished(p0: BillingResult) {
               if (p0.responseCode== BillingClient.BillingResponseCode.OK)
                   queryExistingPurchase()
                onReady()
            }




        })


    }

    override fun onPurchasesUpdated(
        p0: BillingResult,
        p1: List<Purchase?>?
    ) {
        if (p0.responseCode== BillingClient.BillingResponseCode.OK && p1!=null){
            for (purchase in p1){
                if (purchase?.purchaseState== Purchase.PurchaseState.PURCHASED){
                    if (!purchase.isAcknowledged){
                        acknowledgePurchase(purchase.purchaseToken)
                    }
                    prefs.setPurchased(true)
                    _isPurchased.value=true



                }


            }


        }else if (p0.responseCode== BillingClient.BillingResponseCode.USER_CANCELED){}


    }
    fun purchase(sku: String,activity: FragmentActivity){
        val details=cachedSKUDetails?:return
        val flowParams= BillingFlowParams.newBuilder()
            .setSkuDetails(details).build()
        client.launchBillingFlow(activity,flowParams)
    }


    private fun acknowledgePurchase(purchaseToken:String) {

        val params= AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        client.acknowledgePurchase(params){}
    }


    private fun queryExistingPurchase() {
        client.queryPurchasesAsync(
            BillingClient.SkuType.INAPP
        ){billingResult,purchaseList ->
            val purchased= purchaseList.any(){
                it.purchaseState== Purchase.PurchaseState.PURCHASED
            }
            if (purchased) prefs.setPurchased(true)
            _isPurchased.value=prefs.isPremiumPurchased()



    }
    fun getSKUDetail(sku: String,callBack:(SkuDetails?)->Unit){

        val params= SkuDetailsParams.newBuilder()
            .setSkusList(listOf(sku))
            .setType(BillingClient.SkuType.INAPP)
            .build()
        client.querySkuDetailsAsync(params){
            _,skuDetailsList->
            cachedSKUDetails=skuDetailsList?.firstOrNull()
            callBack(cachedSKUDetails)

        }
    }






}
    fun setPremiumLocally(purchased: Boolean){
        prefs.setPurchased(purchased)
        _isPurchased.value=purchased

    }


}