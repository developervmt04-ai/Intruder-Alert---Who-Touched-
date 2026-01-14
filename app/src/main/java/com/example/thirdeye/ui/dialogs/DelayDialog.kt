package com.example.thirdeye.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.thirdeye.R
import com.example.thirdeye.data.localData.DelayPrefs
import com.example.thirdeye.utils.rbUtils
import androidx.core.content.ContextCompat

object DelayDialog {

    fun showDelayDialog(context: Context, onSelect: (Long) -> Unit) {

        val prefs = DelayPrefs(context)
        val savedDelay = prefs.getCaptureDelay()

        val view = LayoutInflater.from(context).inflate(R.layout.delay_dialog, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.delayRadioGroup)
        val backBtn = view.findViewById<ImageView>(R.id.backBtn)

        val options = listOf(1000L, 2000L, 3000L, 4000L, 500L)
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
                        leftMargin = 10
                        rightMargin = 10
                        topMargin = 25
                        bottomMargin = 30
                    }
                    setBackgroundColor(ContextCompat.getColor(context, R.color.strokeColor))
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
