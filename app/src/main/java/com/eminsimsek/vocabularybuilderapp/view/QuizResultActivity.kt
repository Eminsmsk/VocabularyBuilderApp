package com.eminsimsek.vocabularybuilderapp.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eminsimsek.vocabularybuilderapp.R
import kotlinx.android.synthetic.main.activity_quiz_result.*


class QuizResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_result)

        val points: Int = intent.getIntExtra("points", 0)
        val category = intent.getStringExtra("category").toString()

        val sp = getSharedPreferences("highestScore", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        val highestScore = sp.getInt("highestScore", 0)

        if (highestScore < points) {
            editor.putInt("highestScore", points)
            editor.apply()
        }
        textViewResultHighestScore.text = sp.getInt("highestScore", 0).toString()
        textViewResultScore.text = points.toString()

        buttonResultPlayAgain.setOnClickListener {
            val intent: Intent = Intent(applicationContext, QuizActivity::class.java)
            intent.putExtra("category", category)
            startActivity(intent)
            finish();
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}