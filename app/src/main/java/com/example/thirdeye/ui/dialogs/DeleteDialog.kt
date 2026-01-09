package com.example.thirdeye.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import com.example.thirdeye.R
import com.example.thirdeye.data.models.IntrudersImages

object DeleteDialog {

    fun showDeleteDialog(
        context: Context,
        onYes:()-> Unit
    ){

        val view= LayoutInflater.from(context).inflate(R.layout.delete_dialog,null)
        val cancelBtn = view.findViewById<Button>(R.id.cancelBtn)
        val deleteBtn = view.findViewById<Button>(R.id.deletBtn)

        val dialog= AlertDialog.Builder(context).setView(view).setCancelable(true).create()

        cancelBtn.setOnClickListener {dialog.dismiss() }
        deleteBtn.setOnClickListener { onYes.invoke()
        dialog.dismiss()}
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)




    }
}