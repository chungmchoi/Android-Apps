package com.example.t04

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_detail.view.*

//Fragment that shows movie picture, title, release date,overview, and has a like button.
class detailFragment : Fragment() {

    private val viewModel: MovieViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        //Gets the picture and prints it on the screen.
        Picasso.get().load(resources.getString(R.string.picture_base_url)+viewModel.movList4[0].poster_path).into(view.poster1)

        //Print the title, release date, and overview.
        view.detailTitle.text = viewModel.movList4[0].title
        view.detailDate.text = viewModel.movList4[0].release_date.toString()
        view.detailInfo.text = viewModel.movList4[0].overview

        //If the movie is already liked, then the button says "unlike" and vice versa.
        if (viewModel.movLiked.contains(viewModel.movList4[0])) {
            view.like.text = "Unlike"
        }
        else
        {
            view.like.text = "Like"
        }

        //If the like/unlike button is pressed, then add or remove item from liked movie filter.
        view.like.setOnClickListener{
            if (view.like.text == "Like") {
                viewModel.movLiked.add(viewModel.movList4[0])
                view.like.text = "Unlike"
            }
            else {
                viewModel.movLiked.remove(viewModel.movList4[0])
                view.like.text = "Like"
            }
        }

        return view
    }
}
