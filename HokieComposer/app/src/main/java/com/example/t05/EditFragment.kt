package com.example.t05

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.work.*
import java.text.SimpleDateFormat
import java.util.*


//Edit fragment contains the seek bars for sound customization and play button.
class EditFragment : Fragment(), View.OnClickListener{

    //Additional values/variables used in this program.
    val workManager = WorkManager.getInstance()
    var myData: Data = workDataOf()
    var dataBuilding = OneTimeWorkRequestBuilder<UploadWorker>().setInputData(myData).build()

    companion object   {
        const val TAG = "PP05_TAG"
        const val USERNAME = "CS3714"
        const val INITIALIZE_STATUS = "initialization status"
        const val MUSIC_PLAYING = "music playing"
    }

    //Gets the date and time
    val sdf = SimpleDateFormat("M/dd/yyyy hh:mm:ss")
    val currentDate = sdf.format(Date())

    //Appends the name, event based on what action is performed, and date+time.
    private fun appendEvent(event: String)  {
        myData = workDataOf("username" to "CS3714", "event" to event, "date" to currentDate)
        dataBuilding = OneTimeWorkRequestBuilder<UploadWorker>().setInputData(myData).build()
        WorkManager.getInstance().beginUniqueWork(event, ExistingWorkPolicy.APPEND, dataBuilding)
    }

    //When this button is pressed, a song is played and data is enqueued and sent online.
    @SuppressLint("SetTextI18n")
    override fun onClick(v: View?) {

        if (isBound) {
            when (musicService?.getPlayingStatus()) {
                0 -> {
                    musicService?.startMusic()
                    play?.text = "Play"
                }
                1 -> {
                    appendEvent("Pause clicked")
                    workManager.enqueue(dataBuilding)
                    musicService?.pauseMusic()
                    play?.text = "Resume"
                }
                2 -> {
                    appendEvent("Resume clicked")
                    workManager.enqueue(dataBuilding)
                    musicService?.resumeMusic()
                    play?.text = "Pause"
                }
            }
        }
    }

    //This button is used to restart the music.
    fun onClick2(v: View?) {
        appendEvent("\"Play button pressed. The song is: \" + songName")
        workManager.enqueue(dataBuilding)
        musicService?.restartMusic()
    }

    //Variables used to find out what 3 sound effects shoudl be used and their seek bar position.
    var num = 0
    var effectNum1 = 0
    var effectNum2 = 0
    var effectNum3 = 0
    var seekState1 = 0
    var seekState2 = 0
    var seekState3 = 0

    //Used to tell the app that the current music, sound effect, and sound effect delay has changed.
    fun changeMusic()   {
        musicService?.changeMusic(num,effectNum1,effectNum2,effectNum3,seekState1,seekState2,seekState3)
    }

    //Variables used in this fragment.
    val INITIALIZE_STATUS = "intialization status"
    val MUSIC_PLAYING = "music playing"
    var play: Button? = null
    var music: TextView? = null
    var musicService: MusicService? = null
    var musicCompletionReceiver: MusicCompletionReceiver? = null
    var startMusicServiceIntent: Intent? = null
    var isInitialized = false
    var isBound = false
    var songName = "Go Tech Go"

    private val musicServiceConnection = object : ServiceConnection {
        @SuppressLint("SetTextI18n")
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

            val binder = iBinder as MusicService.MyBinder
            musicService = binder.getService()
            isBound = true
            when (musicService?.getPlayingStatus()) {
                0 -> play?.text = "Play"
                1 -> play?.text = "Play"
                2 -> play?.text = "Play"
            }
            appendEvent("onServiceConnected")
            workManager.enqueue(dataBuilding)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            musicService = null
            isBound = false

            appendEvent("onServiceDisconnected")
            workManager.enqueue(dataBuilding)
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

    //Variables needed for spinner drop down menu
    lateinit var backgroundOptions: Spinner
    lateinit var soundOptions1: Spinner
    lateinit var soundOptions2: Spinner
    lateinit var soundOptions3: Spinner
    private val model: ViewModel by activityViewModels()
    //Variables for the seek bars
    lateinit var slider1 : SeekBar
    lateinit var slider2 : SeekBar
    lateinit var slider3 : SeekBar
    lateinit var value1: TextView
    lateinit var value2: TextView
    lateinit var value3: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit, container, false)
        super.onCreate(savedInstanceState)
        play = view.findViewById(R.id.play)
        music = view.findViewById(R.id.music)
        play?.setOnClickListener(this)

