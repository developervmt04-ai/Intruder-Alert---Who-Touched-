package com.example.thirdeye.utils

import android.content.Context
import android.content.res.ColorStateList
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import com.example.thirdeye.R

object rbUtils {
    fun applyRbStyle(context: Context,rb: RadioButton) {


        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(
                ContextCompat.getColor(context, R.color.black),
                ContextCompat.getColor(context, R.color.black_40)
            )
        )
        rb.setTextColor(colorStateList)
        CompoundButtonCompat.setButtonTintList(rb, colorStateList)
    }


}