package com.eminsimsek.vocabularybuilderapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eminsimsek.vocabularybuilderapp.R
import com.eminsimsek.vocabularybuilderapp.model.Word
import com.eminsimsek.vocabularybuilderapp.service.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_words.*


class WordsFragment : Fragment() {

    private val categories =
        arrayListOf<String>("Verbs", "Adverbs", "Adjectives", "Phrases and Idioms")
    val db = FirebaseFirestore.getInstance()
    private lateinit var categoryComboAdapter: ArrayAdapter<String>
    private lateinit var wordList: ArrayList<Word>
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var localdb: AppDatabase
    private var flag = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_words, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryComboAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            categories
        )
        spinnerQuizCategory.adapter = categoryComboAdapter
        wordList = ArrayList<Word>()
        localdb = AppDatabase(activity?.applicationContext)
        recyclerAdapter = RecyclerAdapter(activity?.applicationContext, wordList, localdb, this)


        val wordsRV: RecyclerView = view.findViewById(R.id.wordsRV)
        wordsRV.layoutManager = LinearLayoutManager(context)
        wordsRV.adapter = recyclerAdapter

        spinnerQuizCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //Toast.makeText(context, categories.get(position).split(" ").joinToString(""),Toast.LENGTH_LONG).show()
                if (!wordList.isEmpty()) {
                    wordList.clear()
                    recyclerAdapter.notifyDataSetChanged()
                }

                loadWords(categories.get(position).split(" ").joinToString(""))


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


    }

    fun loadWords(category: String) {

        db.collection("CATEGORIES").document("ydFXNevAhX6Id8cX2xbW").collection(category)
            .addSnapshotListener { value, error ->
                if (value != null) {

                    for (v in value.documents) {
                        wordList.add(
                            Word(
                                v.get("word").toString(),
                                v.get("definition").toString(),
                                v.get("example").toString(),
                                v.get("synonyms").toString(),
                                v.get("antonyms").toString(),
                                v.get("image").toString()
                            )
                        )
                    }


                    if (flag) {
                        recyclerAdapter =
                            RecyclerAdapter(activity?.applicationContext, wordList, localdb, this)
                        wordsRV.adapter = recyclerAdapter
                        flag = false
                    } else {
                        //recyclerAdapter.notifyItemRangeChanged(0,wordList.size)
                        recyclerAdapter.notifyDataSetChanged()

                    }


                } else {
                    error?.printStackTrace()

                }
            }

    }


}