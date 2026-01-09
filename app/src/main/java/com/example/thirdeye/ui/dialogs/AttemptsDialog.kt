package com.example.thirdeye.ui.dialogs

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.thirdeye.R
import com.example.thirdeye.R.color.dividerColor
import com.example.thirdeye.data.localData.IntruderSelfiePrefs
import com.example.thirdeye.utils.rbUtils

object AttemptsDialog {

    fun showAttemptsDialog(context: Context, onSelect: (Int) -> Unit) {

        val prefs = IntruderSelfiePrefs(context)
        val savedAttempts = prefs.getWrongAttempts()

        val view = LayoutInflater.from(context).inflate(R.layout.attempts_dialog, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.attemptsRadioGroup)
        val imageView= view.findViewById<ImageView>(R.id.backBtn)



        val options = listOf(1, 2, 3, 4,5,6)


        options.forEachIndexed { index, number ->

            val radioButton = RadioButton(context).apply {

                id = number
                text = " $number Wrong Attempts "

                isChecked = number == savedAttempts
            }
            rbUtils.applyRbStyle(context,radioButton)

            radioGroup.addView(radioButton)

            if (index != options.lastIndex) {
                val divider = View(context).apply {
                    layoutParams = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        1
                    ).apply {
                        topMargin = 25
                        bottomMargin = 25
                    }
                    setBackgroundColor(context.getColor(R.color.strokeColor))
                }

                radioGroup.addView(divider)
            }
        }




        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)



        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            prefs.setWrongTries(checkedId)
            onSelect(checkedId)
            dialog.dismiss()
        }

        dialog.show()
        imageView.setOnClickListener {
            dialog.dismiss()


        }
    }

}
