package com.example.thirdeye.data.encryptedStorage

import android.content.Context
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import com.example.thirdeye.constants.Constants.dirName
import com.google.common.primitives.Bytes
import kotlinx.serialization.builtins.PairSerializer
import java.io.File

class EncryptedStorageRepository(private val context: Context) {


    private fun getOrCreateDir(): File{
        val dir= File(context.filesDir,dirName)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun createMasterKey(): MasterKey{
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }
     fun saveEncryptedImage(bytes: ByteArray): Pair<File, Long>{
         val timeStamp= System.currentTimeMillis()


        val dir=getOrCreateDir()
        val file= File(dir,"intruder_${timeStamp}.jpg")
        val masterKey=createMasterKey()

        val encryptedFile= EncryptedFile.Builder(
            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()

        encryptedFile.openFileOutput().use {
            output->
            output.write(bytes)
        }

        return Pair(file,timeStamp)

    }
     fun readEncryptedImage(file: File,savedTime:Long): Pair<ByteArray,Long>{

        val masterKey=createMasterKey()
        val encryptedFile= EncryptedFile.Builder(

            context,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB

        ).build()
         val bytes=encryptedFile.openFileInput().use { it.readBytes() }
        return Pair(bytes,savedTime)
    }
    fun listOfImages(): List<File> {
        val dir = getOrCreateDir()
        return dir.listFiles { file -> file.isFile }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }
    fun deleteImage(file: File): Boolean=file.delete()




}