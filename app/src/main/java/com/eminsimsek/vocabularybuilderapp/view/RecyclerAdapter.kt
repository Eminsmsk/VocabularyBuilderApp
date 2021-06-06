package com.eminsimsek.vocabularybuilderapp.view


import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eminsimsek.vocabularybuilderapp.R
import com.eminsimsek.vocabularybuilderapp.model.Word
import com.eminsimsek.vocabularybuilderapp.service.AppDatabase
import com.eminsimsek.vocabularybuilderapp.service.CallBackInterface
import com.eminsimsek.vocabularybuilderapp.service.MyListDAO


class RecyclerAdapter(
    private val myContext: Context?,
    private var wordList: ArrayList<Word>,
    private val localdb: AppDatabase,
    private val fragment: Fragment
) : RecyclerView.Adapter<RecyclerAdapter.CardViewHolder>() {

    private var listener: CallBackInterface? = null

    class CardViewHolder : RecyclerView.ViewHolder {
        var cardView: CardView? = null
        var textViewName: TextView? = null
        var textViewDefinition: TextView? = null
        var textViewExample: TextView? = null
        var textViewSynonyms: TextView? = null
        var textViewAntonyms: TextView? = null
        var imageViewWord: ImageView? = null
        var imageViewAddMyList: ImageView? = null

        constructor(itemView: View) : super(itemView) {
            cardView = itemView.findViewById(R.id.cardView)
            textViewName = itemView.findViewById(R.id.textViewName)
            textViewDefinition = itemView.findViewById(R.id.textViewDefinition)
            textViewExample = itemView.findViewById(R.id.textViewExample)
            textViewSynonyms = itemView.findViewById(R.id.textViewSynonyms)
            textViewAntonyms = itemView.findViewById(R.id.textViewAntonyms)
            imageViewWord = itemView.findViewById(R.id.imageViewWord)
            imageViewAddMyList = itemView.findViewById(R.id.imageViewAddMyList)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view: View = LayoutInflater.from(myContext).inflate(R.layout.word_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {

        holder.textViewName?.text = wordList.get(position).wordName
        holder.textViewDefinition?.text = "Definition: " + wordList.get(position).definition
        holder.textViewExample?.text = "Example: " + wordList.get(position).example
        holder.textViewSynonyms?.text = "Synonyms: " + wordList.get(position).synonyms
        holder.textViewAntonyms?.text = "Antonyms: " + wordList.get(position).antonyms

        if (wordList.get(position).image.isNullOrEmpty())
            holder.imageViewWord?.setImageResource(R.drawable.ic_launcher_background)
        else {
            var imagePath = wordList.get(position).image!!
            if (!(URLUtil.isHttpUrl(imagePath) or URLUtil.isHttpsUrl(imagePath))) {
                try {
                    val encodeByte = Base64.decode(imagePath, Base64.DEFAULT)
                    val bitmap: Bitmap = BitmapFactory.decodeByteArray(
                        encodeByte,
                        0,
                        encodeByte.size
                    )
                    Glide.with(myContext!!).load(bitmap).into(holder.imageViewWord!!)
                } catch (e: Exception) {
                    println(e.stackTrace)
                }
            } else
                Glide.with(myContext!!).load(imagePath).into(holder.imageViewWord!!)
        }
        if (fragment is MyListFragment) {
            listener = fragment as CallBackInterface
            holder.imageViewWord?.setOnClickListener {
                listener!!.changeImage(wordList.get(position))

            }
        }

        if (MyListDAO().searchWord(localdb, wordList.get(position).wordName))
            holder.imageViewAddMyList?.setImageResource(R.drawable.ic_baseline_favorite_24)
        else
            holder.imageViewAddMyList?.setImageResource(R.drawable.ic_baseline_favorite_border_24)

        holder.imageViewAddMyList?.setOnClickListener {

            if (MyListDAO().addWord(localdb, wordList.get(position))) {
                holder.imageViewAddMyList?.setImageResource(R.drawable.ic_baseline_favorite_24)
                Toast.makeText(myContext, "Added to My List", Toast.LENGTH_SHORT).show()
            } else {
                holder.imageViewAddMyList?.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                MyListDAO().deleteWord(localdb, wordList.get(position).wordName)
                if (!(MyListDAO().getListSize(localdb) > 0)) {
                    val sharedPref: SharedPreferences? =
                        myContext?.getSharedPreferences("appWord", Context.MODE_PRIVATE)
                    with(sharedPref?.edit()) {
                        this?.putString("word", "-")
                        this?.putString("definition", "-")
                        this?.putString("example", "-")
                        this?.putString("synonyms", "-")
                        this?.putString("antonyms", "-")
                    }
                }
                Toast.makeText(myContext, "Removed from My List", Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun getItemCount(): Int {
        return wordList.size
    }

    fun updateNewsList(list: ArrayList<Word>) {


        //wordList.clear()
        this.wordList = list
        // wordList.addAll(list)

        notifyDataSetChanged()
    }


}