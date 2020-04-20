package com.madlab.alarammanagerdemo.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.madlab.alarammanagerdemo.ConstantResource
import com.madlab.alarammanagerdemo.MainActivity
import com.madlab.alarammanagerdemo.service.TonePlayingService


class AlarmReceiver : BroadcastReceiver() {

    var mainActivity: MainActivity? = null

    fun callMust(main: MainActivity) {
        mainActivity = main
    }


    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.getIntExtra(
                ConstantResource.KEY_ALARM,
                -1
            ) == ConstantResource.REQUEST_CODE_ALARM
        ) {
            Toast.makeText(context, "Alarm On", Toast.LENGTH_LONG).show()
            context?.startService(
                Intent(context, TonePlayingService::class.java)
            )
        }

        if (intent?.getIntExtra(
                ConstantResource.KEY_SNOOZE,
                -1
            ) == ConstantResource.REQUEST_CODE_SNOOZE
        ) {
            Toast.makeText(context, "Your Alarm Snooze", Toast.LENGTH_SHORT).show()
            context?.stopService(Intent(context, TonePlayingService::class.java))
            mainActivity?.snoozeAlarm()
        }

        if (intent?.getIntExtra(
                ConstantResource.KEY_CANCEL,
                -1
            ) == ConstantResource.REQUEST_CODE_CANCEL
        ) {
            Toast.makeText(context, "Your Alarm Cancel", Toast.LENGTH_SHORT).show()
            context?.stopService(Intent(context, TonePlayingService::class.java))
        }

    }
}