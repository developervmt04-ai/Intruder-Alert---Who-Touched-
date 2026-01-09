package com.example.thirdeye.ui.onboarding.paywall

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
import androidx.navigation.fragment.findNavController
import com.example.thirdeye.R
import com.example.thirdeye.data.localData.PlansData
import com.example.thirdeye.databinding.FragmentPayWallBinding
import kotlinx.coroutines.launch

class PayWallFragment : Fragment() {

    private lateinit var binding: FragmentPayWallBinding
    private var plans: List<PlansData> = emptyList()

    private var selectedPlan: PlansData? = null
    private var selectedRadioButton: RadioButton? = null

    private var isExiting = false
    private var isPlansRendered = false

    private val planViewModel: PlanViewModel by activityViewModels()

    private var selectedCard: com.google.android.material.card.MaterialCardView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // moved here (same call, earlier lifecycle)
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

        binding.cancel.setOnClickListener {
            isExiting = true

            findNavController().navigate(
                R.id.homeFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.payWallFragment, true)
                    .setLaunchSingleTop(true)
                    .build()
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(
                androidx.lifecycle.Lifecycle.State.STARTED
            ) {
                planViewModel.plans.collect { planList ->
                    if (isExiting) return@collect

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

        plans.forEach { plan ->
            val row =
                layoutInflater.inflate(R.layout.paywall_item, binding.rgPlans, false)
            val card = row as com.google.android.material.card.MaterialCardView

            val radioBtn = row.findViewById<RadioButton>(R.id.rbPlans)
            val duration = row.findViewById<TextView>(R.id.tvDuration)
            val description = row.findViewById<TextView>(R.id.tvDescription)
            val price = row.findViewById<TextView>(R.id.price)

            radioBtn.id = View.generateViewId()
            duration.text = plan.duration
            description.text = plan.description
            price.text = plan.price

            row.setOnClickListener {

                handleSelection(card,radioBtn, plan)
            }

            radioBtn.setOnClickListener {
                handleSelection(card,radioBtn, plan)
            }

            binding.rgPlans.addView(row)
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
