package com.example.thirdeye.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IntruderImageMetaData(
    @PrimaryKey  val id:String,
    val timeStamp:Long
)
