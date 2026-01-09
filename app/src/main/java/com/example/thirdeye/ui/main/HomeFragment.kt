package com.example.thirdeye.ui.main

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import com.example.thirdeye.MainActivity
import com.example.thirdeye.R
import com.example.thirdeye.data.localData.ButtonPrefs
import com.example.thirdeye.data.localData.RingtonePrefs
import com.example.thirdeye.databinding.FragmentHomeBinding
import com.example.thirdeye.service.CameraCaptureService
import com.example.thirdeye.ui.dialogs.addWidget.AddWidgetDialog
import com.example.thirdeye.ui.dialogs.AudibleDialog
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
    private lateinit var homeAdapter: HomePagerAdapter
    private lateinit var btnPrefs: ButtonPrefs

    private val viewModel: IntruderPhotosViewModel by activityViewModels()
    private val timerViewModel: TimerViewModel by viewModels()

    private var jobTimer: Job? = null
    private var interstitialAd: InterstitialAd? = null

    private lateinit var ringtonePrefs: RingtonePrefs


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

        view.post {
            setupUi()
        }
    }

    private fun setupUi() {

        btnPrefs = ButtonPrefs(requireContext())

        observeTimer()

        homeAdapter = HomePagerAdapter()
        binding.homePager.adapter = homeAdapter


        if (viewModel.images.value.isEmpty()) {
            viewModel.loadImages()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            binding.adView.loadAd(AdRequest.Builder().build())
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.images.collect { images ->
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

        homeAdapter.onWatchAdClicked = {
            if (interstitialAd != null && NetworkUtils.isInternetAvailable(requireContext())) {
                interstitialAd?.show(requireActivity())
                viewModel.unlockImage(it.id)
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

        var isOn = btnPrefs.isButtonEnabled()
        val isServiceRunning =
            CameraCaptureService.Instance?.isCameraReady == true

        val handler = android.os.Handler(Looper.getMainLooper())

        if (isOn && isServiceRunning) {
            binding.powerButton.setCardBackgroundColor(Color.parseColor("#00E5FF"))
            binding.powerIcon.setColorFilter(Color.parseColor("#00E5FF"))
            binding.outerRing.progress = 100
        } else {
            binding.powerButton.setCardBackgroundColor(Color.WHITE)
            binding.powerIcon.setColorFilter(Color.WHITE)
            binding.outerRing.progress = 0
            binding.turnOnText.text = getString(R.string.turn_on_text)
        }

        binding.powerButton.setOnClickListener {

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

            if (!isOn) {
                isOn = true
                btnPrefs.enableButton(true)
                binding.turnOnText.text = getString(R.string.active)


                binding.powerButton.setCardBackgroundColor(Color.parseColor("#00E5FF"))
                binding.powerIcon.setColorFilter(Color.parseColor("#00E5FF"))
                binding.outerRing.progress = 0

                val animator = ValueAnimator.ofInt(0, 100).apply {
                    duration = 4000
                    addUpdateListener {
                        binding.outerRing.progress = it.animatedValue as Int
                    }
                }
                animator.start()

                CameraCaptureService.start(requireContext())

                handler.post(object : Runnable {
                    override fun run() {
                        val service = CameraCaptureService.Instance
                        if (service != null && service.isCameraReady) {
                            animator.cancel()
                            binding.outerRing.progress = 100
                        } else {
                            handler.postDelayed(this, 50)
                        }
                    }
                })

            } else {
                isOn = false
                btnPrefs.enableButton(false)

                binding.turnOnText.text = getString(R.string.turn_on_text)

                binding.powerButton.setCardBackgroundColor(Color.WHITE)
                binding.powerIcon.setColorFilter(Color.WHITE)
                binding.outerRing.progress = 0

                requireContext().stopService(
                    Intent(requireContext(), CameraCaptureService::class.java)
                )
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

            AudibleDialog(requireContext())
                .setTitle(getString(R.string.alarmtitle))
                .setMessage(getString(R.string.attempt))
                .onClick {
                    findNavController().navigate(
                        R.id.action_homeFragment_to_alarmFragment
                    )
                }
                .show()
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
                .setDescription(getString(R.string.add_widget))
                .onClick { AddWidget.addWidget(requireContext()) }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()

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
}
