package com.example.t06

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.photo.view.*

class adapter(private val exampleList: List<info>, var clickListener: OnItemClickListener) : RecyclerView.Adapter<adapter.ExampleViewHolder>() {

    //Represents one row/box of the recycler view. Each val represents the items in the box.
    class ExampleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)   {
        val imageView: ImageView = itemView.image_view
        val textView1: TextView = itemView.text_view_1
        val textView2: TextView = itemView.text_view_2
        val textView3: TextView = itemView.text_view_3

        fun initialize(item: info, action:OnItemClickListener)  {
            textView1.text = item.text1
            textView2.text = item.text2
            textView3.text = item.text3
            imageView.setImageBitmap(item.imageResource)

            itemView.setOnClickListener{
                action.onItemClick(item, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.photo, parent, false)
        return ExampleViewHolder(itemView)
    }

    override fun getItemCount() = exampleList.size

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val currentItem = exampleList[position]
        //holder.imageView.setImageBitmap(currentItem.imageResource)
        //holder.textView1.text = currentItem.text1
        //holder.textView2.text = currentItem.text2
        //holder.textView3.text = currentItem.text3
        holder.initialize(exampleList.get(position), clickListener)
    }


    interface OnItemClickListener   {
        fun onItemClick(user:info, position: Int)
    }

}


















