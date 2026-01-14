package com.example.thirdeye

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle

import android.view.Gravity
import android.view.KeyEvent.*
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.example.thirdeye.data.localData.SecurityPrefs
import com.example.thirdeye.databinding.ActivityMainBinding
import com.example.thirdeye.permissions.DeviceAdminManager
import com.example.thirdeye.permissions.Permissions
import com.example.thirdeye.utils.LocaleHelper
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.drawable.toDrawable

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var prefs: SecurityPrefs
    lateinit var permissions: Permissions
    private lateinit var deviceAdminManager: DeviceAdminManager
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        prefs = SecurityPrefs(this)
        deviceAdminManager = DeviceAdminManager(this)
        permissions = Permissions(this, deviceAdminManager)

        setupNavGraph()


        onBackPressedDispatcher.addCallback(this) {
            val navController = (supportFragmentManager
                .findFragmentById(R.id.navHostFragment) as? NavHostFragment)
                ?.navController

            if (navController?.currentDestination?.id == R.id.homeFragment) {
                handleBackPress()
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        }
    }


    private fun handleBackPress() {

        val dialogView = layoutInflater.inflate(R.layout.exit_dalog, null)

        val ad = dialogView.findViewById<AdView>(R.id.adView)
        val backPress=dialogView.findViewById<TextView>(R.id.exitText)


        ad.loadAd(AdRequest.Builder().build())
        backPress.setOnClickListener {
            finishAffinity()


        }

        val exitDialog = Dialog(this)

        exitDialog.setContentView(dialogView)

        exitDialog.setCancelable(true)

        exitDialog.window?.apply {

            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())


            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            setGravity(Gravity.BOTTOM)
            setWindowAnimations(R.style.BottomDialogAnimation)


            decorView.setPadding(0, 0, 0, 0)

            decorView.findViewById<ViewGroup>(android.R.id.content)?.setPadding(0, 0, 0, 0)
        }

        exitDialog.setOnKeyListener { _, keyCode, _ ->

            if (keyCode == KEYCODE_BACK) {

                finishAffinity()

                true

            } else false
        }

        exitDialog.show()
    }


    private fun setupNavGraph() {
        val navHostFragment =

            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        val navController = navHostFragment.navController

        val navGraph: NavGraph = navController.navInflater.inflate(R.navigation.app_nav_graph)

        navGraph.setStartDestination(
            when {

                prefs.isFirstLaunch -> R.id.gettingStartedFragment

                else -> R.id.payWallFragment
            }
        )

        navController.graph = navGraph
    }

    fun requestPermissions() {
        permissions.checkAndRequest()
    }

    override fun attachBaseContext(newBase: Context) {

        val securityPrefs = SecurityPrefs(newBase)

        val lang = securityPrefs.selectedLanguage ?: "en"
        val context = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }


    override fun onResume() {
        super.onResume()

    }
}