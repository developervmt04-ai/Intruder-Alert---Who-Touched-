package com.example.thirdeye.ui.camouflage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.thirdeye.R
import com.example.thirdeye.constants.Constants.BOOK_ICON
import com.example.thirdeye.constants.Constants.CALCULATOR_ICON
import com.example.thirdeye.constants.Constants.COMPASS_ICON
import com.example.thirdeye.constants.Constants.DEFAULT_ICON
import com.example.thirdeye.constants.Constants.HEALTH_ICON
import com.example.thirdeye.constants.Constants.MUSIC_ICON
import com.example.thirdeye.constants.Constants.NOTES_ICON
import com.example.thirdeye.constants.Constants.WEATHER_ICON
import com.example.thirdeye.data.localData.AppIcons
import com.example.thirdeye.data.localData.IconPrefs
import com.example.thirdeye.databinding.FragmentCamouflageBinding
import com.example.thirdeye.ui.dialogs.IconApplyingDialog

class CamouflageFragment : androidx.fragment.app.Fragment() {

    private lateinit var binding: FragmentCamouflageBinding
    private lateinit var freeIconsAdapter: IconsAdapter
    private lateinit var premiumIconAdapter: PremiumIconAdapter
    private var selectedIconRes: Int? = null
    private var selectedIconAlias: String? = null
    private var appliedIconRes: Int = -1
    private lateinit var iconPrefs: IconPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCamouflageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iconPrefs = IconPrefs(requireContext())

        binding.backIcon.setOnClickListener { findNavController().navigateUp() }

        val freeIcons = listOf(
            AppIcons(R.mipmap.ic_launcher, DEFAULT_ICON, "Default"),
            AppIcons(R.drawable.weathericon, WEATHER_ICON, "Weather"),
            AppIcons(R.drawable.calculatoricon, CALCULATOR_ICON, "Calculator"),
            AppIcons(R.drawable.compass, COMPASS_ICON, "Compass"),
            AppIcons(R.drawable.compass, COMPASS_ICON, "Compass")
        )

        val premiumIcons = listOf(
            AppIcons(R.drawable.notesicon, NOTES_ICON, "Notes"),
            AppIcons(R.drawable.book, BOOK_ICON, "Book"),
            AppIcons(R.drawable.health, HEALTH_ICON, "Health"),
            AppIcons(R.drawable.music, MUSIC_ICON, "Music"),
            AppIcons(R.drawable.music, MUSIC_ICON, "Music")
        )

        setupFreeIconsRv(freeIcons)
        setupPremiumIconsRv(premiumIcons)

        appliedIconRes = iconPrefs.getAppliedIcon().takeIf { it != -1 } ?: freeIcons[0].icon
        val appliedIcon = freeIcons.find { it.icon == appliedIconRes } ?: freeIcons[0]

        selectedIconRes = appliedIcon.icon
        selectedIconAlias = appliedIcon.alias
        freeIconsAdapter.selectedPosition = freeIcons.indexOf(appliedIcon)

        binding.icon.setImageResource(appliedIcon.icon)

        updateApplyButton()

    }

    override fun onResume() {
        super.onResume()
        // Load ad AFTER fragment is visible
        binding.adView.loadAd(com.google.android.gms.ads.AdRequest.Builder().build())
    }

    private fun setupFreeIconsRv(icons: List<AppIcons>) {
        freeIconsAdapter = IconsAdapter()
        binding.iconRV.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = freeIconsAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(10)
        }

        freeIconsAdapter.differ.submitList(icons)

        freeIconsAdapter.onFreeIconClick = { icon, previousPosition ->
            selectedIconRes = icon.icon
            selectedIconAlias = icon.alias
            binding.icon.setImageResource(selectedIconRes!!)



            updateApplyButton()
            binding.premiumBtnapply.visibility = View.INVISIBLE
            binding.applyBtn.visibility = View.VISIBLE
        }
    }

    private fun setupPremiumIconsRv(icons: List<AppIcons>) {
        premiumIconAdapter = PremiumIconAdapter(icons)
        binding.premiumIconRv.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = premiumIconAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(8)
        }

        premiumIconAdapter.onPremiumIconClick = { icon ->
            freeIconsAdapter.clearSelection()
            selectedIconRes = icon.icon
            selectedIconAlias = icon.alias

            Glide.with(this)
                .load(icon.icon)
                .placeholder(R.drawable.blurbg)
                .into(binding.icon)

            binding.premiumBtnapply.visibility = View.VISIBLE
            binding.applyBtn.visibility = View.INVISIBLE
        }
    }

    private fun updateApplyButton() {
        val isApplied = selectedIconRes == appliedIconRes

        binding.applyBtn.isEnabled = !isApplied
        binding.applyBtn.alpha = if (isApplied) 0.5f else 1f
        binding.applyBtn.text = if (isApplied) getString(R.string.applied) else getString(R.string.apply)

        binding.applyBtn.setOnClickListener {
            if (isApplied) return@setOnClickListener

            IconApplyingDialog.showIconApplyingDialog(requireContext())

            // Check if this is the laggy part - IconChanger might be slow
            IconChanger.changeIcon(requireContext(), selectedIconAlias!!)

            appliedIconRes = selectedIconRes!!
            iconPrefs.saveAppliedIcon(appliedIconRes)
            updateApplyButton()

            freeIconsAdapter.differ.currentList.indexOfFirst { it.icon == appliedIconRes }.let { index ->
                if (index != -1) {
                    val prev = freeIconsAdapter.selectedPosition
                    freeIconsAdapter.selectedPosition = index
                    freeIconsAdapter.notifyItemChanged(prev)
                    freeIconsAdapter.notifyItemChanged(index)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Simple cleanup
        binding.iconRV.adapter = null
        binding.premiumIconRv.adapter = null
    }
}