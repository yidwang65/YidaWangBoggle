package com.example.boggle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.max

class SharedViewModel : ViewModel() {
    val refreshLiveData = MutableLiveData<Boolean>()
    val scoreLiveDate = MutableLiveData<Int>()
    fun setNewGame(refresh: Boolean) {
        refreshLiveData.value = refresh
    }
    fun setScore(score: Int){
        scoreLiveDate.value = score
    }
}