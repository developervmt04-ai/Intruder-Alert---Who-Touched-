package com.example.thirdeye.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.example.thirdeye.databinding.AudibleDialogBinding

class AudibleDialog(context: Context) {

    private val dialog = Dialog(context).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private val binding = AudibleDialogBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        binding.closeButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun setTitle(title: String): AudibleDialog {
        binding.title.text = title
        return this
    }

    fun setMessage(msg: String): AudibleDialog {
        binding.description.text = msg
        return this
    }

    fun onClick(action: () -> Unit): AudibleDialog {
        binding.addWidget.setOnClickListener {
            action.invoke()
            dialog.dismiss()
        }
        return this
    }

    fun show(): AudibleDialog {

        dialog.window?.apply {
            setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(android.view.Gravity.BOTTOM)
            setWindowAnimations(android.R.style.Animation_Dialog)
        }
        dialog.show()
        return this
    }

}