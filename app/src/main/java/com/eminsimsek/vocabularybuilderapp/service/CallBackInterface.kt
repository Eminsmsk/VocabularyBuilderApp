package com.eminsimsek.vocabularybuilderapp.service

import com.eminsimsek.vocabularybuilderapp.model.Word

interface CallBackInterface {
    fun changeImage(word: Word)
}