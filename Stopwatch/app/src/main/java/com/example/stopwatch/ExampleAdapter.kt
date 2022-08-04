package com.example.stopwatch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.laps_recycler.view.*

/**
 * This is my Adapter Class which is used for RecyclerView. I named it ExampleAdapter because I was
 * in the process of learning how to implement RecyclerView. I managed to get it to work on this
 * app but didn't want to risk ruining my program by trying to name it something else.
 */
class ExampleAdapter(private val exampleList: ArrayList<String>) :

    RecyclerView.Adapter<ExampleAdapter.ExampleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.laps_recycler, parent, false)
        return ExampleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {

        val currentItem = exampleList[position]
        holder.textView2.text = currentItem
    }

    override fun getItemCount() = exampleList.size

    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)   {
        val textView2: TextView = itemView.lap_data
    }
}