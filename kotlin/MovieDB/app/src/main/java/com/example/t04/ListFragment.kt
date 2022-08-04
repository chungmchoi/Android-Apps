package com.example.t04

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_list.view.*


//Main screen fragment that shows the list of movies, buttons, and search bar.
class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    //This apps adapter, modelling, a counter, and arraylists used to gather movie data.
    val adapter = MovieListAdapter()
    private val model1: MovieViewModel by activityViewModels()
    var status = 0
    var movList1:MutableList<MovieItem> = ArrayList()
    var movList2:MutableList<MovieItem> = ArrayList()
    var movListTitle:List<MovieItem> = ArrayList()
    var movListVote:List<MovieItem> = ArrayList()
    var movList3:MutableList<MovieItem> = ArrayList()
    var movList5:MutableList<MovieItem> = ArrayList()

    //Search bar text change.
    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    //search bar text submit/
    override fun onQueryTextSubmit(query: String): Boolean {
        adapter.search(query)
        return true
    }

    //search bar creation.
    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater)   {
        menuInflater.inflate(R.menu.search_movie, menu)
        val searchItem: MenuItem = menu.findItem(R.id.menu_search)
        searchItem.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                return true
            }
        })
        val searchView: SearchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }



    //Sort movies based on title/votes, then updates list of movies
    fun sortMovies(mov:MutableList<MovieItem>)  {

        //Sorts movie by average votes.
        fun sortVote(mov:MutableList<MovieItem>)   {
            movListVote =  mov.sortedByDescending { mov -> mov.vote_average}
        }
        //Sorts movie by title
        fun sortTitle(mov:MutableList<MovieItem>) {
            movListTitle =  mov.sortedBy { mov -> mov.title }
        }
        //Updates movie list depending on rating/title/none button status.
        if (status == 1)    {
            sortVote(mov)
            adapter.setMovies(movListVote)
        }
        if (status == 0)
        {
            sortTitle(mov)
            adapter.setMovies(movListTitle)
        }
    }


    /**
     * Finds current movie list of the ListFragment after returning from the detail fragment.
     * For example, if you're on the liked movies screen and then click on a picture, then go back,
     * the ListFragment will show the liked movies screen upon return.
     */

    fun returnStatus() {
        if (model1.statusfilter == 1)   {
            movList3 = model1.movLiked
            sortMovies(movList3)
        }
        if (model1.statussearch > 0)  {
            movList3 = movList5
            sortMovies(movList3)
        }
    }

    //Creates the view.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        var movList:MutableList<MovieItem> = ArrayList()
        setHasOptionsMenu(true) //Used to create search bar.

        //Recycler view.
        val recyclerView = view.findViewById<RecyclerView>(R.id.movie_list)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val model: MovieViewModel by activityViewModels()

        //Observing live
        model.allMovies.observe(
            viewLifecycleOwner,//this
            Observer<List<MovieItem>>{ movies ->
                movies?.let{adapter.setMovies(it)}
                movies?.let{movList.addAll(it)}    //Adds all movies to movList
                sortMovies(movList) //Sorts movList and places them on the screen at start.
                returnStatus()  //Places different list if returning from detailFragment.
            }
        )
        //movList1 and movList3 are set to movList to be used for later data usage.
        movList1 = movList
        movList3 = movList

        //Refresh button. Posts all movies based on what sort button was pressed.
        (view.findViewById<Button>(R.id.refresh)).setOnClickListener{
            model.refreshMovies(1)
            sortMovies(movList)
            movList3 = movList
            model1.statusfilter -= 1    //Used in returnStatus().
            model1.statussearch = 0     //Used in returnStatus().
        }
        //Sort movie button. Sorts movie list based on button pressed.
        (view.findViewById<Button>(R.id.sortMovies)).setOnClickListener{

            if (status == 0)   {
                status += 1
                view.sortMovies.text = "Sort by: Votes"
                sortMovies(movList3)

            }
            else if (status == 1)   {
                status -= 1
                view.sortMovies.text = "Sort by: Titles"
                sortMovies(movList3)
            }
            returnStatus()
        }
        //Filter button. Filters movies based on movies you like.
        (view.findViewById<Button>(R.id.filter)).setOnClickListener{
            movList3 = model1.movLiked
            sortMovies(movList3)
            model1.statusfilter += 1
        }

        return view
    }

    //Adapter class.
    inner class MovieListAdapter():
        RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>(){
        private var movies = emptyList<MovieItem>()

        //Sets movies on the screen.
        internal fun setMovies(movies: List<MovieItem>) {
            this.movies = movies
            notifyDataSetChanged()

        }

        //Gets the number of movies in the list.
        override fun getItemCount(): Int {

            return movies.size
        }

        //Inflater for this section.
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {

            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.card_view, parent, false)

            return MovieViewHolder(v)
        }

        //Places movie poster, title, and rating on the screen.
        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

            Glide.with(this@ListFragment).load(resources.getString(R.string.picture_base_url)+movies[position].poster_path).apply( RequestOptions().override(128, 128)).into(holder.view.findViewById(R.id.poster))
            holder.view.findViewById<TextView>(R.id.title).text=movies[position].title
            holder.view.findViewById<TextView>(R.id.rating).text=movies[position].vote_average.toString()

            /**
             * When a movie is clicked, the movie data is stored in movList4 and then the app
             * goes to the detailFragment.
             */
            holder.itemView.setOnClickListener{
                model1.movList4.add(0, movies[position])
                Navigation.findNavController(holder.view).navigate(R.id.action_listFragment_to_detailFragment)
                //Navigation.findNavController(holder.view).navigate(R.id.action_listFragment_to_detailFragment, bundleOf("key" to movies[position].title))
                //view?.findNavController()?.
            }
        }

        //Used by search bar to find movies that match the words.
        fun search(str:String)    {

            for (x in 0 until movList3.size)   {
                if (movList3[x].title.contains(str)) {
                    movList2.add(movList3[x])
                }
            }
            val copyMov = movList2.toMutableList()  //hard copied list is needed.
            movList2.clear()                        //cleared original for later use.
            sortMovies(copyMov)
            movList5 = copyMov
            model1.statussearch += 1                //Used in returnStatus()
        }

        //Used in onBindViewHolder.
        inner class MovieViewHolder(val view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
            override fun onClick(view: View?){

                if (view != null) {

                }
            }
        }
    }

}

