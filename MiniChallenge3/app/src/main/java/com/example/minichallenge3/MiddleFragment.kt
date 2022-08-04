package com.example.minichallenge3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_middle.*

/**
 * A simple [Fragment] subclass.
 */
class MiddleFragment : Fragment() {

    //Used when calling SharedViewModel.kt functions.
    private val model: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view3 = inflater.inflate(R.layout.fragment_middle, container, false)

        //Gets the randomly generated middle number and posts it to the middle textView.
        model.getValues().observe(viewLifecycleOwner, Observer<List<Int>> {
                valuesLocal->

            middleText.text = valuesLocal[1].toString()
        })

        return view3
    }
}
