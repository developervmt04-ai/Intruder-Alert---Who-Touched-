package com.example.thirdeye.ui.camouflage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.thirdeye.R
import com.example.thirdeye.ads.NativeAdController
import com.example.thirdeye.ads.NativeAdType
import com.example.thirdeye.billing.AdController
import com.example.thirdeye.constants.Constants.BOOK_ICON
import com.example.thirdeye.constants.Constants.BROWSER_ICON
import com.example.thirdeye.constants.Constants.CALCULATOR_ICON
import com.example.thirdeye.constants.Constants.CALENDER_ICON
import com.example.thirdeye.constants.Constants.COMPASS_ICON
import com.example.thirdeye.constants.Constants.DEFAULT_ICON
import com.example.thirdeye.constants.Constants.GALLERY_ICON
import com.example.thirdeye.constants.Constants.HEALTH_ICON
import com.example.thirdeye.constants.Constants.JOURNAL_ICON
import com.example.thirdeye.constants.Constants.MUSIC_ICON
import com.example.thirdeye.constants.Constants.NOTES_ICON
import com.example.thirdeye.constants.Constants.THEMES_ICON
import com.example.thirdeye.constants.Constants.TRANSLATE_ICON
import com.example.thirdeye.constants.Constants.WEATHER_ICON
import com.example.thirdeye.data.localData.AppIcons
import com.example.thirdeye.data.localData.IconPrefs
import com.example.thirdeye.databinding.FragmentCamouflageBinding
import com.example.thirdeye.ui.dialogs.iconsAplying.IconApplyingDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.material.snackbar.Snackbar

class CamouflageFragment : androidx.fragment.app.Fragment() {

    private lateinit var binding: FragmentCamouflageBinding
    private lateinit var freeIconsAdapter: IconsAdapter
    private lateinit var premiumIconAdapter: PremiumIconAdapter
    private var selectedIconRes: Int? = null
    private var selectedIconAlias: String? = null
    private var appliedIconRes: Int = -1
    private lateinit var iconPrefs: IconPrefs

    private lateinit var nativeAdController: NativeAdController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCamouflageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nativeAdController = NativeAdController(requireContext())

        iconPrefs = IconPrefs(requireContext())

        binding.backIcon.setOnClickListener { findNavController().navigateUp() }

        val freeIcons = listOf(
            AppIcons(R.mipmap.ic_launcher, DEFAULT_ICON, "Default"),
            AppIcons(R.drawable.notesicon, NOTES_ICON, "Notes"),
            AppIcons(R.drawable.weathericon, WEATHER_ICON, "Weather"),
            AppIcons(R.drawable.calculatoricon, CALCULATOR_ICON, "Calculator"),
            AppIcons(R.drawable.gallery_icon, GALLERY_ICON, "Gallery"),
            AppIcons(R.drawable.calender_icon, CALENDER_ICON, "Calender"),
            AppIcons(R.drawable.compass, COMPASS_ICON, "Compass")
        )

        val premiumIcons = listOf(
            AppIcons(R.drawable.books_icon, BOOK_ICON, "Book"),
            AppIcons(R.drawable.themes_icon, THEMES_ICON, "Themes"),
            AppIcons(R.drawable.journal_icon, JOURNAL_ICON, "Journal"),
            AppIcons(R.drawable.health_icon, HEALTH_ICON, "Health"),
            AppIcons(R.drawable.browser_icon, BROWSER_ICON, "Browser"),
            AppIcons(R.drawable.music_icon, MUSIC_ICON, "Music"),
            AppIcons(R.drawable.translate_icon, TRANSLATE_ICON, "Music"),
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
        if (AdController.shouldShowAdd()) {

            nativeAdController.loadNativeAd(binding.nativeAdRoot, NativeAdType.SMALL)


        } else {


        }
    }

    private fun setupFreeIconsRv(icons: List<AppIcons>) {
        freeIconsAdapter = IconsAdapter()
        binding.iconRV.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = freeIconsAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(10)
        }

