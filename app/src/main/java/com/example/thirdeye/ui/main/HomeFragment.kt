package com.example.thirdeye.ui.main

import android.animation.ValueAnimator
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.thirdeye.MainActivity
import com.example.thirdeye.R
import com.example.thirdeye.ads.NativeAdController
import com.example.thirdeye.ads.NativeAdType
import com.example.thirdeye.billing.AdController
import com.example.thirdeye.data.localData.RingtonePrefs
import com.example.thirdeye.data.localData.SecurityPrefs
import com.example.thirdeye.data.localData.ServicePrefs
import com.example.thirdeye.databinding.FragmentHomeBinding
import com.example.thirdeye.service.CameraCaptureService
import com.example.thirdeye.ui.dialogs.addWidget.AddWidgetDialog
import com.example.thirdeye.ui.dialogs.AudibleDialog
import com.example.thirdeye.ui.dialogs.biometricDialogs.FingerPrintDialog
import com.example.thirdeye.ui.intruders.IntruderPhotosViewModel
import com.example.thirdeye.ui.timer.TimerViewModel
import com.example.thirdeye.ui.widget.AddWidget
import com.example.thirdeye.utils.NetworkUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var addWidgetDialog: AddWidgetDialog
    private val homeAdapter by lazy {
        HomePagerAdapter()
    }
    private val viewModel: IntruderPhotosViewModel by activityViewModels()
    private val timerViewModel: TimerViewModel by viewModels()

    private var jobTimer: Job? = null
    private var interstitialAd: InterstitialAd? = null

    private lateinit var ringtonePrefs: RingtonePrefs
    private lateinit var prefs: SecurityPrefs

    private lateinit var nativeAdController: NativeAdController

    private var image = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = SecurityPrefs(requireContext())
        nativeAdController = NativeAdController(requireContext())


        view.post {
            setupUi()
        }
    }


    private fun setupUi() {


        observeTimer()

        binding.homePager.adapter = homeAdapter



        if (viewModel.images.value.isEmpty()) {
            viewModel.loadImages()
        }


        if (AdController.shouldShowAdd()) {
            nativeAdController.loadNativeAd(binding.nativeAdRoot, adType = NativeAdType.SMALL)


        } else {


        }



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.images.collect { images ->
                image = images.size

                if (images.isEmpty()) {
                    binding.emptyIntruders.visibility = View.VISIBLE
                    binding.homePager.visibility = View.INVISIBLE
                } else {
                    binding.emptyIntruders.visibility = View.INVISIBLE
                    binding.homePager.visibility = View.VISIBLE
                }
                homeAdapter.differ.submitList(images)
            }
        }

        homeAdapter.onLockedClick = {
            lifecycleScope.launch { viewModel.unlockImage(it.id) }
        }

        homeAdapter.onDetailsClicked = {
            val action =
                HomeFragmentDirections.actionHomeFragmentToIntruderDetailFragment(it)
            findNavController().navigate(action)
        }

        homeAdapter.onWatchAdClicked = { image ->
            if (interstitialAd != null && NetworkUtils.isInternetAvailable(requireContext())) {
                interstitialAd?.fullScreenContentCallback =
                    object : com.google.android.gms.ads.FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            viewModel.unlockImage(image.id)
                            preloadInterstitialAd()
                        }
                    }

                interstitialAd?.show(requireActivity())

            } else {
                Toast.makeText(
                    requireContext(),
                    "Ad is not loaded yet. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
                preloadInterstitialAd()
            }
        }


        homeAdapter.onPremiumClicked = {
            Toast.makeText(requireContext(), "Premium Clicked", Toast.LENGTH_SHORT).show()
        }

        setupPowerButton()
        setupClicks()
    }

    private fun setupPowerButton() {
        val handler = Handler(Looper.getMainLooper())

        // Helper function to update the UI based on actual service state
        fun renderUI(isRunning: Boolean) {
            if (isRunning) {
                binding.powerButton.setCardBackgroundColor(Color.parseColor("#00E5FF"))
                binding.powerIcon.setColorFilter(Color.parseColor("#00E5FF"))
                binding.turnOnText.text = getString(R.string.active)
                binding.outerRing.progress = 100
            } else {
                binding.powerButton.setCardBackgroundColor(Color.WHITE)
                binding.powerIcon.setColorFilter(Color.WHITE)
                binding.turnOnText.text = getString(R.string.turn_on_text)
                binding.outerRing.progress = 0
            }
        }

        // Always sync UI with service state when fragment starts
        renderUI(isServiceRunning())

        binding.powerButton.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity

            // Check permissions first
            if (!mainActivity.permissions.allGranted()) {
                Toast.makeText(
                    requireContext(),
                    "Please grant all required permissions",
                    Toast.LENGTH_SHORT
                ).show()
                mainActivity.requestPermissions()
                return@setOnClickListener
            }

            val isRunning = isServiceRunning() // Always check actual service

            if (!isRunning) {
                // Start service with ring animation
                binding.outerRing.progress = 0
                val animator = ValueAnimator.ofInt(0, 100).apply {
                    duration = 4000
                    addUpdateListener {
                        binding.outerRing.progress = it.animatedValue as Int
                    }
                    start()
                }

                CameraCaptureService.start(requireContext())

                // Wait until camera is ready
                handler.post(object : Runnable {
                    override fun run() {
                        if (CameraCaptureService.Instance?.isCameraReady == true) {
                            animator.cancel()
                            binding.outerRing.progress = 100
                            renderUI(true) // Update UI based on service
                        } else {
                            handler.postDelayed(this, 50)
                        }
                    }
                })

            } else {
                // Stop service
                requireContext().stopService(Intent(requireContext(), CameraCaptureService::class.java))
                renderUI(false)
            }
        }

        // Optional: keep UI updated if service stops unexpectedly
        handler.post(object : Runnable {
            override fun run() {
                renderUI(isServiceRunning())
                handler.postDelayed(this, 500) // check every 0.5s
            }
        })
    }

    private fun setupClicks() {
        ringtonePrefs = RingtonePrefs(requireContext())
        val isAlarmOn = ringtonePrefs.isAlarmEnabled()
        if (isAlarmOn) {
            binding.alarmOnIndicator.visibility = View.VISIBLE
        } else {
            binding.alarmOnIndicator.visibility = View.INVISIBLE
        }




        binding.alarmBtn.setOnClickListener {

            val mainActivity = requireActivity() as MainActivity
            if (!mainActivity.permissions.allGranted()) {
                Toast.makeText(
                    requireContext(),
                    "Please grant all required permissions",
                    Toast.LENGTH_SHORT
                ).show()
                mainActivity.requestPermissions()
                return@setOnClickListener
            }

            if (!ringtonePrefs.isAlarmEnabled()) {

                AudibleDialog(requireContext())
                    .setTitle(getString(R.string.alarmtitle))
                    .setMessage(getString(R.string.attempt))
                    .onClick {


                        findNavController().navigate(
                            R.id.action_homeFragment_to_alarmFragment
                        )
                    }
                    .show()
            } else findNavController().navigate(R.id.action_homeFragment_to_alarmFragment)
        }

        binding.intruderCounter.text = image.toString()

        binding.settingIcon.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_settingFragment
            )
        }


        binding.intruderDropdown.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_intrudersFragment
            )
        }

        binding.camouflageIcon.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_camouflageFragment
            )
        }

        binding.premiumIconHome.setOnClickListener {
            findNavController().navigate(
                R.id.payWallFragment,
                null,
                NavOptions.Builder().setLaunchSingleTop(true)
                    .build()
            )


        }

        binding.addWidgetBtn.setOnClickListener {

            val mainActivity = requireActivity() as MainActivity
            if (!mainActivity.permissions.allGranted()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.grantall),
                    Toast.LENGTH_SHORT
                ).show()
                mainActivity.requestPermissions()
                return@setOnClickListener
            }

            addWidgetDialog = AddWidgetDialog(requireContext())
            addWidgetDialog
                .setTitle(getString(R.string.addTitle))
                .setDescription(getString(R.string.Add_Widget))

                .onClick { AddWidget.addWidget(requireContext()) }
                .onHowToAddClick {
                    findNavController().navigate(
                        R.id.action_homeFragment_to_payWallFragment


                    )
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val mainActivity = requireActivity() as MainActivity

            if (!mainActivity.permissions.allGranted()) {
                mainActivity.requestPermissions()
            }

            if (mainActivity.permissions.allGranted() && prefs.isFirstLaunch) {
                val dialog = FingerPrintDialog(requireContext())
                dialog.show()
                prefs.isFirstLaunch = false
            }
        }

        view?.postDelayed({
            preloadInterstitialAd()
        }, 600)
    }

    private fun preloadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    private fun observeTimer() {
        jobTimer = viewLifecycleOwner.lifecycleScope.launch {
            timerViewModel.millisLeft.collect { millis ->
                val minutes = millis / 1000 / 60
                val seconds = (millis / 1000) % 60
                val centis = (millis % 1000) / 10

                binding.tvTimer.text =
                    String.format("%02d:%02d:%02d", minutes, seconds, centis)
            }
        }
    }

    private fun isServiceRunning(): Boolean {
        val activityManager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (service.service.className == CameraCaptureService::class.java.name) {
                return true
            }
        }
        return false
    }

}
