package com.example.thirdeye.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import com.example.thirdeye.R
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
        dialog.window?.let { window ->
            val params = window.attributes


            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            params.dimAmount = 0.4f

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                params.blurBehindRadius = 24
            }


            window.setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(android.view.Gravity.BOTTOM)
            window.setWindowAnimations(R.style.BottomDialogAnimation)


            window.attributes = params
        }

        dialog.show()
        return this
    }
}