        freeIconsAdapter.differ.submitList(icons)

        freeIconsAdapter.onFreeIconClick = { icon, previousPosition ->
            premiumIconAdapter.clearSelection()
            selectedIconRes = icon.icon
            selectedIconAlias = icon.alias
            binding.icon.setImageResource(selectedIconRes!!)



            updateApplyButton()
            binding.premiumBtnapply.visibility = View.INVISIBLE
            binding.applyBtn.visibility = View.VISIBLE
        }
    }

    private fun setupPremiumIconsRv(icons: List<AppIcons>) {
        premiumIconAdapter = PremiumIconAdapter()
        binding.premiumIconRv.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = premiumIconAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(8)
        }
        premiumIconAdapter.differ.submitList(icons)
        if (!AdController.shouldShowAdd()){

            premiumIconAdapter.hidePremiumLogos()
        }

            premiumIconAdapter.onPremiumIconClick = { icon ->
                freeIconsAdapter.clearSelection()
                selectedIconRes = icon.icon
                selectedIconAlias = icon.alias
                binding.icon.setImageResource(selectedIconRes!!)





                binding.premiumBtnapply.visibility = View.VISIBLE
                binding.applyBtn.visibility = View.INVISIBLE
            }
    }

    private fun updateApplyButton() {
        val isApplied = selectedIconRes == appliedIconRes

        binding.applyBtn.isEnabled = !isApplied

        binding.applyBtn.text =
            if (isApplied) getString(R.string.applied)
            else getString(R.string.apply)

        binding.applyBtn.background =
            ContextCompat.getDrawable(
                requireContext(),
                if (isApplied) R.drawable.disabled_btn
                else R.drawable.enabled_btn
            )
        binding.applyBtn.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (isApplied) R.color.appliedBtnTextColor
                else R.color.black
            )
        )


        binding.premiumBtnapply.setOnClickListener {
            if (AdController.shouldShowAdd()) {
                Snackbar.make(
                    this.requireView(),
                    getString(R.string.premium_icon_buy_subscription),
                    Snackbar.LENGTH_SHORT
                ).show()
                findNavController().navigate(
                    R.id.payWallFragment,
                    null,
                    NavOptions.Builder().setLaunchSingleTop(true)
                        .build()
                )


            } else {


                if (isApplied) return@setOnClickListener

                val dialog = IconApplyingDialog
                    .showIconApplyingDialog(requireContext())

                binding.root.postDelayed({


                    dialog.showApplied()
                    IconChanger.changeIcon(requireContext(), selectedIconAlias!!)


                }, 1200)


                appliedIconRes = selectedIconRes!!
                iconPrefs.saveAppliedIcon(appliedIconRes)
                updateApplyButton()

                premiumIconAdapter.differ.currentList.indexOfFirst { it.icon == appliedIconRes }
                    .let { index ->
                        if (index != -1) {
                            val prev = premiumIconAdapter.selectedPosition
                            premiumIconAdapter.selectedPosition = index
                            premiumIconAdapter.notifyItemChanged(prev)
                            premiumIconAdapter.notifyItemChanged(index)
                        }
                    }
            }


        }


        binding.applyBtn.setOnClickListener {
            if (isApplied) return@setOnClickListener

            val dialog = IconApplyingDialog
                .showIconApplyingDialog(requireContext())

            binding.root.postDelayed({


                dialog.showApplied()
                IconChanger.changeIcon(requireContext(), selectedIconAlias!!)


            }, 1200)


            appliedIconRes = selectedIconRes!!
            iconPrefs.saveAppliedIcon(appliedIconRes)
            updateApplyButton()

            freeIconsAdapter.differ.currentList.indexOfFirst { it.icon == appliedIconRes }
                .let { index ->
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
        binding.iconRV.adapter = null
        binding.premiumIconRv.adapter = null
    }
}