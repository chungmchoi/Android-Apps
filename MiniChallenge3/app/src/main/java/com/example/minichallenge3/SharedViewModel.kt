package com.example.minichallenge3

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.*

class SharedViewModel : ViewModel(){

    //Values used for this class.
    private val valuesLiveData = MutableLiveData<List<Int>>()
    private val valuesLocal = mutableListOf(0, 0, 0)
    private val delay: Long=1_000
    private val repetitions = 30
    private lateinit var viewModelJob: Job
    //Mandatory scope for ensure friendly behavior
    private lateinit var ioScope: CoroutineScope

    //Initializing: assigning viewModelJob, ioScope, valuesLiveData.value
    init {
        viewModelJob = Job()
        ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)
        valuesLiveData.value = valuesLocal
    }

    //return random number between 0 to 10
    suspend fun randomInt(repetitions: Int, delay: Long, index: Int): Int {
        var rand = -1
        for(i: Int in 0..repetitions){
            delay(delay)
            rand = (0..10).random()
            Log.d("coroutine", "Hello from background thread"+Thread.currentThread().name)

            valuesLocal[index] = rand   //Putting random number in array.
            valuesLiveData.postValue(valuesLocal)   //Triggers updates
        }
        return rand
    }

    //Used to tell the program to pause generating random numbers.
    fun stop() {
        viewModelJob.cancel()
        viewModelJob = Job()
        ioScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    }

    //Used as a getter in the fragments to obtain the random numbers generated.
    fun getValues(): MutableLiveData<List<Int>>{
        return valuesLiveData
    }

    //Stores randomly generated numbers into arrays.
    fun slotMachineDraw()   {

        ioScope.launch {

            val leftDeffered = async {
                randomInt(repetitions, delay, 0)
            }
            val middleDefferred = async {
                randomInt(repetitions, delay, 1)
            }
            val rightDefferred = async {
                randomInt(repetitions, delay, 2)
            }

            valuesLocal[0] = leftDeffered.await()
            valuesLocal[1] = middleDefferred.await()
            valuesLocal[2] = rightDefferred.await()
            valuesLiveData.postValue(valuesLocal)
        }
    }
}











