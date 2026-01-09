package com.example.thirdeye.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.thirdeye.R


object IconApplyingDialog {
    fun showIconApplyingDialog(
        context: Context
    ){

        val view= LayoutInflater.from(context).inflate(R.layout.icon_apply_dialog,null)

        val dialog= AlertDialog.Builder(context).setView(view).setCancelable(true).create()
        dialog.show()


    }
}