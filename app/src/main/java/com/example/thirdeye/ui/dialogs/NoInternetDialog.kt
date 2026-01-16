package com.example.thirdeye.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.thirdeye.R
import com.example.thirdeye.databinding.NoInternetDialogBinding

class NoInternetDialog: DialogFragment() {

    private var _binding: NoInternetDialogBinding? = null
    private val binding get() = _binding!!

    private var titleText: String? = null
    private var descriptionText: String? = null
    private var checkInternetAction: (() -> Unit)? = null
    private var tryAgainAction: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NoInternetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.titleText.text = titleText
        binding.descriptionText.text = descriptionText

        binding.checkInternetBtn.setOnClickListener {
            checkInternetAction?.invoke()
        }

        binding.tryAgain.setOnClickListener {
            tryAgainAction?.invoke()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setTitle(title: String): NoInternetDialog {
        this.titleText = title
        return this
    }

    fun setDescription(desc: String): NoInternetDialog {
        this.descriptionText = desc
        return this
    }

    fun onCheckInternet(action: () -> Unit): NoInternetDialog {
        this.checkInternetAction = action
        return this
    }

    fun onTryAgain(action: () -> Unit): NoInternetDialog{
        this.tryAgainAction = action
        return this
    }
}
