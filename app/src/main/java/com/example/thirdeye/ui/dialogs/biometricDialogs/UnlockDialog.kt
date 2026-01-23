package com.example.thirdeye.ui.dialogs.biometricDialogs

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.thirdeye.R
import com.example.thirdeye.databinding.UnlockBiometricDialogBinding
import com.google.android.gms.ads.AdRequest

class UnlockDialog(context: Context) {

    private val dialog = Dialog(context).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private val binding =
        UnlockBiometricDialogBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)

        // load ad safely
//        binding.adView.loadAd(AdRequest.Builder().build())

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

    fun show(): UnlockDialog {
        dialog.window?.let { window ->
            val params = window.attributes

            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            params.dimAmount = 0.4f

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                params.blurBehindRadius = 24
            }

            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(Gravity.BOTTOM)
            window.setWindowAnimations(R.style.BottomDialogAnimation)

            window.attributes = params
        }

        dialog.show()
        return this
    }
}
