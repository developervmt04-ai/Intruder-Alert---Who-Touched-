package com.example.thirdeye.ui.intruders

import com.example.thirdeye.data.localData.db.IntruderDao
import com.example.thirdeye.data.models.IntruderImageMetaData
import com.example.thirdeye.data.models.IntrudersImages

class IntruderRepo( private val dao : IntruderDao) {

 suspend fun addImageData(images: IntruderImageMetaData){
        dao.insert(images)


    }
  suspend  fun deleteData(images: String){
        dao.delete(images)


    }
    suspend fun getMeta(id: String): IntruderImageMetaData? {
        return dao.getById(id)
    }



}