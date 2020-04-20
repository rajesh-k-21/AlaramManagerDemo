package com.madlab.alarammanagerdemo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.madlab.alarammanagerdemo.broadcast.AlarmReceiver
import com.madlab.alarammanagerdemo.service.TonePlayingService
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

open class MainActivity : AppCompatActivity() {

    private val alarmManager: AlarmManager by lazy {
        getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private val calendar by lazy {
        Calendar.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AlarmReceiver().callMust(this)

        buttonAlarmStart.setOnClickListener {
            startAlarm()
        }
        buttonAlarmStop.setOnClickListener {
            stopAlarm()
        }
    }

    private fun startAlarm() {
        Toast.makeText(this, "Alarm is On Now", Toast.LENGTH_SHORT).show()

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                calendar.set(Calendar.MINUTE, timePicker.minute)
            }
            else -> {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.currentHour)
                calendar.set(Calendar.MINUTE, timePicker.currentMinute)
            }
        }

        val pendingIntent =
            PendingIntent.getBroadcast(
                this,
                200,
                Intent(this, AlarmReceiver::class.java).putExtra(
                    ConstantResource.KEY_ALARM,
                    ConstantResource.REQUEST_CODE_ALARM
                ),
                0
            )

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    @SuppressLint("ShortAlarm")
    fun snoozeAlarm() {

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, AlarmReceiver::class.java).putExtra(
                ConstantResource.KEY_ALARM,
                ConstantResource.REQUEST_CODE_ALARM
            ),
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis + 60 * 1000,
            pendingIntent
        )
        Toast.makeText(this, "Alarm is snooze for 5 minutes", Toast.LENGTH_SHORT).show()

    }

    private fun stopAlarm() {
        Toast.makeText(this, "Alarm is stop", Toast.LENGTH_SHORT).show()

        alarmManager.cancel(
            PendingIntent.getBroadcast(
                this, 200,
                Intent(this, AlarmReceiver::class.java), 0
            )
        )

        stopService(Intent(this, TonePlayingService::class.java))
    }
}
