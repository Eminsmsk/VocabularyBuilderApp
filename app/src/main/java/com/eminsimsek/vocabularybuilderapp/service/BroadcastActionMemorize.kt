package com.eminsimsek.vocabularybuilderapp.service


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.eminsimsek.vocabularybuilderapp.model.Word
import com.eminsimsek.vocabularybuilderapp.view.HomeFragment
import com.eminsimsek.vocabularybuilderapp.view.MainActivity
import kotlinx.android.synthetic.main.fragment_home.*

class BroadcastActionMemorize : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("Action memorize is received")
        val localdb = AppDatabase(context)
        val sharedPref: SharedPreferences? =
            context?.getSharedPreferences("appWord", Context.MODE_PRIVATE) ?: return
        with(sharedPref?.edit()) {
            MyListDAO().deleteWord(localdb, sharedPref?.getString("word", "-").toString())
            if (MyListDAO().getListSize(localdb) > 0) {
                val chosenWord: Word? = MyListDAO().getRandomWordFromMyList(localdb)
                this?.putString("word", chosenWord?.wordName)
                this?.putString("definition", chosenWord?.definition)
                this?.putString("example", chosenWord?.example)
                this?.putString("synonyms", chosenWord?.synonyms)
                this?.putString("antonyms", chosenWord?.antonyms)
                this?.putString("image", chosenWord?.image)
                //this?.putBoolean("flag", false)
                this?.apply()
            } else {
                Toast.makeText(context, "No words in My List remained", Toast.LENGTH_SHORT).show()
                this?.putString("word", "-")
                this?.putString("definition", "-")
                this?.putString("example", "-")
                this?.putString("synonyms", "-")
                this?.putString("antonyms", "-")
                this?.apply()
            }


        }


        //remove notification
        NotificationManagerCompat.from(context).cancel(0);

    }
}