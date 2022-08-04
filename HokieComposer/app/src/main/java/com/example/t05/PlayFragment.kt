package com.example.t05

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.work.*
import kotlinx.android.synthetic.main.fragment_play.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class PlayFragment : Fragment(), View.OnClickListener {

    //Modelling used in this program.
    private val model: ViewModel by activityViewModels()

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
                1 -> {
                    appendEvent("Pause button clicked.")
                    workManager.enqueue(dataBuilding)
                    musicService?.pauseMusic()
                    play?.text = "Resume"
                }
                2 -> {
                    appendEvent("Resume button clicked.")
                    workManager.enqueue(dataBuilding)

                    musicService?.resumeMusic()
                    play?.text = "Pause"
                }
            }
        }
    }

    //This button is used to restart the music.
    fun onClick2() {
                    appendEvent("Restart button pressed.")
                    workManager.enqueue(dataBuilding)
                    musicService?.restartMusic()
            }


    //Variables and values used for this program.
    val INITIALIZE_STATUS = "intialization status"
    val MUSIC_PLAYING = "music playing"
    var play: Button? = null
    var music: TextView? = null
    var musicService: MusicService? = null
    var musicCompletionReceiver: MusicCompletionReceiver? = null
    var startMusicServiceIntent: Intent? = null
    var isInitialized = true
    var isBound = false

    private val musicServiceConnection = object : ServiceConnection {
        @SuppressLint("SetTextI18n")
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

            val binder = iBinder as MusicService.MyBinder
            musicService = binder.getService()
            isBound = true
            when (musicService?.getPlayingStatus()) {
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
            requireActivity().unbindService(musicServiceConnection)
            isBound = false
        }
        requireActivity().unregisterReceiver(musicCompletionReceiver)
    }

    override fun onResume() {
        super.onResume()
        if (isInitialized && !isBound) {
            requireActivity().bindService(startMusicServiceIntent, musicServiceConnection, Context.BIND_AUTO_CREATE)
        }
        requireActivity().registerReceiver(musicCompletionReceiver, IntentFilter(MusicService.COMPLETE_INTENT))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(INITIALIZE_STATUS, isInitialized)
        outState.putString(MUSIC_PLAYING, music?.text.toString())
        super.onSaveInstanceState(outState)

        appendEvent("onSaveInstanceState")     //added
        workManager.enqueue(dataBuilding)            //added
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_play, container, false)

        super.onCreate(savedInstanceState)

        play = view.findViewById(R.id.play)
        music = view.findViewById(R.id.music)
        play?.setOnClickListener(this)

        //this restores the textview
        if (savedInstanceState != null) {
            isInitialized = savedInstanceState.getBoolean(INITIALIZE_STATUS)
            music?.text = savedInstanceState.getString(MUSIC_PLAYING)
            appendEvent("updateName")     //added
            workManager.enqueue(dataBuilding)   //added

        }

        startMusicServiceIntent = Intent(getActivity(), MusicService::class.java)
        musicCompletionReceiver = MusicCompletionReceiver()

        appendEvent("onCreate")       //added
        workManager.enqueue(dataBuilding)   //added


        when (model.backgroundState) {
            1 -> view.backgroundMusic.text = "Go Tech Go"
            2 -> view.backgroundMusic.text = "Enter Sandman Intro"
            3 -> view.backgroundMusic.text = "Tech Triumph"
        }
        when (model.backgroundState) {
            1 -> view.imageView.setBackgroundResource(R.drawable.go)
            2 -> view.imageView.setBackgroundResource(R.drawable.sand)
            3 -> view.imageView.setBackgroundResource(R.drawable.win)
        }

        view.restart.setOnClickListener {
            onClick2()
        }
        return view
    }
}
