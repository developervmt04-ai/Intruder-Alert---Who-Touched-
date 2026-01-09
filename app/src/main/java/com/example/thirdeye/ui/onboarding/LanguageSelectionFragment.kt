package com.example.thirdeye.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.thirdeye.MainActivity
import com.example.thirdeye.R
import com.example.thirdeye.data.localData.SecurityPrefs
import com.example.thirdeye.data.models.LanguageData
import com.example.thirdeye.databinding.FragmentLanguageSelectionBinding
import com.example.thirdeye.utils.LocaleHelper

class LanguageSelectionFragment : Fragment() {

    private lateinit var binding: FragmentLanguageSelectionBinding
    private lateinit var securityPrefs: SecurityPrefs
    private var selectedRadioButton: RadioButton? = null
    private var selectedCard: com.google.android.material.card.MaterialCardView? = null


    private val languages = listOf(
        LanguageData("English", R.drawable.englishicon),
        LanguageData("Urdu", R.drawable.hindiicon),
        LanguageData("Arabic", R.drawable.arabicicon),
        LanguageData("Spanish", R.drawable.spanishicon)
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
        populateLanguages()
        binding.btnContinue.visibility = View.INVISIBLE


        binding.btnContinue.setOnClickListener {
            selectedRadioButton?.let { radio ->
                val langCode = radio.tag as String

                securityPrefs.selectedLanguage = langCode
                securityPrefs.isLanguageSelected = true
                LocaleHelper.setLocale(requireActivity(), langCode)

                (requireActivity() as MainActivity).requestPermissions()

                findNavController().navigate(R.id.action_languageSelectionFragment_to_homeFragment)
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
}
