package com.example.thirdeye.ui.dialogs.addWidget


import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.example.thirdeye.R
import com.example.thirdeye.databinding.GeneralDialogBinding

class AddWidgetDialog(context: Context) {

    val dialog = Dialog(context).apply {
        window?.setBackgroundDrawableResource(R.color.transparent)
    }
    val binding = GeneralDialogBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)


        binding.closeButton.setOnClickListener {
            dialog.dismiss()


        }

        binding.howToAdd.setOnClickListener {

            


        }
    }

    fun setTitle(title: String): AddWidgetDialog {
        binding.title.text = title
        return this


    }

    fun setDescription(msg: String): AddWidgetDialog {
        binding.description.text = msg
        return this


    }

    fun onClick(action: () -> Unit): AddWidgetDialog {
        binding.addWidget.setOnClickListener {
            action()
            dialog.dismiss()


        }
        return this
    }

    fun show(): AddWidgetDialog {
        dialog.show()

        dialog.window?.apply {
            setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(android.view.Gravity.BOTTOM)
            setWindowAnimations(R.style.BottomDialogAnimation)
        }

        return this
    }


}