package com.example.t04

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MovieViewModel(application: Application): AndroidViewModel(application){

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob+ Dispatchers.Main

    //Variables used to store movies in different lists, and counter variables.
    private val scope = CoroutineScope(coroutineContext)
    private var disposable: Disposable? = null
    private val repository: MovieItemRepository
    val allMovies: LiveData<List<MovieItem>>
    var movList4:MutableList<MovieItem> = ArrayList()   //Stores a movie that is clicked.
    var movLiked:MutableList<MovieItem> = ArrayList()   //Stores liked movies.
    var statusfilter = 0    //counter used to find filter button status
    var statussearch = 0    //counter used to find search bar status

    init {
        val moviesDao = MovieRoomDatabase.getDatabase(application).movieDao()

        repository = MovieItemRepository(moviesDao)
        allMovies = repository.allMovies
    }

    fun refreshMovies(page: Int){

        //API key added.
        disposable =
            RetrofitService.create("https://api.themoviedb.org/3/").getNowPlaying("https://api.themoviedb.org/3/movie/550?api_key=bdec7a84ae0c5980fc8079db57b72825",page).subscribeOn(
                Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()).subscribe(
                {result -> showResult(result)},
                {error -> showError(error)})
    }

    private fun showError(error: Throwable?) {

        Log.d("t04","Error:"+error?.toString())
    }

    private fun showResult(result: Movies?) {

        Log.d("T04","Page:"+result?.page+"Result:"+result?.results?.last()?.release_date+ " pages "+ result?.total_pages)
        deleteAll()

        result?.results?.forEach { movie ->
            insert(movie)
        }
    }

    private fun insert(movie: MovieItem) = scope.launch(Dispatchers.IO) {
        repository.insert(movie)
    }

    private fun deleteAll() = scope.launch (Dispatchers.IO){
        repository.deleteAll()
    }
}