        //this restores the textview
        if (savedInstanceState != null) {
            isInitialized = savedInstanceState.getBoolean(INITIALIZE_STATUS)
            music?.text = savedInstanceState.getString(MUSIC_PLAYING)
        }

        startMusicServiceIntent = Intent(getActivity(), MusicService::class.java)
        if (!isInitialized) {
            context?.startService(startMusicServiceIntent)
            isInitialized = true
        }


        musicCompletionReceiver = MusicCompletionReceiver(this)

        appendEvent("onCreate called")       //added
        workManager.enqueue(dataBuilding)   //added

        //When the play button is pressed, go to the playFragment.
        play?.setOnClickListener {

            if (model.playState == 0) {
                onClick(view)
                model.playState += 1
            }
            else {
                musicService?.restartMusic()
            }
            appendEvent("Play button pressed. The song is: " + songName)
            workManager.enqueue(dataBuilding)
            Navigation.findNavController(view).navigate(R.id.action_editFragment_to_playFragment2)
        }

        //findViewById of the different drop down menu buttons.
        backgroundOptions = view.findViewById(R.id.background_option) as Spinner
        soundOptions1 = view.findViewById(R.id.sound1_option) as Spinner
        soundOptions2 = view.findViewById(R.id.sound2_option) as Spinner
        soundOptions3 = view.findViewById(R.id.sound3_option) as Spinner

        //Declaring what strings will show up in the spinner drop down menu.
        val backgroundStrings = arrayOf("Go Tech Go", "Enter Sandman Intro", "Tech Triumph")
        val effectStrings = arrayOf("Clapping", "Cheering", "Lets Go Hokies")

