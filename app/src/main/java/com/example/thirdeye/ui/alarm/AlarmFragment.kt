package com.example.thirdeye.ui.alarm

import android.R.style.Animation
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts

import androidx.navigation.fragment.findNavController
import com.example.thirdeye.R
import com.example.thirdeye.data.localData.RingtonePrefs
import com.example.thirdeye.databinding.FragmentAlarmBinding


class AlarmFragment : Fragment() {
    private lateinit var binding: FragmentAlarmBinding
    private lateinit var ringtonePrefs: RingtonePrefs

    private val ringTonePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.getParcelableExtra<Uri>(
                    RingtoneManager.EXTRA_RINGTONE_PICKED_URI
                )
                if (uri != null) {
                    ringtonePrefs.saveAlarmTone(uri.toString())
                    showSavedRingtoneName()
                    return@registerForActivityResult


                }
            }


        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ringtonePrefs = RingtonePrefs(requireContext())
        binding.backIcon.setOnClickListener {
            findNavController().navigateUp()

        }

        binding.alarmSwitch.isChecked = ringtonePrefs.isAlarmEnabled()



        binding.alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            ringtonePrefs.setAlarmEnabled(isChecked)


        }

        binding.selectRingtone.setOnClickListener {

            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                putExtra(
                    RingtoneManager.EXTRA_RINGTONE_TITLE,
                    getString(R.string.selected_ringtone)
                )
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            }
            ringTonePickerLauncher.launch(intent)
        }


    }

    private fun showSavedRingtoneName() {
        val uri = ringtonePrefs.getAlarmTone()
        val ringtone = RingtoneManager.getRingtone(requireContext(), uri)
        binding.ringTonename.text = ringtone?.getTitle(requireContext()) ?: "No ringtone"
    }

}