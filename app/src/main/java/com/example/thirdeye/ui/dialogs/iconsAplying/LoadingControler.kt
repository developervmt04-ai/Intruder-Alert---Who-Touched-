package com.example.thirdeye.ui.dialogs.iconsAplying

import android.app.AlertDialog
import android.app.Dialog
import android.widget.ImageView
import android.widget.TextView
import com.example.thirdeye.R

class LoadingControler(

    private val dialog: Dialog,
    private val statusIcon: ImageView,
    private val title: TextView,
    private val description: TextView
) {

    fun showApplied() {
        statusIcon.setImageResource(R.drawable.success)

        title.text = dialog.context.getString(R.string.congratulation)
        description.text = dialog.context.getString(R.string.success_des)

        title.postDelayed({
            dialog.dismiss()
        }, 900)
    }
}
