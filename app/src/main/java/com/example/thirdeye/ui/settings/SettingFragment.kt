package com.example.thirdeye.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.thirdeye.MainActivity
import com.example.thirdeye.R
import com.example.thirdeye.biometrics.BiometricHelper
import com.example.thirdeye.constants.Constants.PACKAGE
import com.example.thirdeye.constants.Constants.PLAY_STORE
import com.example.thirdeye.constants.Constants.PRIVACY
import com.example.thirdeye.databinding.FragmentSettingBinding
import com.example.thirdeye.data.localData.BiometricPrefs
import com.example.thirdeye.data.localData.DelayPrefs
import com.example.thirdeye.data.localData.IntruderSelfiePrefs
import com.example.thirdeye.service.CameraCaptureService
import com.example.thirdeye.ui.dialogs.addWidget.AddWidgetDialog
import com.example.thirdeye.ui.dialogs.AttemptsDialog
import com.example.thirdeye.ui.dialogs.DelayDialog
import com.example.thirdeye.ui.widget.AddWidget
import com.example.thirdeye.utils.showStopServiceDialog
import kotlinx.coroutines.launch

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var addWidgetDialog: AddWidgetDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                setupUi()


            }
        }
    }

    private fun setupUi() {

        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()
        }

        val biometricPref = BiometricPrefs(requireContext())
        val biometricHelper = BiometricHelper(requireActivity())
        val selfiePrefs = IntruderSelfiePrefs(requireContext())
        val delayPrefs = DelayPrefs(requireContext())

        binding.bioMetricSwitch.isChecked = biometricPref.isBiometricEnabled()

        binding.bioMetricSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (CameraCaptureService.Instance != null) {
                showStopServiceDialog.showDialog(requireContext(), {
                    requireContext().stopService(
                        Intent(requireContext(), CameraCaptureService::class.java)
                    )
                })
                return@setOnCheckedChangeListener
            } else {
                biometricHelper.toggleAppLock(isChecked, biometricPref) { switchState ->
                    binding.bioMetricSwitch.isChecked = switchState
                }
            }

        }

        binding.delayLayout.setOnClickListener {
            DelayDialog.showDelayDialog(requireContext()) { selectedDelay ->
                val delay = delayPrefs.getCaptureDelay()
                binding.delayDescription.text =
                    "Current Delay is ${delay}Setting this value to less than 1000ms may cause"
            }
        }


        binding.selfieAttempt.setOnClickListener {
            AttemptsDialog.showAttemptsDialog(requireContext()) { selectNumber ->
                selfiePrefs.setWrongTries(selectNumber)
                val tries = selfiePrefs.getWrongAttempts()
                binding.selfieDescription.text =
                    "Selfie will be taken after $tries wrong tries"
            }
        }


        binding.premiumLayout.setOnClickListener {

            findNavController().navigate(
                R.id.payWallFragment,
                null,
                NavOptions.Builder().setLaunchSingleTop(true).setPopUpTo(R.id.homeFragment, true)
                    .build()
            )

        }
        binding.addwidgeteLayout.setOnClickListener {

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

            addWidgetDialog = AddWidgetDialog(requireContext())
            addWidgetDialog
                .setTitle(getString(R.string.addTitle))
                .setDescription(getString(R.string.add_widget))
                .onClick { AddWidget.addWidget(requireContext()) }
                .show()

        }


        binding.privacyPolicyLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY))
            requireContext().startActivity(intent)
        }

        binding.subscriptionLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(PLAY_STORE)
                setPackage(PACKAGE)
            }
            startActivity(intent)


        }
    }

    override fun onResume() {
        super.onResume()

    }
}
