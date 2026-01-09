package com.example.thirdeye.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import java.io.File

object ShareHelper {

    fun shareFile(
        context: Context,
        mimeType:String,
        bitmap: Bitmap,
        chooserTitle: String
    ) {
        val tempFile = File(
            context.cacheDir,
            "share_${System.currentTimeMillis()}.jpg"
        )

        tempFile.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(intent, chooserTitle)
        )
    }
}
