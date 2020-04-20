package com.madlab.alarammanagerdemo.service

import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.madlab.alarammanagerdemo.ConstantResource
import com.madlab.alarammanagerdemo.R
import com.madlab.alarammanagerdemo.broadcast.AlarmReceiver

class TonePlayingService : Service() {

    private val notificationManager: NotificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val uri: Uri by lazy {
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    }
    private val ringtone: Ringtone by lazy {
        RingtoneManager.getRingtone(this, uri)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(data: Intent?, flags: Int, startId: Int): Int {
        ringtone.play()
        createNotification()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone.stop()
        notificationManager.cancel(1)
    }

    private fun createNotification() {

        val channelId =
            "${applicationContext.packageName}-${applicationContext.getString(R.string.app_name)}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                "Replay Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.WHITE
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)

            val pendingIntent: PendingIntent =
                Intent(this, TonePlayingService::class.java).let {
                    PendingIntent.getActivity(this, 0, it, 0)
                }

            val cancelPendingIntent = PendingIntent.getBroadcast(
                this,
                ConstantResource.REQUEST_CODE_CANCEL,
                Intent(this, AlarmReceiver::class.java).putExtra(
                    ConstantResource.KEY_CANCEL,
                    ConstantResource.REQUEST_CODE_CANCEL
                ), PendingIntent.FLAG_UPDATE_CURRENT
            )

            val snoozePendingIntent = PendingIntent.getBroadcast(
                this,
                ConstantResource.REQUEST_CODE_SNOOZE,
                Intent(this, AlarmReceiver::class.java).putExtra(
                    ConstantResource.KEY_SNOOZE,
                    ConstantResource.REQUEST_CODE_SNOOZE
                ), PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification =
                NotificationCompat.Builder(this, channelId)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText("Alarm starting")
                    .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                    .setContentIntent(pendingIntent)
                    .addAction(
                        R.drawable.ic_notifications_active_black_24dp,
                        "Cancel",
                        cancelPendingIntent
                    )
                    .addAction(
                        R.drawable.ic_notifications_active_black_24dp,
                        "Snooze",
                        snoozePendingIntent
                    )
                    .build()

            startForeground(1, notification)
        }
    }
}
