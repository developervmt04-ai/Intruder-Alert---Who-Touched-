package com.example.thirdeye.permissions

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import com.example.thirdeye.broadcasts.MyDeviceAdminReceiver

class DeviceAdminManager(private val activity: Activity) {

    fun isDeviceAdminActive():Boolean{
        val dpm=activity.getSystemService(DevicePolicyManager::class.java)
        val component= ComponentName(activity, MyDeviceAdminReceiver::class.java)
        return dpm.isAdminActive(component)



    }
     fun requestDeviceAdmin(){
        val intent= Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                ComponentName(activity, MyDeviceAdminReceiver::class.java)

            )
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Allow app to monitor failed unlock  Attempts"
            )
        }
        activity.startActivity(intent)


    }



}