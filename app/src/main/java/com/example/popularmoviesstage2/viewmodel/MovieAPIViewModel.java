package com.example.popularmoviesstage2.viewmodel;

import android.app.Application;
import android.app.ProgressDialog;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.popularmoviesstage2.activities.MainActivity;
import com.example.popularmoviesstage2.movie.Movie;
import com.example.popularmoviesstage2.utilities.AppExecutor;
import com.example.popularmoviesstage2.utilities.JsonMovieUtils;
import com.example.popularmoviesstage2.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MovieAPIViewModel extends AndroidViewModel {

    private List<Movie> mMovies;
    private String mSortOption;
    private ProgressDialog progDialog;

    public MovieAPIViewModel(@NonNull Application application) {
        super(application);
        mMovies = new ArrayList<>();
        mSortOption = "";
    }

    public List<Movie> getMovies() {
        return mMovies;
    }

    public void requestMovie(final String mSortOption) {

        if(this.mSortOption.equals(mSortOption)){

            AppExecutor.getsInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("SUPER__", "IM GETTING THE DATA FROM THE MODEL VIEW!!");
                    MainActivity.setMovies(mMovies);
                    MainActivity.mAdapter.updateData(mMovies);
                }
            });

            return;
        }

        this.mSortOption = mSortOption;
        AppExecutor.getsInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {

                String jsonMovies;
                try {

                    switch (mSortOption) {
                        case NetworkUtils.top_rated:
                            jsonMovies = NetworkUtils.getTopRateMovies();
                            break;
                        case NetworkUtils.popular:
                            jsonMovies = NetworkUtils.getPopularMovies();
                            break;
                        default:
                            jsonMovies = "";
                            break;
                    }

                    mMovies = JsonMovieUtils.parseMoviesJsonArray(jsonMovies);

                    AppExecutor.getsInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("SUPER__", "IM ASKING API FROM MODELVIEW!!");
                            MainActivity.setMovies(mMovies);
                            MainActivity.mAdapter.updateData(mMovies);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
