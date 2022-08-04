package com.example.t06

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ViewModel(application: Application): AndroidViewModel(application) {

    var pictureList:MutableList<info> = ArrayList()
    var image1:Bitmap? = null
    lateinit var address:String
    lateinit var coordinates:String
    lateinit var date:String
    var num:Int = 0
    lateinit var lat:String
    lateinit var long:String
}