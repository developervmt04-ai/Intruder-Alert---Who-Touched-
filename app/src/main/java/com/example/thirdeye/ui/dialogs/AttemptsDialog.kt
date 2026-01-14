package com.example.thirdeye.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.thirdeye.R
import com.example.thirdeye.data.localData.IntruderSelfiePrefs
import com.example.thirdeye.utils.rbUtils

object AttemptsDialog {

    fun showAttemptsDialog(context: Context, onSelect: (Int) -> Unit) {

        val prefs = IntruderSelfiePrefs(context)
        val savedAttempts = prefs.getWrongAttempts()

        val view = LayoutInflater.from(context).inflate(R.layout.attempts_dialog, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.attemptsRadioGroup)
        val imageView = view.findViewById<ImageView>(R.id.backBtn)

        val options = listOf(1, 2, 3, 4, 5)

        options.forEachIndexed { index, number ->
            val radioButton = RadioButton(context).apply {
                id = number
                text = " $number Wrong Attempts "
                isChecked = number == savedAttempts
            }
            rbUtils.applyRbStyle(context, radioButton)
            radioGroup.addView(radioButton)

            if (index != options.lastIndex) {
                val divider = View(context).apply {
                    layoutParams = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        1
                    ).apply {
                        leftMargin = 10
                        rightMargin = 10
                        topMargin = 25
                        bottomMargin = 30
                    }
                    setBackgroundColor(context.getColor(R.color.strokeColor))
                }
                radioGroup.addView(divider)
            }
        }


        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


        dialog.window?.attributes?.windowAnimations = R.style.EnterDialog

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            prefs.setWrongTries(checkedId)
            onSelect(checkedId)
            dialog.dismiss()
        }

        imageView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
