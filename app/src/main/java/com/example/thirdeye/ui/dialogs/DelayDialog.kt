package com.example.thirdeye.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.thirdeye.R
import com.example.thirdeye.data.localData.DelayPrefs
import com.example.thirdeye.utils.rbUtils

object DelayDialog {

    fun DelayDialog(context: Context, onSelect: (Long) -> Unit) {

        val prefs = DelayPrefs(context)
        val savedDelay = prefs.getCaptureDelay()


        val view = LayoutInflater.from(context).inflate(R.layout.delay_dialog, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.delayRadioGroup)
        val backBtn = view.findViewById<ImageView>(R.id.backBtn)


        val options = listOf(1000L, 2000L, 3000L, 4000L,500L)
        options.forEachIndexed { index, number ->
            val radioButton = RadioButton(context).apply {
                id = number.toInt()
                text = "$number ms"
                isChecked = number == savedDelay
                tag = number
            }
            rbUtils.applyRbStyle(context, radioButton)
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
                    setBackgroundColor(ContextCompat.getColor(context, R.color.strokeColor))
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
            val selectedButton = group.findViewById<RadioButton>(checkedId)
            val selectedValue = selectedButton.tag as Long
            prefs.setPictureDelay(selectedValue)
            onSelect(selectedValue)
            dialog.dismiss()
        }


        backBtn.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}
