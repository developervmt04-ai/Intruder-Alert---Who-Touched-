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
import kotlinx.coroutines.launch

class PayWallFragment : Fragment() {

    private lateinit var binding: FragmentPayWallBinding
    private var plans: List<PlansData> = emptyList()

    private var selectedPlan: PlansData? = null
    private var selectedRadioButton: RadioButton? = null


    private var isPlansRendered = false

    private val planViewModel: PlanViewModel by activityViewModels()
    private lateinit var prefs: SecurityPrefs

    private var selectedCard: com.google.android.material.card.MaterialCardView? = null


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



        binding.privacyPolicyText.setOnClickListener {
            val privacy = Intent(Intent.ACTION_PICK, Uri.parse(PRIVACY))
            requireContext().startActivity(privacy)


        }
        binding.termsText.setOnClickListener {
            val terms = Intent(Intent.ACTION_PICK, Uri.parse(TERMS))
            requireContext().startActivity(terms)


        }

        binding.detailsText.setOnClickListener {
            val details = Intent(Intent.ACTION_PICK, Uri.parse(DETAILS))
            requireContext().startActivity(details)


        }


        binding.cancel.setOnClickListener {


            if (findNavController().previousBackStackEntry != null) {

                findNavController().navigateUp()
            } else {
                findNavController().navigate(
                    R.id.homeFragment,
                    null,
                    NavOptions.Builder().setLaunchSingleTop(true)
                        .build()
                )


            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                androidx.lifecycle.Lifecycle.State.STARTED
            ) {
                planViewModel.plans.collect { planList ->

                    plans = planList
                    populatePlans()
                }
            }
        }
    }

    private fun populatePlans() {
        if (isPlansRendered) return
        isPlansRendered = true

        binding.rgPlans.removeAllViews()

        plans.forEachIndexed { index, plan ->
            val row =
                layoutInflater.inflate(R.layout.paywall_item, binding.rgPlans, false)
            val card = row as com.google.android.material.card.MaterialCardView

            val radioBtn = row.findViewById<RadioButton>(R.id.rbPlans)
            val duration = row.findViewById<TextView>(R.id.tvDuration)
            val description = row.findViewById<TextView>(R.id.tvDescription)
            val price = row.findViewById<TextView>(R.id.price)

            radioBtn.id = View.generateViewId()
            duration.text = buildString {
                append(plan.duration)
                append(getString(R.string.week_1))
            }
            description.text = plan.description
            price.text = plan.price

            row.setOnClickListener {

                handleSelection(card, radioBtn, plan)
            }

            radioBtn.setOnClickListener {
                handleSelection(card, radioBtn, plan)
            }

            binding.rgPlans.addView(row)
            if (index == 2 && selectedPlan == null) {
                handleSelection(card, radioBtn, plan)
            }
        }
    }


    private fun handleSelection(
        card: com.google.android.material.card.MaterialCardView,
        rb: RadioButton,
        plan: PlansData
    ) {

        selectedRadioButton?.isChecked = false
        selectedCard?.strokeColor = resources.getColor(
            R.color.strokeColor,
            null
        )


        rb.isChecked = true
        card.strokeColor = resources.getColor(
            R.color.dividerColor,
            null
        )

        selectedRadioButton = rb
        selectedCard = card
        selectedPlan = plan
    }

}
