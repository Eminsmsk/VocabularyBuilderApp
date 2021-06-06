package com.eminsimsek.vocabularybuilderapp.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.eminsimsek.vocabularybuilderapp.R
import com.eminsimsek.vocabularybuilderapp.service.AppDatabase
import com.eminsimsek.vocabularybuilderapp.service.MyListDAO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_quiz.*
import kotlinx.android.synthetic.main.fragment_quiz.spinnerQuizCategory
import kotlinx.android.synthetic.main.fragment_words.*


class QuizFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }

    private val categories =
        arrayListOf<String>("Verbs", "Adverbs", "Adjectives", "Phrases and Idioms", "My List")
    val db = FirebaseFirestore.getInstance()
    private lateinit var categoryComboAdapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryComboAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            categories
        )
        spinnerQuizCategory.adapter = categoryComboAdapter
        buttonQuizStart.setOnClickListener {
            val chosen: String? = spinnerQuizCategory.selectedItem?.toString()
            if (!chosen.isNullOrEmpty()) {
                if (chosen.equals("My List")) {
                    var localdb: AppDatabase = AppDatabase(context)
                    if (MyListDAO().getListSize(localdb) > 0) {
                        val intent: Intent = Intent(context, QuizActivity::class.java)
                        intent.putExtra("category", chosen)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "You must have at least 10 words in My List",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val intent: Intent = Intent(context, QuizActivity::class.java)
                    intent.putExtra("category", chosen)
                    startActivity(intent)
                }
            }
        }


    }


}