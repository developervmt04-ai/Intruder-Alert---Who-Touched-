package com.example.thirdeye.ads

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.thirdeye.R
import com.example.thirdeye.constants.Constants.NATIVE_AD
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

class NativeAdController(private val context: Context) {

    companion object {
        private var cachedNativeAd: NativeAd? = null
        private var isLoading = false
        private var lastLoadedTime = 0L

        private const val EXPIRY_TIME = 30 * 60 * 1000
    }

    private fun isExpired(): Boolean {
        return System.currentTimeMillis() - lastLoadedTime > EXPIRY_TIME
    }

    fun loadNativeAd(
        container: ViewGroup,
        adType: NativeAdType
    ) {
        container.removeAllViews()

        val adView = inflateAdView(container, adType)
        container.addView(adView)

        val cardView =
            adView.findViewById<com.google.android.material.card.MaterialCardView>(R.id.adCard)
        val shimmer =
            adView.findViewById<ShimmerFrameLayout>(R.id.shimmerViewContainer)

        shimmer?.visibility = View.VISIBLE
        shimmer?.startShimmer()
        cardView?.visibility = View.GONE


        if (cachedNativeAd != null && !isExpired()) {
            shimmer?.stopShimmer()
            shimmer?.visibility = View.GONE
            cardView?.visibility = View.VISIBLE
            populateNativeAdView(cachedNativeAd!!, adView)
            return
        }

        if (isLoading) return
        isLoading = true

        val adLoader = AdLoader.Builder(context.applicationContext, NATIVE_AD)
            .forNativeAd { nativeAd ->
                cachedNativeAd?.destroy()
                cachedNativeAd = nativeAd
                lastLoadedTime = System.currentTimeMillis()
                isLoading = false

                shimmer?.stopShimmer()
                shimmer?.visibility = View.GONE
                cardView?.visibility = View.VISIBLE

                populateNativeAdView(nativeAd, adView)
            }
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    isLoading = false
                    shimmer?.stopShimmer()
                    shimmer?.visibility = View.GONE
                    cardView?.visibility = View.GONE
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }


    private fun inflateAdView(
        parent: ViewGroup,
        adType: NativeAdType
    ): NativeAdView {
        val layoutRes = when (adType) {
            NativeAdType.SMALL -> R.layout.small_native_ad
            NativeAdType.MEDIUM -> R.layout.medium_native_ad
            NativeAdType.LARGE -> R.layout.larger_native_ad_layout
        }

        return LayoutInflater.from(context)
            .inflate(layoutRes, parent, false) as NativeAdView
    }

    private fun populateNativeAdView(
        nativeAd: NativeAd,
        adView: NativeAdView
    ) {

        val iconView = adView.findViewById<AppCompatImageView>(R.id.ad_app_icon)
        val mediaView = adView.findViewById<MediaView>(R.id.media_view)
        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
        val bodyView = adView.findViewById<TextView>(R.id.ad_body)
        val priceView = adView.findViewById<TextView>(R.id.ad_price)
        val ctaView = adView.findViewById<Button>(R.id.ad_call_to_action)

        adView.headlineView = headlineView
        adView.mediaView = mediaView
        adView.iconView = iconView
        adView.bodyView = bodyView
        adView.priceView = priceView
        adView.callToActionView = ctaView

        headlineView.text = nativeAd.headline

        bodyView.apply {
            text = nativeAd.body
            visibility = if (nativeAd.body.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        priceView?.apply {
            text = nativeAd.price
            visibility = if (nativeAd.price.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        ctaView.apply {
            text = nativeAd.callToAction
            visibility = if (nativeAd.callToAction.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        iconView?.apply {
            setImageDrawable(nativeAd.icon?.drawable)
            visibility = View.VISIBLE
        }

        mediaView?.apply {
            mediaContent = nativeAd.mediaContent
            visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
    }
}
