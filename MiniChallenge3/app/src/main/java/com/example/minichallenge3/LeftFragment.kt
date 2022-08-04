package com.example.minichallenge3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_left.*

/**
 * A simple [Fragment] subclass.
 */
class LeftFragment : Fragment() {

    //Used when calling SharedViewModel.kt functions.
    private val model: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view2 = inflater.inflate(R.layout.fragment_left, container, false)

        //Gets the randomly generated left number and posts it to the left textView.
        model.getValues().observe(viewLifecycleOwner, Observer<List<Int>> {
                valuesLocal->

            leftText.text = valuesLocal[0].toString()
        })

        return view2
    }

}
