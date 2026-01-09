package com.example.thirdeye.data.localData.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thirdeye.data.models.IntruderImageMetaData
import com.example.thirdeye.data.models.IntrudersImages

@Dao
interface IntruderDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)

     suspend fun insert(intrudersImages: IntruderImageMetaData){


    }
    @Query("DELETE FROM IntruderImageMetaData WHERE id = :id")

    suspend fun delete(id: String)

    @Query("SELECT * FROM IntruderImageMetaData WHERE id = :id")
    suspend fun getById(id: String): IntruderImageMetaData?

}