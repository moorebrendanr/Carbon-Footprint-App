package com.spice.carboncrushersrefactor.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.spice.carboncrushersrefactor.Constants
import com.spice.carboncrushersrefactor.SharedPreferencesHelper

class AlarmRestarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("debugging", "received BOOT_COMPLETED")
        val pHelper = SharedPreferencesHelper(context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE))
        val time = pHelper.reminderTime
        if (time != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val newIntent = Intent(context, NotificationSender::class.java)
            val alarmIntent = PendingIntent.getBroadcast(context, 1, newIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager?.setInexactRepeating(
                    AlarmManager.RTC,
                    time,
                    AlarmManager.INTERVAL_DAY,
                    alarmIntent
            )
        }
    }
}
