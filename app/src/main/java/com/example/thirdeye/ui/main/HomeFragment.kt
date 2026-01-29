package com.example.thirdeye.ui.main

import android.animation.ValueAnimator
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.thirdeye.MainActivity
import com.example.thirdeye.R
import com.example.thirdeye.ads.NativeAdController
import com.example.thirdeye.ads.NativeAdType
import com.example.thirdeye.billing.AdController
import com.example.thirdeye.biometrics.BiometricHelper
import com.example.thirdeye.data.localData.BiometricPrefs
import com.example.thirdeye.data.localData.RingtonePrefs
import com.example.thirdeye.data.localData.SecurityPrefs
import com.example.thirdeye.data.localData.ServicePrefs
import com.example.thirdeye.data.localData.badgePrefs
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
import kotlinx.coroutines.flow.combine
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
    private lateinit var badgePrefs: badgePrefs
    private lateinit var prefs: SecurityPrefs

    private lateinit var nativeAdController: NativeAdController


    private lateinit var bmHelper: BiometricHelper
    private lateinit var bmPrefs: BiometricPrefs


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

        bmHelper = BiometricHelper(requireActivity())
        bmPrefs = BiometricPrefs(requireContext())
        badgePrefs = badgePrefs(requireContext())


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

        homeAdapter.onPremiumClicked = {
            findNavController().navigate(R.id.payWallFragment)


        }



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.images.collect { images ->


                val seenIds = badgePrefs.getSeenIntruderIds()
                val unseenCount = images.count { it.id !in seenIds }

                if (unseenCount > 0) {
                    binding.intruderCounter.visibility = View.VISIBLE
                    binding.intruderCounter.text = images.size.toString()
                    binding.intruderCounter.text = unseenCount.toString()
                } else {
                    binding.intruderCounter.visibility = View.GONE
                }


                binding.emptyIntruders.visibility =
                    if (images.isEmpty()) View.VISIBLE else View.GONE
                binding.homePager.visibility =
                    if (images.isNotEmpty()) View.VISIBLE else View.INVISIBLE


                homeAdapter.differ.submitList(images)
            }
        }

        homeAdapter.onLockedClick = {
            findNavController().navigate(R.id.action_homeFragment_to_payWallFragment)


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
            findNavController().navigate(R.id.action_homeFragment_to_payWallFragment)
        }

        setupPowerButton()
        setupClicks()
    }

    private fun setupPowerButton() {
        var progressAnimator: ValueAnimator? = null

        fun cancelAnimation() {
            progressAnimator?.cancel()
            progressAnimator = null
        }

        fun renderUI(state: CameraCaptureService.Companion.ServiceState, notifPosted: Boolean) {
            cancelAnimation()

            if (!state.isRunning) {

                binding.powerButton.setCardBackgroundColor(Color.WHITE)
                binding.powerIcon.setColorFilter(Color.WHITE)
                binding.turnOnText.text = getString(R.string.turn_on_text)
                binding.outerRing.progress = 0
                binding.outerRing.isIndeterminate = false
                return
            }


            if (notifPosted) {
                binding.powerButton.setCardBackgroundColor(Color.parseColor("#00E5FF"))
                binding.powerIcon.setColorFilter(Color.parseColor("#00E5FF"))
                binding.turnOnText.text = getString(R.string.active)
                binding.outerRing.isIndeterminate = false

                if (binding.outerRing.progress < 100) {
                    progressAnimator = ValueAnimator.ofInt(binding.outerRing.progress, 100).apply {
                        duration = 500L
                        interpolator = android.view.animation.LinearInterpolator()
                        addUpdateListener { animation ->
                            binding.outerRing.progress = animation.animatedValue as Int
                        }
                        addListener(onEnd = {
                            progressAnimator = null
                        })
                        start()
                    }
                } else {

                    binding.outerRing.progress = 100
                }
            } else {

                binding.powerButton.setCardBackgroundColor(Color.parseColor("#B3E5FC"))
                binding.powerIcon.setColorFilter(Color.parseColor("#B3E5FC"))
                binding.turnOnText.text = "Starting..."

                if (binding.outerRing.progress == 0) {

                    binding.outerRing.isIndeterminate = false
                    progressAnimator = ValueAnimator.ofInt(0, 99).apply {
                        duration = 3000L
                        interpolator = android.view.animation.LinearInterpolator()
                        addUpdateListener { animation ->
                            binding.outerRing.progress = animation.animatedValue as Int
                        }
                        addListener(onEnd = {
                            progressAnimator = null

                        })
                        start()
                    }
                } else {

                    binding.outerRing.isIndeterminate = false
                }
            }

            state.error?.let { msg ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            }
        }

        val initialState = CameraCaptureService.state.value
        val initialNotifPosted = CameraCaptureService.notificationPosted.value

        if (initialState.isRunning && initialNotifPosted) {
            binding.outerRing.progress = 100
            binding.powerButton.setCardBackgroundColor(Color.parseColor("#00E5FF"))
            binding.powerIcon.setColorFilter(Color.parseColor("#00E5FF"))
            binding.turnOnText.text = getString(R.string.active)
        } else {

            renderUI(initialState, initialNotifPosted)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    CameraCaptureService.state,
                    CameraCaptureService.notificationPosted
                ) { state, notifPosted ->
                    state to notifPosted
                }.collect { (state, notifPosted) ->
                    renderUI(state, notifPosted)
                }
            }
        }


        binding.powerButton.setOnClickListener {
            val activity = requireActivity() as? MainActivity ?: return@setOnClickListener

            if (!activity.permissions.allGranted()) {
                Toast.makeText(requireContext(), "Grant all permissions first", Toast.LENGTH_SHORT).show()
                activity.requestPermissions()
                return@setOnClickListener
            }

            val currentState = CameraCaptureService.state.value

            if (!currentState.isRunning) {
                CameraCaptureService.start(requireContext())
            } else {
                requireContext().stopService(Intent(requireContext(), CameraCaptureService::class.java))
            }
        }
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

            if (!ringtonePrefs.isAlarmEnabled() && prefs.isFirstLaunch) {

                AudibleDialog(requireContext())
                    .setTitle(getString(R.string.alarmtitle))
                    .setMessage(getString(R.string.attempt))
                    .onClick {
                        ringtonePrefs.setAlarmEnabled(true)



                        findNavController().navigate(
                            R.id.action_homeFragment_to_alarmFragment
                        )
                    }
                    .show()
            } else findNavController().navigate(R.id.action_homeFragment_to_alarmFragment)
        }



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
                dialog.onOk {
                    if (bmHelper.isFingerprintEnrolled()) {
                        bmPrefs.setBiometricKeyEnabled(true)
                    } else {
                        bmHelper.isBiometricAvailable()
                        bmPrefs.setBiometricKeyEnabled(false)


                    }


                }
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
    private fun isMyNotificationActive(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true // fallback for older Android
        }

        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val activeNotifications = notificationManager.activeNotifications

        for (statusBarNotification in activeNotifications) {
            if (statusBarNotification.id == CameraCaptureService.NOTIF_ID) { // 1001
                Log.d("NotificationCheck", "Notification ID ${CameraCaptureService.NOTIF_ID} is active")
                return true
            }
        }

        Log.d("NotificationCheck", "Notification ID ${CameraCaptureService.NOTIF_ID} NOT found yet")
        return false
    }



}
