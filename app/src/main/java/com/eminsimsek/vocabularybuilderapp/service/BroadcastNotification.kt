package com.eminsimsek.vocabularybuilderapp.service

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.webkit.URLUtil
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.eminsimsek.vocabularybuilderapp.R
import com.eminsimsek.vocabularybuilderapp.model.Word


class BroadcastNotification : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val localdb = AppDatabase(context)
        if (MyListDAO().getListSize(localdb) > 0) {

            val sharedPref: SharedPreferences? = context?.getSharedPreferences(
                "appWord",
                Context.MODE_PRIVATE
            )
            val chosenWord: Word = Word(
                sharedPref?.getString("word", "-").toString(),
                sharedPref?.getString("definition", "-").toString(),
                sharedPref?.getString("example", "-").toString(),
                sharedPref?.getString("synonyms", "-"),
                sharedPref?.getString("antonyms", "-"),
                sharedPref?.getString("image", "-")
            )

            val notificationLayout = RemoteViews(context?.packageName, R.layout.custom_notify)

            notificationLayout.setTextViewText(R.id.notifyWordName, chosenWord?.wordName)
            notificationLayout.setTextViewText(
                R.id.notifyWordDetails,
                "\nDefinition: ${chosenWord?.definition}" +
                        "\n\nExample: ${chosenWord?.example}" +
                        "\n\nSynonyms: ${chosenWord?.synonyms}" +
                        "\n\nAntonyms:${chosenWord?.antonyms}"
            )

            val broadcastIntentMemorize =
                Intent(context?.applicationContext, BroadcastActionMemorize::class.java)
            val broadcastMemorizePendingIntent: PendingIntent =
                PendingIntent.getBroadcast(
                    context?.applicationContext,
                    0,
                    broadcastIntentMemorize,
                    0
                )


            val broadcastIntentCancel =
                Intent(context?.applicationContext, BroadcastActionCancel::class.java)
            val broadcastCancelPendingIntent: PendingIntent =
                PendingIntent.getBroadcast(context?.applicationContext, 0, broadcastIntentCancel, 0)


            val builder = NotificationCompat.Builder(context!!, "MyID")
                .setSmallIcon(R.drawable.ic_baseline_favorite_24)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setContentTitle("Memorize")
                .setContentText("Word")
                .setAutoCancel(true)
                .addAction(
                    R.drawable.ic_baseline_done_24,
                    "Memorized",
                    broadcastMemorizePendingIntent
                )
                .addAction(
                    R.drawable.ic_baseline_watch_later_24,
                    "Remind Later",
                    broadcastCancelPendingIntent
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
            val notificationManager = NotificationManagerCompat.from(context)
            var imageResource: Any =
                "https://user-images.githubusercontent.com/33187905/120741518-d983de00-c4fd-11eb-8a68-f76f949fcaf0.png"

            if (!(URLUtil.isHttpUrl(chosenWord.image) or URLUtil.isHttpsUrl(chosenWord.image))) {
                try {
                    val encodeByte = Base64.decode(chosenWord.image, Base64.DEFAULT)
                    val bitmap: Bitmap =
                        BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
                    imageResource = bitmap
                } catch (e: Exception) {
                    println(e.stackTrace)
                }
            } else
                imageResource = chosenWord.image as Any

            Glide.with(context)
                .asBitmap()
                .load(imageResource)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        notificationLayout.setImageViewBitmap(R.id.notifyWordImage, resource)
                        val notification = builder.build()
                        notificationManager.notify(0, notification)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })


            println("Notification is sent!")
        } else {
            Toast.makeText(context, "No words in My List", Toast.LENGTH_SHORT).show()
        }

    }
}