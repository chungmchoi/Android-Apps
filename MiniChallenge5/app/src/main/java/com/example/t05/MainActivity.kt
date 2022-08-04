package com.example.t05

import android.annotation.SuppressLint
import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.os.IBinder
import android.util.Log
import android.util.Log.d
import android.view.View
import androidx.work.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener{

    companion object   {
        const val TAG = "PP05_TAG"
        const val USERNAME = "CS3714"
        const val URL = "http://requestbin.fullcontact.com/"
        const val ROUTE = "https://enug3zu94jmnq.x.pipedream.net"
        const val INITIALIZE_STATUS = "initialization status"
        const val MUSIC_PLAYING = "music playing"
    }

    //Work manager
    class UploadWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams)   {

        override fun doWork(): Result {
            val json = JSONObject()
            json.put("username", inputData.getString("username"))
            json.put("date", inputData.getString("date"))
            json.put("event", inputData.getString("event"))
            //Log.d("doWork is username:", inputData.getString("username").toString())     //testing if it works
            //Log.d("doWork is event:", inputData.getString("event").toString())           //testing if it works
            Log.d(MainActivity.TAG, "params:"+json.toString()+ " url "+MainActivity.URL)
            return uploadLog(json, MainActivity.URL)
        }

        //Utilizes TrackerRetrofitService.
        fun uploadLog(json: JSONObject, url: String): Result {
            var call = TrackerRetrofitService.create(url).postLog(json)
            call.execute()
            return Result.success()
        }
    }

    //Additional values/variables used in this program.
    val workManager = WorkManager.getInstance()
    var myData: Data = workDataOf()
    var dataBuilding = OneTimeWorkRequestBuilder<UploadWorker>().setInputData(myData).build()

    //Gets the date and time
    val sdf = SimpleDateFormat("M/dd/yyyy hh:mm:ss")
    val currentDate = sdf.format(Date())


    //Appends the name, event based on what action is performed, and date+time.
    private fun appendEvent(event: String)  {
        myData = workDataOf("username" to "Chung", "event" to event, "date" to currentDate)
        dataBuilding = OneTimeWorkRequestBuilder<UploadWorker>().setInputData(myData).build()
        WorkManager.getInstance().beginUniqueWork(event, ExistingWorkPolicy.APPEND, dataBuilding)
    }


    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {
        if (isBound) {
            when (musicService?.getPlayingStatus()) {
                0 -> {
                    appendEvent("onClick")        //added
                    workManager.enqueue(dataBuilding)   //added
                    musicService?.startMusic()
                    play?.text = "Pause"
                }
                1 -> {
                    appendEvent("Pause clicked")        //added
                    workManager.enqueue(dataBuilding)   //added
                    musicService?.pauseMusic()
                    play?.text = "Resume"
                }
                2 -> {
                    appendEvent("Resume clicked")        //added
                    workManager.enqueue(dataBuilding)   //added

                    musicService?.resumeMusic()
                    play?.text = "Pause"
                }
            }
        }
    }

    val INITIALIZE_STATUS = "intialization status"
    val MUSIC_PLAYING = "music playing"

    var play: Button? = null
    var music: TextView? = null

    var musicService: MusicService? = null
    var musicCompletionReceiver: MusicCompletionReceiver? = null
    var startMusicServiceIntent: Intent? = null
    var isInitialized = false
    var isBound = false

    private val musicServiceConnection = object : ServiceConnection {
        @SuppressLint("SetTextI18n")
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

            val binder = iBinder as MusicService.MyBinder
            musicService = binder.getService()
            isBound = true
            when (musicService?.getPlayingStatus()) {
                0 -> play?.text = "Start"
                1 -> play?.text = "Pause"
                2 -> play?.text = "Resume"
            }

            appendEvent("onServiceConnected")     //added
            workManager.enqueue(dataBuilding)           //added
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            musicService = null
            isBound = false

            appendEvent("onServiceDisconnected")     //added
            workManager.enqueue(dataBuilding)              //added
        }
    }

    override fun onPause() {
        super.onPause()
        if (isBound) {
            unbindService(musicServiceConnection)
            isBound = false
        }
        unregisterReceiver(musicCompletionReceiver)
    }

    override fun onResume() {
        super.onResume()
        if (isInitialized && !isBound) {
            bindService(startMusicServiceIntent, musicServiceConnection, Context.BIND_AUTO_CREATE)
        }
        registerReceiver(musicCompletionReceiver, IntentFilter(MusicService.COMPLETE_INTENT))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(INITIALIZE_STATUS, isInitialized)
        outState.putString(MUSIC_PLAYING, music?.text.toString())
        super.onSaveInstanceState(outState)

        appendEvent("onSaveInstanceState")     //added
        workManager.enqueue(dataBuilding)            //added
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        play = findViewById(R.id.play)
        music = findViewById(R.id.music)
        play?.setOnClickListener(this)

        //this restores the textview
        if (savedInstanceState != null) {
            isInitialized = savedInstanceState.getBoolean(INITIALIZE_STATUS)
            music?.text = savedInstanceState.getString(MUSIC_PLAYING)
        }

        startMusicServiceIntent = Intent(this, MusicService::class.java)
        if (!isInitialized) {
            startService(startMusicServiceIntent)
            isInitialized = true
        }
        musicCompletionReceiver = MusicCompletionReceiver(this)

        appendEvent("onCreate")       //added
        workManager.enqueue(dataBuilding)   //added
    }

    @SuppressLint("SetTextI18n")
    fun updateName(musicName: String) {
        music?.text = "You are listening to $musicName"

        appendEvent("updateName")     //added
        workManager.enqueue(dataBuilding)   //added
    }
}


