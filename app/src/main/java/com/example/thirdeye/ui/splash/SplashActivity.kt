package com.example.thirdeye.ui.splash

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.thirdeye.MainActivity
import com.example.thirdeye.R
import com.example.thirdeye.billing.AdController
import com.example.thirdeye.billing.BillingManager
import com.example.thirdeye.biometrics.BiometricHelper
import com.example.thirdeye.data.localData.BiometricPrefs
import com.example.thirdeye.data.localData.PurchasePrefs
import com.example.thirdeye.data.localData.SecurityPrefs
import com.example.thirdeye.databinding.ActivitySplashBinding
import com.example.thirdeye.ui.dialogs.NoInternetDialog
import com.example.thirdeye.ui.dialogs.biometricDialogs.UnlockDialog
import com.example.thirdeye.utils.LocaleHelper
import com.example.thirdeye.utils.NetworkUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar

class SplashActivity : AppCompatActivity() {

    private var navigated = false

    private lateinit var biometricHelper: BiometricHelper
    private lateinit var biometricPrefs: BiometricPrefs
    private lateinit var dialog: UnlockDialog
    private lateinit var binding: ActivitySplashBinding
    private lateinit var purchasePrefs: PurchasePrefs
    private lateinit var billingManager: BillingManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        purchasePrefs = PurchasePrefs(this)
        billingManager = BillingManager(this, purchasePrefs)
        billingManager.startConnection {
            AdController.init(billingManager)
        }





        biometricHelper = BiometricHelper(this)
        biometricPrefs = BiometricPrefs(this)
        dialog = UnlockDialog(this)
    }

    override fun onResume() {
        super.onResume()



        if (biometricPrefs.isBiometricEnabled()) {
            showUnlockDialog()
        } else {
            startMain()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = SecurityPrefs(newBase)
        val langCode = prefs.selectedLanguage ?: "en"
        val context = LocaleHelper.setLocale(newBase, langCode)
        super.attachBaseContext(context)
    }


    private fun showUnlockDialog() {
        dialog.setTitle(getString(R.string.Fingerprint_lock_text))
            .setDescription(getString(R.string.safa))
            .onClick {
                biometricHelper.authenticate(
                    onSuccess = { startMain() },
                    onCancel = {
                        finishAffinity()
                    }
                )
            }
            .show()
    }

    private fun startMain() {
        window.decorView.post {
            if (!NetworkUtils.isInternetAvailable(this)) {
                val noInternet = NoInternetDialog()
                noInternet.setTitle(getString(R.string.no_internet_connection))
                noInternet.setDescription(getString(R.string.noIntdes))
                    .onCheckInternet {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    startActivity( Intent(Settings.Panel.ACTION_WIFI))
                                }


                    }
                    .onTryAgain {
                        if (NetworkUtils.isInternetAvailable(this)) {
                            noInternet.dismiss()
                            if (!navigated) {
                                navigated = true
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                        } else {
                            Snackbar.make(
                                noInternet.requireView(),
                                "Still no internet!",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                noInternet.show(supportFragmentManager, "NoInternetDialog")
            } else {
                if (!navigated) { // only navigate if not already
                    navigated = true
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

}

