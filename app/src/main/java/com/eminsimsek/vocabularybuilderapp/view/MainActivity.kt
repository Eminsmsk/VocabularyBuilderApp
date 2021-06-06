package com.eminsimsek.vocabularybuilderapp.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.eminsimsek.vocabularybuilderapp.R
import com.eminsimsek.vocabularybuilderapp.service.AppDatabase
import com.eminsimsek.vocabularybuilderapp.service.CallBackInterface
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity() : AppCompatActivity() {

    private lateinit var localdb: AppDatabase
    lateinit var tempFragment: Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createNotificationChannel()
        tempFragment = HomeFragment()
        supportFragmentManager.beginTransaction().add(R.id.fragmentHolder, tempFragment).commit()
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNav)
        bottomNav.setOnNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.itemHome -> tempFragment = HomeFragment()
                R.id.itemMyList -> tempFragment = MyListFragment()
                R.id.itemWords -> tempFragment = WordsFragment()
                R.id.itemQuiz -> tempFragment = QuizFragment()

            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, tempFragment).commit()



            true
        }


    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel: NotificationChannel = NotificationChannel(
                "MyID",
                "AppNotification",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Application Notificiation"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }


}