package com.eminsimsek.vocabularybuilderapp.service

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabase(context: Context?) : SQLiteOpenHelper(context, "VocAppDatabase.sqlite", null, 1) {


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE \"MyList\" (\n" +
                "\t\"word\"\tTEXT,\n" +
                "\t\"definition\"\tTEXT,\n" +
                "\t\"example\"\tTEXT,\n" +
                "\t\"synonyms\"\tTEXT,\n" +
                "\t\"antonyms\"\tTEXT,\n" +
                "\t\"image\"\tTEXT,\n" +
                "\t\"id\"\tINTEGER NOT NULL,\n" +
                "\tPRIMARY KEY(\"id\" AUTOINCREMENT)\n" +
                ");")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS MyList")
        onCreate(db)
    }


}