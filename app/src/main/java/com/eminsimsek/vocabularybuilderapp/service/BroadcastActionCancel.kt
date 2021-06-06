package com.eminsimsek.vocabularybuilderapp.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class BroadcastActionCancel: BroadcastReceiver()  {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("Action cancel is received")
        if (context != null) {
            NotificationManagerCompat.from(context).cancel(0)
        }
    }
}