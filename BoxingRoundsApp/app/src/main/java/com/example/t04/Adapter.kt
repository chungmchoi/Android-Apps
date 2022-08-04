package com.example.t04

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.workout1.view.*

class Adapter(private val exampleList: List<BoxingData>) : RecyclerView.Adapter<Adapter.ExampleViewHolder>() {

    //Represents a single workout
    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView1: TextView = itemView.rndlength
        val textView2: TextView = itemView.restlength
        val textView3: TextView = itemView.completed
        val textView4: TextView = itemView.minsec
        val textView5: TextView = itemView.date
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.workout1, parent,
            false)
        return ExampleViewHolder(itemView)
    }

    override fun getItemCount() = exampleList.size

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = exampleList[position]
        holder.textView1.text = "Round length: " + currentItem.roundLength.toString()
        holder.textView2.text = "Rest length: " + currentItem.restLength.toString()
        holder.textView3.text = "Round reached: " + currentItem.current.toString()
        holder.textView4.text = "Total time: " + currentItem.min + "minutes and " + currentItem.sec + "seconds"
        holder.textView5.text = "Date: " + currentItem.time
    }

}

















