package com.example.popularmoviesstage2.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.popularmoviesstage2.activities.MainActivity;
import com.example.popularmoviesstage2.movie.Movie;
import com.example.popularmoviesstage2.utilities.AppExecutor;
import com.example.popularmoviesstage2.utilities.JsonMovieUtils;
import com.example.popularmoviesstage2.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class ensures that the movies requested via API survive to configuration changes
 * like app rotations. We avoid unnecessaries queries to the movies APUI
 */

public class MovieAPIViewModel extends AndroidViewModel {

    private List<Movie> mMovies;
    private String mSortOption;

    public MovieAPIViewModel(@NonNull Application application) {
        super(application);
        mMovies = new ArrayList<>();
        mSortOption = "";
    }

    public List<Movie> getMovies() {
        return mMovies;
    }

    /**
     * This method request the movies to the online API
     *
     * @param mSortOption query selected by the user
     */
    public void requestMovie(final String mSortOption) {

        // If we request the movies we already have, just return them
        if(this.mSortOption.equals(mSortOption)){
            AppExecutor.getsInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    MainActivity.setMovies(mMovies);
                    MainActivity.mAdapter.updateData(mMovies);
                    MainActivity.progDailog.dismiss();
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

                    if(mMovies != null){
                        AppExecutor.getsInstance().mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.setMovies(mMovies);
                                MainActivity.mAdapter.updateData(mMovies);
                                MainActivity.progDailog.dismiss();
                            }
                        });

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
