package com.eminsimsek.vocabularybuilderapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.eminsimsek.vocabularybuilderapp.R
import com.eminsimsek.vocabularybuilderapp.model.Word
import com.eminsimsek.vocabularybuilderapp.service.AppDatabase
import com.eminsimsek.vocabularybuilderapp.service.MyListDAO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_quiz.*
import java.util.*
import kotlin.collections.ArrayList

class QuizActivity : AppCompatActivity() {

    private lateinit var category: String
    private var count: Int = 0
    private var question: Int = 0;
    private lateinit var categoryWords: ArrayList<Word>
    private lateinit var options: ArrayList<String>
    private lateinit var localdb: AppDatabase
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        category = intent.getStringExtra("category").toString()
        localdb = AppDatabase(applicationContext)
        categoryWords = ArrayList<Word>()
        options = ArrayList<String>()
        loadQuizWords()


        buttonOptionA.setOnClickListener {
            checkAnswer(buttonOptionA.text.toString())
        }
        buttonOptionB.setOnClickListener {
            checkAnswer(buttonOptionB.text.toString())
        }
        buttonOptionC.setOnClickListener {
            checkAnswer(buttonOptionC.text.toString())
        }
        buttonOptionD.setOnClickListener {
            checkAnswer(buttonOptionD.text.toString())
        }

    }

    private fun loadQuizWords() {
        if (categoryWords.size > 0)
            categoryWords.clear()
        if (category.equals("My List")) {
            categoryWords.addAll(MyListDAO().getAllMyList(localdb))
        } else {
            val words: ArrayList<Word> = ArrayList<Word>()
            db.collection("CATEGORIES")
                .document("ydFXNevAhX6Id8cX2xbW")
                .collection(category.split(" ").joinToString(""))
                .get().addOnSuccessListener {
                    for (v in it.documents) {
                        categoryWords.add(
                            Word(
                                v.get("word").toString(),
                                v.get("definition").toString(),
                                v.get("example").toString(),
                                v.get("synonyms").toString(),
                                v.get("antonyms").toString()
                            )
                        )
                    }

                    categoryWords.shuffle()
                    loadQuestion()
                }.addOnFailureListener { exception ->
                    println(exception.stackTrace)
                }

            /*
        .addSnapshotListener { value, error ->
            if (value != null) {

                for (v in value.documents){
                    categoryWords.add(Word(v.get("word").toString()
                        ,v.get("definition").toString()
                        ,v.get("example").toString()
                        ,v.get("synonyms").toString()
                        ,v.get("antonyms").toString()))
                }
                println(categoryWords.size)
            }
    }*/

        }

        //

    }

    private fun loadQuestion() {
        if (!options.isEmpty())
            options.clear()

        while (options.size < 3) {
            val rand: Random = Random()
            val randomNum: Int = rand.nextInt(categoryWords.size)
            if (!options.contains(categoryWords[randomNum].wordName) && !categoryWords[question].wordName.equals(
                    categoryWords[randomNum].wordName
                )
            ) {
                options.add(categoryWords[randomNum].wordName);

            }
        }

        options.add(categoryWords[question].wordName)
        options.shuffle()
        println("cevap: " + options.size.toString())
        buttonOptionA.text = options[0]
        buttonOptionB.text = options[1]
        buttonOptionC.text = options[2]
        buttonOptionD.text = options[3]
        textViewQuestion.text = categoryWords.get(question).definition


        question += 1;
    }

    private fun checkAnswer(answer: String) {
        if (categoryWords.get(question - 1).wordName.equals(answer)) {
            count += 1;
            textViewQuizPoints.text = count.toString()
            countConsecutive()
        } else {
            val intent: Intent = Intent(applicationContext, QuizResultActivity::class.java)
            intent.putExtra("points", count)
            intent.putExtra("category", category)
            startActivity(intent)
            finish();
        }
    }

    private fun countConsecutive() {
        if (count == 10) {
            val intent: Intent = Intent(applicationContext, QuizResultActivity::class.java)
            intent.putExtra("points", count)
            intent.putExtra("category", category)
            startActivity(intent)
            finish()
        } else {
            loadQuestion()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}