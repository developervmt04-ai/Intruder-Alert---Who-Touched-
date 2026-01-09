package com.example.thirdeye.ui.widget

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object widgetImage {

    fun saveWidgetBitmap(context: Context,bitmap: Bitmap){
        val file= File(context.cacheDir,"widget_intruder.png")
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG,90,it)
        }
    }
}