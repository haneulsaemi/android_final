package com.example.walkinghadang

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        createNotificationChannel(context)

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val soundEnabled = prefs.getBoolean("alarm_sound_enabled", true)
        val vibrationEnabled = prefs.getBoolean("alarm_vibration_enabled", true)

        val notification = NotificationCompat.Builder(context, "alarm_channel")
            .setSmallIcon(R.drawable.ic_notify)
            .setContentTitle("혈당 기록 알림")
            .setContentText("설정하신 시간입니다. 혈당을 기록해 주세요.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        if (!soundEnabled) {
            notification.setSound(null)
        }

        if (vibrationEnabled) {
            notification.setVibrate(longArrayOf(0, 300, 200, 300)) // 진동 패턴
        } else {
            notification.setVibrate(null)
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
            NotificationManagerCompat.from(context).notify(notificationId, notification.build())
        } else {
            // Permission not granted, optionally handle fallback or log it
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "혈당 알림 채널"
            val descriptionText = "혈당 기록을 알리기 위한 채널"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarm_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
