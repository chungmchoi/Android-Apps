package com.example.minichallenge2

import android.content.ClipData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel as ViewModel

//This class stores variable wallet and cost which will be shared and used between fragments.
class SharedViewModel : ViewModel() {
    val selected = MutableLiveData<ClipData.Item>()
    fun select(item: ClipData.Item) {
        selected.value = item
    }
    var wallet = 25.0   //Current amount in wallet.
    var cost = 0.0      //Current cost of fruits.
}