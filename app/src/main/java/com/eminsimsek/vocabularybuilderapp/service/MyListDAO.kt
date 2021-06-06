package com.eminsimsek.vocabularybuilderapp.service

import android.content.ContentValues
import android.database.Cursor
import com.eminsimsek.vocabularybuilderapp.model.Word


class MyListDAO {

    fun getAllMyList(db: AppDatabase): ArrayList<Word> {
        var myList: ArrayList<Word> = ArrayList<Word>()
        val tempDB = db.readableDatabase
        val cursor: Cursor = tempDB.rawQuery("SELECT * FROM MyList", null)

        while (cursor.moveToNext()) {
            val w = Word(
                cursor.getString(cursor.getColumnIndex("word")),
                cursor.getString(cursor.getColumnIndex("definition")),
                cursor.getString(cursor.getColumnIndex("example")),
                cursor.getString(cursor.getColumnIndex("synonyms")),
                cursor.getString(cursor.getColumnIndex("antonyms")),
                cursor.getString(cursor.getColumnIndex("image"))
            )

            myList.add(w)
        }
        cursor.close()

        return myList
    }

    fun searchWord(db: AppDatabase, word: String): Boolean {
        val tempDB = db.readableDatabase
        val cursor: Cursor = tempDB.rawQuery("SELECT * FROM MyList WHERE word='$word'", null)
        return cursor.count > 0
    }

    fun updateWordImage(db: AppDatabase, word: Word, imagePath: String) {
        val tempDB = db.writableDatabase
        val contentValues: ContentValues = ContentValues()
        contentValues.put("image", imagePath);
        tempDB.update("MyList", contentValues, "word=?", arrayOf(word.wordName))
    }


    fun addWord(db: AppDatabase, word: Word): Boolean {
        if (!searchWord(db, word.wordName)) {
            val sqLiteDatabase = db.writableDatabase
            val contentValues = ContentValues()
            contentValues.put("word", word.wordName)
            contentValues.put("definition", word.definition)
            contentValues.put("example", word.example)
            if (word.synonyms.isNullOrBlank())
                contentValues.put("synonyms", "-")
            else
                contentValues.put("synonyms", word.synonyms)
            if (word.antonyms.isNullOrBlank())
                contentValues.put("antonyms", "-")
            else
                contentValues.put("antonyms", word.antonyms)
            contentValues.put("image", word.image)
            sqLiteDatabase.insertOrThrow("MyList", null, contentValues)
            sqLiteDatabase.close()
            return true
        } else
            return false
    }

    fun deleteWord(db: AppDatabase, word: String) {
        val sqLiteDatabase = db.writableDatabase
        sqLiteDatabase.delete("MyList", "word=?", arrayOf(word));
        sqLiteDatabase.close();
    }

    fun getRandomWordFromMyList(db: AppDatabase): Word? {
        var word: Word? = null
        val tempDB = db.readableDatabase
        val cursor: Cursor = tempDB.rawQuery("SELECT * FROM MyList ORDER BY random() LIMIT 1", null)
        while (cursor.moveToNext()) {
            word = Word(
                cursor.getString(cursor.getColumnIndex("word")),
                cursor.getString(cursor.getColumnIndex("definition")),
                cursor.getString(cursor.getColumnIndex("example")),
                cursor.getString(cursor.getColumnIndex("synonyms")),
                cursor.getString(cursor.getColumnIndex("antonyms")),
                cursor.getString(cursor.getColumnIndex("image"))
            )

        }

        return word
    }

    fun getListSize(db: AppDatabase): Int {
        val tempDB = db.readableDatabase
        val cursor: Cursor = tempDB.rawQuery("SELECT * FROM MyList", null)
        return cursor.count
    }

}