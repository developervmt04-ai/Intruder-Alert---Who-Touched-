package com.example.thirdeye.ui.dialogs.iconsAplying

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import com.example.thirdeye.R

object IconApplyingDialog {

    fun showIconApplyingDialog(context: Context): LoadingControler {

        // Inflate the custom layout
        val view = LayoutInflater.from(context)
            .inflate(R.layout.icon_apply_dialog, null)

        val statusIcon = view.findViewById<ImageView>(R.id.statusIcon)
        val title = view.findViewById<TextView>(R.id.loadingtitle)
        val description = view.findViewById<TextView>(R.id.descriptionloading)


        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()

        return LoadingControler(
            dialog,
            statusIcon,
            title,
            description
        )
    }
}