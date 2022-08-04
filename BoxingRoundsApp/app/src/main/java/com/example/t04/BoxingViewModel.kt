package com.example.t04

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class BoxingViewModel(application: Application): AndroidViewModel(application){

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////
    //Values used to get live data.
    private val valuesLiveData = MutableLiveData<List<Int>>()
    val valuesLocal = mutableListOf(0, 0)
    private val delay: Long=1_000
    private lateinit var viewModelJob: Job

    //Mandatory scope for ensure friendly behavior
    private lateinit var ioScope: CoroutineScope

    /**
     * num used by timer to repeat the loop.
     * state is used like a boolean value to check if timer is on/off.
     * count represents the lap number
     * arraylist stores the current lap number.
     */
    var num = 1
    var state = 0
    var arraylist = ArrayList<BoxingData>()
    var roundNumber = 1
    var roundLength:Int = 5
    var restLength:Int = 1
    var currentRound1:Int = 1
    var secondTime = -1
    var subTime = -1
    var startState:Int = 0
    var sdf = SimpleDateFormat("M/dd/yyyy hh:mm:ss")
    var currentDate = sdf.format(Date())
    var message = 0
    var pauseState = 0
    var soundId = 1
    var arraylist2 = ArrayList<BoxingData>()
    lateinit var empty: List<BoxingData>
    //Initializing: assigning viewModelJob, ioScope, valuesLiveData.value
    init {
        viewModelJob = Job()
        ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)
        valuesLiveData.value = valuesLocal
    }

    /**
     * Timer that displays seconds and minutes.
     * Timer runs until the stop button is pressed or app ends.
     */
    suspend fun timer(delay: Long): Int {

        while (num > -1)    {
            delay(delay)
            //My addition

            valuesLocal[0] += 1
            //60 seconds = 1 minute.
            if (valuesLocal[0] == 60)   {
                valuesLocal[1] += 1
                valuesLocal[0] -= 60
            }
            valuesLiveData.postValue(valuesLocal)   //Triggers updates
        }
        return 1
    }

    //Used to tell the program to pause timer.
    fun stop() {
        viewModelJob.cancel()
        viewModelJob = Job()
        ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    }

    //resets the timer to 00:00.
    fun resetTime() {
        valuesLocal[0] = 0
        valuesLocal[1] = 0
    }

    //Used as a getter in the fragments to obtain the random numbers.
    fun getValues(): MutableLiveData<List<Int>> {
        return valuesLiveData
    }

    //Starts the timer
    fun startTimer() {
        ioScope.launch {
            val delay1 = async {
                timer(delay)

            }
            valuesLocal[0] = delay1.await()
            valuesLiveData.postValue(valuesLocal)
        }
    }



}