package com.example.t05

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ViewModel(application: Application): AndroidViewModel(application) {

    //View model variables used in this app.
    var backgroundState = 1
    var songState1 = 1
    var songState2 = 1
    var songState3 = 1
    var playState = 0
}