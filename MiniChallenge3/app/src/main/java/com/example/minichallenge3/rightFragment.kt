package com.example.minichallenge3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_right.*

/**
 * A simple [Fragment] subclass.
 */
class rightFragment : Fragment() {

    //Used when calling SharedViewModel.kt functions.
    private val model: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view4 = inflater.inflate(R.layout.fragment_right, container, false)

        //Gets the randomly generated right number and posts it to the right textView.
        model.getValues().observe(viewLifecycleOwner, Observer<List<Int>> {
                valuesLocal->

            rightText.text = valuesLocal[2].toString()
        })

        return view4
    }

}
