package com.example.tutorial3

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //start
        (findViewById<Button>(R.id.start)).setOnClickListener{
            slotMachineDraw((findViewById<TextView>(R.id.left)),
                    (findViewById<TextView>(R.id.middle)),
                    (findViewById<TextView>(R.id.right)))
        }

        //stop
        (findViewById<Button>(R.id.stop)).setOnClickListener{
            coroutineJob?.cancel()
        }

    }

    private val TAG = "slotmachine"
    private var coroutineJob: Job? = null
    private val delay: Long = 1_000
    private val repetitions = 10

    //return a random number between 0 to 10
    suspend fun randomInt(repetitions: Int, delay: Long, v: TextView): Int {
        var rand = -1
        for (i: Int in 0..repetitions) {
            delay(delay)
            rand = (0..10).random()
            Log.d(TAG, "Background -> " + Thread.currentThread().name)

            //updating the UI from the main thread
            withContext(Dispatchers.Main) {
                Log.d(TAG, "UI -> " + Thread.currentThread().name)
                v.text = rand.toString()

            }
        }
        return rand
    }

    fun slotMachineDraw(left: TextView, middle: TextView, right: TextView) {
        //cancel the job object in case user wants to start it again
        coroutineJob?.cancel()
        // .launch returns a Job object that we can then use to cancel the coroutine
        coroutineJob = CoroutineScope(Dispatchers.IO).launch {

            // 'async' will let us run the randInt generators concurrently.
            // notice the parameters that we are passing to randomInt()
            val leftDeffered = async {
                randomInt(repetitions, delay, left)
            }

            val middleDeffered = async {
                randomInt(repetitions, delay, middle)
            }

            val rightDeffered = async {
                randomInt(repetitions, delay, right)
            }

            // 'await' is needed to retrieve the final result once it is available
            val left_final = leftDeffered.await()
            val middle_final = middleDeffered.await()
            val right_final = rightDeffered.await()
            // we can display the final values with hyphens on both ends
            withContext(Dispatchers.Main) {

                Log.d(TAG, "Final values UI -->" + Thread.currentThread().name)

                left.text = "-" + left_final + "-"
                middle.text = "-" + middle_final + "-"
                right.text = "-" + right_final + "-"
            }
        }
    }
}