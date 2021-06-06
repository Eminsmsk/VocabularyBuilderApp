package com.eminsimsek.vocabularybuilderapp.model

import java.io.Serializable

data class Word(val wordName:String,val definition:String,val example:String,val synonyms:String?, val antonyms:String?, val image:String? = null) : Serializable