        //Placing the strings into the drop down menu.
        backgroundOptions.adapter = ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,backgroundStrings)
        soundOptions1.adapter = ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,effectStrings)
        soundOptions2.adapter = ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,effectStrings)
        soundOptions3.adapter = ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,effectStrings)

        /**
         * When the select background music spinner button is clicked. The song of choice is
         * selected.
         */
        backgroundOptions.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                appendEvent("Music selection drop down menu clicked.")
                workManager.enqueue(dataBuilding)

                if(backgroundOptions.getItemAtPosition(p2).toString() == "Go Tech Go")   {
                    model.backgroundState = 1
                    num = 0
                    changeMusic()
                    appendEvent("Go Tech Go selected.")
                    workManager.enqueue(dataBuilding)
                    songName = "Go Tech Go"
                }
                else if (backgroundOptions.getItemAtPosition(p2).toString() == "Enter Sandman Intro")   {
                    model.backgroundState = 2
                    num = 1
                    changeMusic()
                    appendEvent("Enter Sandman Intro selected.")
                    workManager.enqueue(dataBuilding)
                    songName = "Enter Sandman Intro"
                }
                else if (backgroundOptions.getItemAtPosition(p2).toString() == "Tech Triumph")   {
                    model.backgroundState = 3
                    num = 2
                    changeMusic()
                    appendEvent("Tech Triumph selected.")
                    workManager.enqueue(dataBuilding)
                    songName = "Tech Triumph"
                }
            }
        }

        /**
         * First sound effect choice spinner button. Choose between clapping, cheering, and
         * lets go hokies.
         */
        soundOptions1.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                appendEvent("Second sound effect drop down menu clicked.")
                workManager.enqueue(dataBuilding)

                if(soundOptions1.getItemAtPosition(p2).toString() == "Clapping")   {
                    model.songState1 = 1
                    effectNum1 = 0
                    changeMusic()
                    appendEvent("Clapping 1 selected.")
                    workManager.enqueue(dataBuilding)
                }
                else if (soundOptions1.getItemAtPosition(p2).toString() == "Cheering")   {
                    model.songState1 = 2
                    effectNum1 = 1
                    changeMusic()
                    appendEvent("Cheering 1 selected.")
                    workManager.enqueue(dataBuilding)
                }
                else if (soundOptions1.getItemAtPosition(p2).toString() == "Lets Go Hokies")   {
                    model.songState1 = 3
                    effectNum1 = 2
                    changeMusic()
                    appendEvent("Lets Go Hokies 1 selected.")
                    workManager.enqueue(dataBuilding)
                }
            }
        }

        //Second sound effect choice spinner button.
        soundOptions2.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                appendEvent("Second sound effect drop down menu clicked.")
                workManager.enqueue(dataBuilding)

                if(soundOptions2.getItemAtPosition(p2).toString() == "Clapping")   {
                    model.songState2 = 1
                    effectNum2 = 0
                    changeMusic()
                    appendEvent("Clapping 2 selected.")
                    workManager.enqueue(dataBuilding)
                }
                else if (soundOptions2.getItemAtPosition(p2).toString() == "Cheering")   {
                    model.songState2 = 2
                    effectNum2 = 1
                    changeMusic()
                    appendEvent("Cheering 2 selected.")
                    workManager.enqueue(dataBuilding)
                }
                else if (soundOptions2.getItemAtPosition(p2).toString() == "Lets Go Hokies")   {
                    model.songState2 = 3
                    effectNum2 = 2
                    changeMusic()
                    appendEvent("Lets Go Hokies 2 selected.")
                    workManager.enqueue(dataBuilding)
                }
            }
        }

        //Third sound effect choice spinner button.
        soundOptions3.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                appendEvent("Third sound effect drop down menu clicked.")
                workManager.enqueue(dataBuilding)

                if(soundOptions3.getItemAtPosition(p2).toString() == "Clapping")   {
                    model.songState3 = 1
                    effectNum3 = 0
                    changeMusic()
                    appendEvent("Clapping 3 selected.")
                    workManager.enqueue(dataBuilding)
                }
                else if (soundOptions3.getItemAtPosition(p2).toString() == "Cheering")   {
                    model.songState3 = 2
                    effectNum3 = 1
                    changeMusic()
                    appendEvent("Cheering 3 selected.")
                    workManager.enqueue(dataBuilding)
                }
                else if (soundOptions3.getItemAtPosition(p2).toString() == "Lets Go Hokies")   {
                    model.songState3 = 3
                    effectNum3 = 2
                    changeMusic()
                    appendEvent("Lets Go Hokies 3 selected.")
                    workManager.enqueue(dataBuilding)
                }
            }
        }

        /////////////////SEEK BAR SECTION///////////////////////////////////////////////////////////
        //First seekbar, finding the sliders and text which shows music delay from 0-100
        slider1 = view.findViewById(R.id.soundSeek1)
        slider2 = view.findViewById(R.id.soundSeek2)
        slider3 = view.findViewById(R.id.soundSeek3)
        value1 = view.findViewById(R.id.delay1) as TextView
        value2 = view.findViewById(R.id.delay2) as TextView
        value3 = view.findViewById(R.id.delay3) as TextView


        //First slider to customize sound effect delay. The code for the three sliders are similiar.
        slider1.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                value1.text = p1.toString() //Shows sound effect delay number.
                seekState1 = p1
                changeMusic()
                appendEvent("First slider bar moved. Positon: " + p1.toString())
                workManager.enqueue(dataBuilding)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        //Second slider sound effect.
        slider2.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                value2.text = p1.toString()
                seekState2 = p1
                changeMusic()
                appendEvent("Second slider bar moved. Positon: " + p1.toString())
                workManager.enqueue(dataBuilding)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        //Third slider sound effect.
        slider3.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                value3.text = p1.toString()
                seekState3 = p1
                changeMusic()
                appendEvent("Third slider bar moved. Positon: " + p1.toString())
                workManager.enqueue(dataBuilding)

            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        /////////////////////////////////////////////////////////////
        return view
    }

    @SuppressLint("SetTextI18n")
    fun updateName(musicName: String) {
        music?.text = "You are listening to $musicName"

        appendEvent("updateName")     //added
        workManager.enqueue(dataBuilding)   //added
    }
}
