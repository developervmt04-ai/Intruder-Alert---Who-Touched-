package com.example.thirdeye.ui.onboarding

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.thirdeye.MainActivity
import com.example.thirdeye.R
import com.example.thirdeye.data.localData.SecurityPrefs
import com.example.thirdeye.databinding.FragmentGettingStartedBinding

class GettingStartedFragment : Fragment() {

    private lateinit var binding: FragmentGettingStartedBinding
    private lateinit var securityPrefs: SecurityPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        securityPrefs = SecurityPrefs(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGettingStartedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            securityPrefs.isFirstLaunch = false
            findNavController().navigate(R.id.action_gettingStartedFragment_to_languageSelectionFragment)
        }
        binding.tvPolicyTerms.text= HtmlCompat.fromHtml(
            getString(R.string.policy_terms_html),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.tvPolicyTerms.movementMethod= LinkMovementMethod.getInstance()
    }
}
