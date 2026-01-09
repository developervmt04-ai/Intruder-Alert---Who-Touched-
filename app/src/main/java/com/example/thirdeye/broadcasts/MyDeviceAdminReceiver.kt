package com.example.thirdeye.broadcasts

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import com.example.thirdeye.data.localData.IntruderSelfiePrefs
import com.example.thirdeye.service.CameraCaptureService

class MyDeviceAdminReceiver: DeviceAdminReceiver() {

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
    }

    override fun onPasswordFailed(context: Context, intent: Intent) {
        super.onPasswordFailed(context, intent)

        val selfiePrefs= IntruderSelfiePrefs(context)

       val current=selfiePrefs.incrementAttempt()

        val threshold=selfiePrefs.getWrongAttempts()

        if (current>=threshold){
            try {
                CameraCaptureService.Instance?.scheduleIntruderCapture()

            }
            catch (e: Exception){
                e.printStackTrace()
            }



        }



    }


    override fun onPasswordSucceeded(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordSucceeded(context, intent, user)
        IntruderSelfiePrefs(context).resetAttempts()
    }
}