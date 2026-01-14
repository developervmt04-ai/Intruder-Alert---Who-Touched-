package com.example.thirdeye.ui.dialogs.biometricDialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.thirdeye.R
import com.example.thirdeye.databinding.UnlockBiometricDialogBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class UnlockDialog(context: Context) {

    private val dialog = Dialog(context, com.example.thirdeye.R.style.FullScreenDialog)

    val ad = dialog.findViewById<AdView>(R.id.adView)

    val binding = UnlockBiometricDialogBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)




        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.attributes?.windowAnimations =
            com.example.thirdeye.R.style.DialogSlideAnimation

        binding.cancelButton.setOnClickListener {
            dialog.dismiss()
            (context as? AppCompatActivity)?.finishAffinity()
        }
    }

    fun setTitle(title: String): UnlockDialog {
        binding.titleText.text = title
        return this
    }

    fun setDescription(desc: String): UnlockDialog {
        binding.descriptionText.text = desc
        return this
    }

    fun onClick(action: () -> Unit): UnlockDialog {
        binding.fingerprintContainer.setOnClickListener {
            action()
        }
        return this
    }

    fun show() {
        dialog.show()
    }
}
