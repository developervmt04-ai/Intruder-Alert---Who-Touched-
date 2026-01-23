package com.example.thirdeye.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.thirdeye.MainActivity
import com.example.thirdeye.R
import com.example.thirdeye.ads.NativeAdController
import com.example.thirdeye.ads.NativeAdType
import com.example.thirdeye.billing.AdController
import com.example.thirdeye.data.localData.SecurityPrefs
import com.example.thirdeye.data.models.LanguageData
import com.example.thirdeye.databinding.FragmentLanguageSelectionBinding
import com.example.thirdeye.utils.LocaleHelper
import com.google.android.gms.ads.AdRequest

class LanguageSelectionFragment : Fragment() {

    private lateinit var binding: FragmentLanguageSelectionBinding
    private lateinit var securityPrefs: SecurityPrefs
    private var selectedRadioButton: RadioButton? = null

    private lateinit var nativeAdController: NativeAdController
    private var selectedCard: com.google.android.material.card.MaterialCardView? = null


    private val languages = listOf(
        LanguageData("English", R.drawable.englishicon),
        LanguageData("Urdu", R.drawable.hindiicon),
        LanguageData("Arabic", R.drawable.arabicicon),
        LanguageData("Spanish", R.drawable.spanishicon),
        LanguageData("Portuguese ", R.drawable.portaguesicon),
        LanguageData("Korean ", R.drawable.koreanicon),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        securityPrefs = SecurityPrefs(requireContext())


        nativeAdController = NativeAdController(requireContext())

        populateLanguages()
        binding.btnContinue.visibility = View.INVISIBLE


        binding.btnContinue.setOnClickListener {
            selectedRadioButton?.let { radio ->
                val langCode = radio.tag as String

                securityPrefs.selectedLanguage = langCode
                securityPrefs.isLanguageSelected = true
                LocaleHelper.setLocale(requireActivity(), langCode)

                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.putExtra("startFragment", "homeFragment")
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                requireActivity().finish()
                startActivity(intent)

                requireActivity().overridePendingTransition(0, 0)
                requireActivity().finish()
                startActivity(intent)
                requireActivity().overridePendingTransition(0, 0)
            }
        }
    }

    private fun populateLanguages() {
        binding.rgLanguages.removeAllViews()

        languages.forEach { languageData ->
            val row = layoutInflater.inflate(
                R.layout.language_item,
                binding.rgLanguages,
                false
            )
            val card = row as com.google.android.material.card.MaterialCardView

            val radioButton = row.findViewById<RadioButton>(R.id.rbLanguage)
            val textView = row.findViewById<TextView>(R.id.tvLanguageName)
            val imageView = row.findViewById<ImageView>(R.id.ivLanguageIcon)

            radioButton.id = View.generateViewId()
            textView.text = languageData.language
            imageView.setImageResource(languageData.img)

            radioButton.tag = getLanguageCode(languageData.language)
            radioButton.visibility = View.INVISIBLE

            row.setOnClickListener {
                radioButton.visibility = View.VISIBLE

                binding.btnContinue.visibility = View.VISIBLE
                handleSelection(card, radioButton)
            }
            radioButton.setOnClickListener {
                radioButton.visibility = View.VISIBLE

                binding.btnContinue.visibility = View.VISIBLE
                handleSelection(card, radioButton)
            }

            binding.rgLanguages.addView(row)
        }
    }

    private fun handleSelection(
        card: com.google.android.material.card.MaterialCardView,
        radioButton: RadioButton
    ) {
        selectedCard?.strokeColor = resources.getColor(R.color.strokeColor, null)

        card.strokeColor = resources.getColor(R.color.dividerColor, null)

        selectedRadioButton?.isChecked = false
        radioButton.isChecked = true

        selectedRadioButton = radioButton
        selectedCard = card
    }


    private fun getLanguageCode(language: String): String = when (language) {
        "English" -> "en"
        "Urdu" -> "ur"
        "Arabic" -> "ar"
        "Spanish" -> "es"
        else -> "en"
    }

    override fun onResume() {
        super.onResume()
        if (AdController.shouldShowAdd()) {

            nativeAdController.loadNativeAd(
                binding
                    .nativeAdRoot, NativeAdType.LARGE
            )


        } else {


        }
    }
}
