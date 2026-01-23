package com.example.thirdeye.ui.dialogs.addWidget

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.example.thirdeye.R
import com.example.thirdeye.databinding.HowtoAdWidgetBinding
import com.example.thirdeye.ui.dialogs.AudibleDialog

class HowToAdWidgetLottieDialog(context: Context) {
    val dialog= Dialog(context).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    val binding= HowtoAdWidgetBinding.inflate(LayoutInflater.from(context))
    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        binding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        binding.addWidget.setOnClickListener {


        }
    }


    fun show(): HowToAdWidgetLottieDialog{

        dialog.window.let {window ->


            window?.setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setGravity(android.view.Gravity.BOTTOM)
            window?.setWindowAnimations(R.style.BottomDialogAnimation)

        }
        dialog.show()
        return this


    }


}