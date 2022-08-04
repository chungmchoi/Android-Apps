package com.example.stopwatch

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_view_lap.*
import kotlinx.android.synthetic.main.fragment_view_lap.view.*
import kotlinx.android.synthetic.main.fragment_view_lap.view.recycler_view

/**
 * A simple [Fragment] subclass.
 */
class viewLapFragment : Fragment() {

    //Used when calling SharedViewModel.kt functions.
    private val model: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view2 = inflater.inflate(R.layout.fragment_view_lap, container, false)

        //Observes continuously and prints the lap number and time using recycler view.
        model.getValues().observe(viewLifecycleOwner, Observer {
            view2.recycler_view.adapter = ExampleAdapter(model.arraylist)
            view2.recycler_view.layoutManager = LinearLayoutManager(getContext())
            view2.recycler_view.setHasFixedSize(true)
        })

        return view2
    }
}
