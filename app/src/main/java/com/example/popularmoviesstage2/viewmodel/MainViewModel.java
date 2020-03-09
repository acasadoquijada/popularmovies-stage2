package com.example.popularmoviesstage2.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.popularmoviesstage2.database.MovieDataBase;
import com.example.popularmoviesstage2.movie.Movie;

import java.util.List;

/**
 * This class ensures that the LiveData survives to configuration changes
 * like app rotations. We avoid unnecessaries queries to the DB
 *
 * This LiveData is the favorite movies stored in the DB
 */

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> fav_movies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        MovieDataBase movieDataBase = MovieDataBase.getInstance(this.getApplication());
        fav_movies = movieDataBase.movieDAO().getMovies();
    }

    public LiveData<List<Movie>> getFav_movies() {
        return fav_movies;
    }
}
