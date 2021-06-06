package com.eminsimsek.vocabularybuilderapp.view


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.eminsimsek.vocabularybuilderapp.R
import com.eminsimsek.vocabularybuilderapp.model.Word
import com.eminsimsek.vocabularybuilderapp.service.AppDatabase
import com.eminsimsek.vocabularybuilderapp.service.BroadcastNotification
import com.eminsimsek.vocabularybuilderapp.service.MyListDAO
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var localdb: AppDatabase
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localdb = AppDatabase(context)
        swipeRefresh = view.findViewById(R.id.swipeRefreshHome)
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.setRefreshing(false)
            refresh()
        }
        swipeRefresh.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        sharedPref = context!!.getSharedPreferences("appWord", Context.MODE_PRIVATE)
        val chosenWord: Word = getWord()

        textViewHomeWordName.text = chosenWord.wordName
        textViewHomeDefinition.text = chosenWord.definition
        textViewHomeExample.text = chosenWord.example
        textViewHomeSynonyms.text = chosenWord.synonyms
        textViewHomeAntonyms.text = chosenWord.antonyms


        buttonNotify.setOnClickListener {

            if (MyListDAO().getListSize(localdb) > 0) {
                val sharedPref = context?.getSharedPreferences("appWord", Context.MODE_PRIVATE)
                with(sharedPref?.edit()) {


                    val chosenWord: Word? = MyListDAO().getRandomWordFromMyList(localdb)
                    this?.putString("word", chosenWord?.wordName)
                    this?.putString("definition", chosenWord?.definition)
                    this?.putString("example", chosenWord?.example)
                    this?.putString("synonyms", chosenWord?.synonyms)
                    this?.putString("antonyms", chosenWord?.antonyms)
                    this?.putString("image", chosenWord?.image)

                    this?.apply()
                    textViewHomeWordName.text = chosenWord?.wordName
                    textViewHomeDefinition.text = chosenWord?.definition
                    textViewHomeExample.text = chosenWord?.example
                    textViewHomeSynonyms.text = chosenWord?.synonyms
                    textViewHomeAntonyms.text = chosenWord?.antonyms

                }


                val intent: Intent =
                    Intent(activity?.applicationContext, BroadcastNotification::class.java)


                val pendingIntent: PendingIntent =
                    PendingIntent.getBroadcast(activity?.applicationContext, 0, intent, 0)
                val alarmManager: AlarmManager =
                    activity?.applicationContext?.getSystemService(ALARM_SERVICE) as AlarmManager

                // Set the alarm to start at approximately 1:00 a.m.
                val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, 1)
                    set(Calendar.MINUTE, 0)
                }

                // the alarm repeats twice a day. at 1:00am and 13:00pm.
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_HALF_DAY,
                    pendingIntent
                )

            } else {
                Toast.makeText(context, "No words in My List", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun getWord(): Word {

        val chosenWord: Word = Word(
            sharedPref.getString("word", "-").toString(),
            sharedPref.getString("definition", "-").toString(),
            sharedPref.getString("example", "-").toString(),
            sharedPref.getString("synonyms", "-"),
            sharedPref.getString("antonyms", "-")
        )
        return chosenWord
    }

    private fun refresh() {

        val chosenWord: Word = getWord()
        textViewHomeWordName.text = chosenWord.wordName
        textViewHomeDefinition.text = chosenWord.definition
        textViewHomeExample.text = chosenWord.example
        textViewHomeSynonyms.text = chosenWord.synonyms
        textViewHomeAntonyms.text = chosenWord.antonyms

    }


}