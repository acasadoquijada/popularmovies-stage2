package com.example.popularmoviesstage2.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.popularmoviesstage2.database.MovieDataBase;
import com.example.popularmoviesstage2.movie.Movie;
import com.example.popularmoviesstage2.movie.MovieAdapter;
import com.example.popularmoviesstage2.R;
import com.example.popularmoviesstage2.utilities.AppExecutor;
import com.example.popularmoviesstage2.utilities.JsonMovieUtils;
import com.example.popularmoviesstage2.utilities.NetworkUtils;
import com.example.popularmoviesstage2.viewmodel.MainViewModel;
import com.example.popularmoviesstage2.viewmodel.MovieAPIViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity of the Popular Movies Stage 2 application
 */
public class MainActivity extends AppCompatActivity
        implements MovieAdapter.GridItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static MovieAdapter mAdapter;
    private static List<Movie> mMovies;
    private List<Movie> mFavoriteMovies;
    private String mSortOption = "";
    private MovieAPIViewModel viewModel;

    public static final String bundle_token = "token";
    public static final String parcelable_token = "parcelable";
    public static final String movie_pos_token = "movie_pos";

    public static ProgressDialog progDailog;

    private final String sort_option_token = "mSortOption";

    private MovieDataBase movieDataBase;

    /**
     * Creates different objects needed for the MovieAdapter and request the popular movies
     * using a AsyncTask class
     * @param savedInstanceState bundle object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mSortOption = savedInstanceState.getString(sort_option_token);
        }

        // Setup preferences

        setupSharedPreferences();

        // Setup adapter

        RecyclerView mMovieGrid = findViewById(R.id.MovieRecyclerView);


        // If landscape is used the number of columns in the GridLayout will be
        // higher due to having more space

        int columns = 1;

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columns = 3;
        } else {
            columns = 2;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, columns);

        mMovieGrid.setLayoutManager(gridLayoutManager);

        mAdapter = new MovieAdapter(this);

        mMovieGrid.setAdapter(mAdapter);

        // Setup database

        movieDataBase = MovieDataBase.getInstance(getApplicationContext());

        // Setup movies lists. This will change with the use of ViewModel

        mMovies = new ArrayList<>();
        mFavoriteMovies = new ArrayList<>();

        setupViewModel();

        viewModel = ViewModelProviders.of(this).get(MovieAPIViewModel.class);

        // Query movies
        queryMovies(mSortOption);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // The movies (api_movies and fav_movies) will be handled by a ModelView
        // This way we don't need to store and recover them
//        outState.putParcelableArrayList(movies_token, Movies);

        outState.putString(sort_option_token, mSortOption);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mSortOption = savedInstanceState.getString(sort_option_token);

        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Inflates the menu that handles the movie sorting
     * @param menu to inflate
     * @return results of the menu inflating
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }

    /**
     * Method run when a menu item is clicked to sort the movies
     * @param item clicked
     * @return boolean result of the operation
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if(itemThatWasClickedId == R.id.action_settings_id){
            Intent startSettingActivityIntent = new Intent(this,SettingsActivity.class);
            startActivity(startSettingActivityIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupSharedPreferences(){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        mSortOption = sharedPreferences.getString(getString(R.string.sort_key),getString(R.string.sort_popular_label));
    }

    /**
     * Method in charge of creating a DetailActivity to presents the details of the movie
     * clicked by the user
     * @param clickedItemIndex index of the MovieViewHolder object clicked
     */

    @Override
    public void onGridItemClick(int clickedItemIndex) {

        Bundle bundle = new Bundle();

        if(mSortOption.equals(getString(R.string.sort_fav_value))){
            bundle.putParcelable(parcelable_token, mFavoriteMovies.get(clickedItemIndex));
        } else{
            bundle.putParcelable(parcelable_token, mMovies.get(clickedItemIndex));
        }

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

        intent.putExtra(bundle_token,bundle);

        intent.putExtra(movie_pos_token,clickedItemIndex);

        startActivity(intent);

    }

    // Fav -> rotate -> popular

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals((getString(R.string.sort_key)))) {

            mSortOption = sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_popular_label));

            queryMovies(mSortOption);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setupViewModel(){

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFav_movies().observe(this, new Observer<List<Movie>>() {

            // The logic of onChanged is the following:
            // We use ModelView to avoid non-necessary queries to the favorite movie database
            // Besides, we are going to use the onChanged method for two purposes:
            // 1) Update mFavoriteMovies when a movie is mark/unmarked as fav. Then we need just to
            // call mAdapter.update() when the sort option is fav movies
            // 2) If there's been a change, the on the data AND the sort option is fav movies
            // we call to mAdapter.update(). This is used when the app is started with this option
            // and when there's a movie deletion

            @Override
            public void onChanged(List<Movie> movies) {
                Log.d("VIEW_MODEL","Updating Fav Movies from ViewModel");
                Log.d("VIEW_MODEL",String.valueOf(movies.size()));

                mFavoriteMovies = movies;
                if (mSortOption.equals(getString(R.string.sort_fav_value))) {
                    mAdapter.updateData(mFavoriteMovies);
                }
            }
        });

    }

    public static void setMovies(List<Movie> m){
        mMovies = m;
    }

    private void queryMovies(final String query) {

        mSortOption = query;

        progDailog = new ProgressDialog(MainActivity.this);
        progDailog.setMessage("Loading " + mSortOption + " movies");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();

        // If fav movies are requested just need to update the adapter data
        // as we are using LiveData

        if (mSortOption.equals(getString(R.string.sort_fav_value))) {
            mAdapter.updateData(mFavoriteMovies);
            progDailog.dismiss();
        }

        else {
            if(isOnline()){
                viewModel.requestMovie(mSortOption);
            } else {

                // If there is no internet connection show message
                // Code obtained from this stackoverflow post
                // https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("There is no internet connection");
                builder.setCancelable(true);

                builder.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
            }

        }


        // Request the movies to the API
        /*else {
            // Check if there is online connection, otherwise show message
            if (isOnline()) {
                AppExecutor.getsInstance().networkIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            String jsonMovies;

                            switch (query) {
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
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.updateData(mMovies);
                                    progDailog.dismiss();
                                    Log.d("DONE__", "I UPDATED!!");
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {


            }

        }*/
    }

    /**
     * Checks if there is internet connection.
     * This method has been obtaion from this StackOverflow post:
     * https://stackoverflow.com/questions/9570237/android-check-internet-connection/24692766#24692766
     * @return true if there is internet connection, false otherwise
     */

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
