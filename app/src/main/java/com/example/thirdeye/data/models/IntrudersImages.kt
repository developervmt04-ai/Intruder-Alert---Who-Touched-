package com.example.thirdeye.data.models

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.sql.Timestamp

@kotlinx.android.parcel.Parcelize
data class IntrudersImages(
    @PrimaryKey  val id: String,
    val bitmap: Bitmap,
    val isLocked: Boolean,
    val file: File,
    val timeStamp: Long
): Parcelable
