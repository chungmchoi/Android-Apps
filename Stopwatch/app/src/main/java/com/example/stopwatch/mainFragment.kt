package com.example.stopwatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_main.view.*

/**
 * A simple [Fragment] subclass.
 */
class mainFragment : Fragment() {

    //Used when calling SharedViewModel.kt functions.
    private val model: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        //Observer used to get changing values of timer.
        model.getValues().observe(viewLifecycleOwner, Observer { valuesLocal ->

            //Starts the app with LAP and CANCEL LAP button disabled.
            if (model.state == 0) {
                view.lap.isEnabled = false
                view.cancel.isEnabled = false
            }

            //While the timer is on, the button will say "STOP" after returning to mainFragment.
            if (model.state == 1){
                start.text = "STOP"
            }

            //Starts timer when the button is pressed. LAP and CANCEL LAP buttons enabled.
            (view.findViewById<Button>(R.id.start)).setOnClickListener{
                if (model.state == 0) {

                    lap.isEnabled = true
                    cancel.isEnabled = true
                    model.startTimer()

                    //seconds and minutes updated.
                    seconds.text = valuesLocal[0].toString().padStart(2, '0')
                    minutes.text = valuesLocal[1].toString().padStart(2, '0')
                    start.text = "STOP"
                    model.state = 1 //boolean state used to check whether timer is active or not.
                }
                else
                {
                    model.stop()
                    lap.isEnabled = false
                    cancel.isEnabled = false
                    start.text = "START"
                    model.state = 0 //boolean state
                }
            }

            //When the LAP button is pressed, adds lap time to the viewLapFragment.
            (view.findViewById<Button>(R.id.lap)).setOnClickListener {
                model.count += 1
                model.arraylist.add(model.count.toString().padEnd(25, '\t')
                    .substring(0,25) +
                        valuesLocal[1].toString().padStart(2, '0') + ":" +
                        valuesLocal[0].toString().padStart(2, '0') + "\n")
            }

            //When the CANCEL button is pressed, the previous lap time is deleted.
            (view.findViewById<Button>(R.id.cancel)).setOnClickListener {
                model.arraylist.removeAt(model.arraylist.size - 1)
                model.count -= 1
            }

            //When the CLEAR button is pressed, all lap times are deleted.
            (view.findViewById<Button>(R.id.clear)).setOnClickListener {
                model.arraylist.clear()
                model.count = 0
            }

            /**
             * When the RESET button is pressed, the timer stopped and reset to 00:00
             * All laps are deleted.
             */
            (view.findViewById<Button>(R.id.reset)).setOnClickListener {
                model.arraylist.clear()
                model.resetTime()
                seconds.text = valuesLocal[0].toString().padStart(2, '0')
                minutes.text = valuesLocal[1].toString().padStart(2, '0')
                model.startTimer()
                lap.isEnabled = false
                cancel.isEnabled = false
                start.text = "START"
                model.count = 0
                model.state = 0
                model.stop()
            }

            //Goes to "VIEW LAP LIST >>" fragment page when the button is clicked.
            (view.findViewById<Button>(R.id.viewLap)).setOnClickListener {

                Navigation.
                findNavController(view).
                    navigate(R.id.action_mainFragment_to_viewLapFragment)
            }

            //Posts the time on mainFragment when the timer is already active.
            seconds.text = valuesLocal[0].toString().padStart(2, '0')
            minutes.text = valuesLocal[1].toString().padStart(2, '0')
            })
        return view
    }
}
