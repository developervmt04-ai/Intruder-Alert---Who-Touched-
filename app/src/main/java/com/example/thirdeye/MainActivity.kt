package com.example.thirdeye

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.example.thirdeye.data.localData.SecurityPrefs
import com.example.thirdeye.databinding.ActivityMainBinding
import com.example.thirdeye.permissions.DeviceAdminManager
import com.example.thirdeye.permissions.Permissions
import com.example.thirdeye.utils.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint

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

}
