package com.example.t04

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.bumptech.glide.Glide.init
import io.reactivex.android.plugins.RxAndroidPlugins.reset
import io.reactivex.plugins.RxJavaPlugins.reset
import kotlinx.android.synthetic.main.fragment_stats.*
import kotlinx.android.synthetic.main.fragment_stats.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val model: BoxingViewModel by activityViewModels()
    private var soundPool: SoundPool? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_stats, container, false)
        model.startState = 0


        //5 different beeps loaded into sound pool
        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool!!.load(activity, R.raw.beep1, 1)
        soundPool!!.load(activity, R.raw.beep2, 1)
        soundPool!!.load(activity, R.raw.beep3, 1)
        soundPool!!.load(activity, R.raw.beep4, 1)
        soundPool!!.load(activity, R.raw.beep5, 1)

        //Observer used to get changing values of timer.
        model.getValues().observe(viewLifecycleOwner, Observer { valuesLocal ->

            //While the timer is on, the button will say "STOP" after returning to mainFragment.
            if (model.state == 1){
                start.text = "PAUSE"
            }
            //Starts timer when the button is pressed. LAP and CANCEL LAP buttons enabled.
            (view.findViewById<Button>(R.id.start)).setOnClickListener{
                //Disable customization buttons when timer has begun.
                view.plus.isEnabled = false
                view.minus.isEnabled = false
                view.editTextNumber.setEnabled(false)
                view.editTextNumber2.setEnabled(false)
                view.status.text = "Training"
                model.message = 2////////////////////////////////////////
                model.startState = 1/////////////////////////
                view.currentRound.text = "1"
                if (model.state == 0) {

                    model.startTimer()

                    //seconds and minutes updated.
                    seconds.text = valuesLocal[0].toString().padStart(2, '0')
                    minutes.text = valuesLocal[1].toString().padStart(2, '0')
                    start.text = "PAUSE"
                    model.state = 1 //boolean state used to check whether timer is active or not.
                    model.pauseState = 0
                }
                else
                {
                    model.stop()
                    start.text = "START"
                    model.state = 0 //boolean state

                }
            }

            if (model.message == 1) {
                view.status.text = "Resting"
            }
            if (model.message == 2)    {
                view.status.text = "Training"
            }
            if (model.message == 3)    {
                view.status.text = "Finished"
            }

            //Prevents edit text from crashing when value is null.
            if (editTextNumber.text.toString().trim().isNotEmpty() ||
                editTextNumber.text.toString().trim().isNotBlank()) {

                model.roundLength = view.editTextNumber.text.toString().toInt()
            }
            else
            {
                model.roundLength = 0
            }

            if (editTextNumber2.text.toString().trim().isNotEmpty() ||
                editTextNumber2.text.toString().trim().isNotBlank()) {

                model.restLength = view.editTextNumber2.text.toString().toInt()
            }
            else
            {
                model.restLength = 0
            }


            /**
             * Sets up the interval training timer of this app.
             */
            view.roundLength.text = "Round length(seconds): " + model.roundLength.toString()
            view.rest.text = "Rest length(seconds): " + model.restLength.toString()
            //Gets the date and time
            model.sdf = SimpleDateFormat("M/dd/yyyy hh:mm:ss")
            model.currentDate = model.sdf.format(Date())
            model.secondTime += 1
            model.subTime += 1

            //When returning from another fragment, start button is disabled.
            if(model.pauseState == 1){
                view.start.isEnabled = false
            }

            //Timer keeps running until training is complete.
            if (model.secondTime < model.roundLength * model.roundNumber + model.restLength * model.roundNumber)  {

                /**
                 * What to do when round length is 0 but rest length isn't.
                 * Alarm clock doesn't activate here because multiple rests with no rounds does not require an alarm unless the timer ends.
                 * There is no need need to inform users of
                 */
                if (model.roundLength == 0 && model.restLength > 0)
                {
                    view.status.text = "Resting"
                    model.message = 1
                }
                //Do this when a round is finished.
                else if (model.subTime == model.roundLength)  {
                    if (model.restLength != 0) {
                        view.status.text = "Resting"
                        model.message = 1
                        soundPool?.play(model.soundId, 1F, 1F, 0, 0, 1F)
                    }
                    //If the round is finished but there is no rest length, then do this.
                    else if (model.restLength == 0)
                    {
                        model.currentRound1 += 1
                        view.currentRound.text = model.currentRound1.toString()
                        model.subTime = 0
                        model.message = 2
                        soundPool?.play(model.soundId, 1F, 1F, 0, 0, 1F)
                    }
                }
                //When round length and rest length equals subTime, subTime is reset and process
                //repeats until training time is complete.
                else if ((model.subTime == (model.roundLength + model.restLength)))   {
                    if (model.roundLength != 0) {
                        view.status.text = "Training"
                        model.message = 2
                        model.subTime = 0
                        model.currentRound1 += 1
                        view.currentRound.text = model.currentRound1.toString()
                        soundPool?.play(model.soundId, 1F, 1F, 0, 0, 1F)
                    }
                    else
                    {
                        view.status.text = "Training"
                        model.currentRound1 += 1
                        view.currentRound.text = model.currentRound1.toString()
                        soundPool?.play(model.soundId, 1F, 1F, 0, 0, 1F)
                    }
                }
            }
            //Do this when the timer has finished.
            else
            {
                if (model.startState != 0 && view.status.text != "Finished") {
                    model.stop()
                    view.status.text = "Finished"
                    model.message = 3
                    view.start.isEnabled = false
                    model.subTime = 0
                    model.pauseState = 1
                    soundPool?.play(model.soundId, 1F, 1F, 0, 0, 1F)
                }
            }

            /**
             * When the RESET button is pressed, the timer stopped and reset to 00:00
             * All laps are deleted.
             */
            (view.findViewById<Button>(R.id.reset)).setOnClickListener {

                val myToast = Toast.makeText(context,"Workout Saved",Toast.LENGTH_SHORT)
                myToast.setGravity(Gravity.CENTER_VERTICAL,0,600)
                myToast.show()
                view.status.text = "Finished"

                model.arraylist2.add(BoxingData(model.roundLength, model.restLength, model.currentRound1, valuesLocal[1].toString()
                    .padStart(2, '0'),
                    valuesLocal[0].toString().padStart(2, '0'), model.currentDate))

                view.start.isEnabled = true
                model.arraylist.clear()
                model.resetTime()
                seconds.text = valuesLocal[0].toString().padStart(2, '0')
                minutes.text = valuesLocal[1].toString().padStart(2, '0')
                model.startTimer()
                start.text = "Start"
                model.state = 0
                model.stop()
                model.secondTime = 0
                model.currentRound1 = 1
                model.pauseState = 0
                view.currentRound.text = model.currentRound1.toString()

                //Enables customization buttons when timer has been reset.
                view.plus.isEnabled = true
                view.minus.isEnabled = true
                view.editTextNumber.setEnabled(true)
                view.editTextNumber2.setEnabled(true)
            }

            //Goes to view results page when the button is clicked.
            (view.findViewById<Button>(R.id.results)).setOnClickListener {

                Navigation.
                findNavController(view).
                navigate(R.id.action_statsFragment_to_infoFragment)
            }

            //Declares what sound will be played when the button is pressed.
            view.findViewById<Button>(R.id.button1).setOnClickListener {
                model.soundId = 1
            }
            view.findViewById<Button>(R.id.button2).setOnClickListener {
                model.soundId = 2
            }
            view.findViewById<Button>(R.id.button3).setOnClickListener {
                model.soundId = 3
            }
            view.findViewById<Button>(R.id.button4).setOnClickListener {
                model.soundId = 4
            }

            view.findViewById<Button>(R.id.button5).setOnClickListener {
                model.soundId = 5
            }


            //Posts the time on mainFragment when the timer is already active.
            seconds.text = valuesLocal[0].toString().padStart(2, '0')
            minutes.text = valuesLocal[1].toString().padStart(2, '0')

            view.currentRound.text = model.currentRound1.toString()
            view.roundnumber.text = model.roundNumber.toString()
            view.finalRound.text = model.roundNumber.toString()
            //Increases the number of rounds when button is pressed
            (view.findViewById<Button>(R.id.plus)).setOnClickListener   {
                model.roundNumber += 1
                view.roundnumber.text = model.roundNumber.toString()
                view.finalRound.text = model.roundNumber.toString()
            }
            //Decreases the number of rounds when button is pressed. Cannot be less than 1.
            (view.findViewById<Button>(R.id.minus)).setOnClickListener   {
                if (model.roundNumber >= 2) {
                    model.roundNumber -= 1
                    view.roundnumber.text = model.roundNumber.toString()
                    view.finalRound.text = model.roundNumber.toString()
                }
            }

        })
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StatsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StatsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}