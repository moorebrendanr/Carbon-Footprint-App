package com.spice.carboncrushersrefactor.receivers

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.spice.carboncrushersrefactor.R
import com.spice.carboncrushersrefactor.screens.MainActivity

class NotificationSender : BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "reminders"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val startupIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(startupIntent)
            getPendingIntent(33, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_daily_log)
                .setContentTitle("Daily Log Reminder")
                .setContentText("It's time to fill out your daily log!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        with(NotificationManagerCompat.from(context)) {
            notify(66, builder.build())
        }
    }
}
