package com.example.minichallenge3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_all.*

/**
 * A simple [Fragment] subclass.
 */
class AllFragment : Fragment() {

    //Used when calling SharedViewModel.kt functions.
    private val model: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_all, container, false)

        /*
        * Start button. When pressed, it obtains the random numbers generated by slotMachineDraw
        * function and then places them in the left, middle, and right textViews.
         */
        (view.findViewById<Button>(R.id.start)).setOnClickListener{

            model.slotMachineDraw()
            model.getValues().observe(viewLifecycleOwner, Observer<List<Int>> {
                valuesLocal->

                left1.text = valuesLocal[0].toString()
                middle.text = valuesLocal[1].toString()
                right1.text = valuesLocal[2].toString()
            })
        }
        //stop button which pauses the random numbers.
        (view.findViewById<Button>(R.id.stop)).setOnClickListener{
            model.stop()
        }

        //Posts the random numbers on this fragment when the start button has already been pressed.
        model.getValues().observe(viewLifecycleOwner, Observer<List<Int>> {
                valuesLocal->

            left1.text = valuesLocal[0].toString()
            middle.text = valuesLocal[1].toString()
            right1.text = valuesLocal[2].toString()
        })

        // When left TextView is clicked, goes to left fragment.
        (view.findViewById<TextView>(R.id.left1)).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_allFragment_to_leftFragment)
        }

        // When middle TextView is clicked, goes to middle fragment.
        (view.findViewById<TextView>(R.id.middle)).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_allFragment_to_middleFragment)
        }

        // When right TextView is clicked, goes to right fragment.
        (view.findViewById<TextView>(R.id.right1)).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_allFragment_to_rightFragment)
        }



        return view
    }
}
