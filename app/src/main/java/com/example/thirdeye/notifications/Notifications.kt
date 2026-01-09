package com.example.thirdeye.notifications
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.thirdeye.R
import com.example.thirdeye.ui.splash.SplashActivity

object Notifications {
    const val FOREGROUND_CHANNEL = "cam_channel"
    const val CHANNEL_INTRUDER = "intruder_alert"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(NotificationManager::class.java)

            val fg = NotificationChannel(
                FOREGROUND_CHANNEL,
                "Background Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val intr = NotificationChannel(
                CHANNEL_INTRUDER,
                "Intruder Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            nm?.createNotificationChannel(fg)
            nm?.createNotificationChannel(intr)
        }
    }

    private fun getPendingIntentForApp(context: Context): PendingIntent {
        val intent = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
    private fun getPendingIntentForIntruder(context: Context): PendingIntent {
        val intent = Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("openFragment", "intruder")
        }
        return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun persistentNotification(context: Context): Notification {
        return Notification.Builder(context, FOREGROUND_CHANNEL)
            .setContentTitle(context.getString(R.string.fgNotificationTitle))
            .setContentText(context.getString(R.string.fgNotifictaionText))
            .setSmallIcon(R.drawable.incognito)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setContentIntent(getPendingIntentForApp(context))
            .build()
    }

    fun intrusionNotification(context: Context): Notification {
        return Notification.Builder(context, CHANNEL_INTRUDER)
            .setContentTitle(context.getString(R.string.IntrusionNotificationTitle))
            .setContentText(context.getString(R.string.intruderNotifiationText))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(getPendingIntentForIntruder(context))
            .build()
    }
}
