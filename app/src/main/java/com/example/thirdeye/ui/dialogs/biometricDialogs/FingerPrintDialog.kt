package com.example.thirdeye.ui.dialogs.biometricDialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.example.thirdeye.R
import com.example.thirdeye.databinding.BiometricDialogBinding

class FingerPrintDialog (context: Context){

    private val dialog= Dialog(context)
    private val binding= BiometricDialogBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        binding.closeButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.attributes?.windowAnimations = R.style.DialogSlideAnimation
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


    }

    fun setTitle(title:String): FingerPrintDialog{
        binding.titleText.text=title
        return this

    }
    fun setMessage(msg:String): FingerPrintDialog{
        binding.descriptionText.text=msg
        return this

    }
    fun onOk(action:()->Unit): FingerPrintDialog{
        binding.AdBiometric.setOnClickListener {

                action()
            dialog.dismiss()
        }

        return this

    }

    fun show(){
        dialog.show()
    }



}