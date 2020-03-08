package com.example.popularmoviesstage2.viewmodel;

import android.app.Application;
import android.app.ListActivity;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.popularmoviesstage2.database.MovieDataBase;
import com.example.popularmoviesstage2.movie.Movie;

import java.util.List;

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
