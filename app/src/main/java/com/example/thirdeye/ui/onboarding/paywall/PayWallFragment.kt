package com.example.thirdeye.ui.onboarding.paywall

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.thirdeye.R
import com.example.thirdeye.constants.Constants.DETAILS
import com.example.thirdeye.constants.Constants.PRIVACY
import com.example.thirdeye.constants.Constants.TERMS
import com.example.thirdeye.data.localData.PlansData
import com.example.thirdeye.data.localData.SecurityPrefs
import com.example.thirdeye.databinding.FragmentPayWallBinding
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class PayWallFragment : Fragment() {

    private lateinit var binding: FragmentPayWallBinding

    private val planViewModel: PlanViewModel by activityViewModels()
    private lateinit var prefs: SecurityPrefs

    private var selectedPlan: PlansData? = null
    private var selectedRadioButton: RadioButton? = null
    private var selectedCard: MaterialCardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        planViewModel.loadPlans()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPayWallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = SecurityPrefs(requireContext())

        setupLinks()
        setupCancel()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                androidx.lifecycle.Lifecycle.State.STARTED
            ) {
                planViewModel.plans.collect { plans ->
                    populatePlans(plans)
                }
            }
        }
    }

    private fun setupLinks() {
        binding.privacyPolicyText.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY)))
        }

        binding.termsText.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TERMS)))
        }

        binding.detailsText.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DETAILS)))
        }
    }

    private fun setupCancel() {
        binding.cancel.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().navigateUp()
            } else {
                findNavController().navigate(
                    R.id.homeFragment,
                    null,
                    NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            }
        }
    }

    private fun populatePlans(plans: List<PlansData>) {

        binding.rgPlans.removeAllViews()

        plans.forEachIndexed { index, plan ->

            val row = layoutInflater.inflate(
                R.layout.paywall_item,
                binding.rgPlans,
                false
            ) as MaterialCardView

            val radioBtn = row.findViewById<RadioButton>(R.id.rbPlans)
            val duration = row.findViewById<TextView>(R.id.tvDuration)
            val description = row.findViewById<TextView>(R.id.tvDescription)
            val price = row.findViewById<TextView>(R.id.price)

            radioBtn.id = View.generateViewId()

            duration.text = "${plan.duration}${getString(R.string.week_1)}"
            description.text = plan.description
            price.text = plan.price

            row.setOnClickListener {
                handleSelection(row, radioBtn, plan)
            }

            radioBtn.setOnClickListener {
                handleSelection(row, radioBtn, plan)
            }

            binding.rgPlans.addView(row)

            if (index == 2 && selectedPlan == null) {
                handleSelection(row, radioBtn, plan)
            }
        }
    }

    private fun handleSelection(
        card: MaterialCardView,
        rb: RadioButton,
        plan: PlansData
    ) {
        selectedRadioButton?.isChecked = false
        selectedCard?.strokeColor =
            resources.getColor(R.color.strokeColor, null)

        rb.isChecked = true
        card.strokeColor =
            resources.getColor(R.color.dividerColor, null)

        selectedRadioButton = rb
        selectedCard = card
        selectedPlan = plan
    }
}
