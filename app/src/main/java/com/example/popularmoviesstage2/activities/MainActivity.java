package com.example.popularmoviesstage2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.popularmoviesstage2.database.DataBaseHelper;
import com.example.popularmoviesstage2.database.MovieDataBase;
import com.example.popularmoviesstage2.movie.Movie;
import com.example.popularmoviesstage2.movie.MovieAdapter;
import com.example.popularmoviesstage2.R;
import com.example.popularmoviesstage2.utilities.AppExecutor;
import com.example.popularmoviesstage2.utilities.JsonMovieUtils;
import com.example.popularmoviesstage2.utilities.NetworkUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity of the Popular Movies Stage 2 application
 */
public class MainActivity extends AppCompatActivity
        implements MovieAdapter.GridItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static MovieAdapter mAdapter;
    private RecyclerView mMovieGrid;
    private List<Movie> mMovies;
    private List<Movie> mFavoriteMovies;

    public static final String bundle_token = "token";
    public static final String parcelable_token = "parcelable";
    public static final String movie_pos_token = "movie_pos";

    private String sort_option = "";
    private final String movies_token = "movies";
    private final String sort_option_token = "sort_option";

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
            sort_option = savedInstanceState.getString(sort_option_token);
        }

        // Setup adapter

        mMovieGrid = findViewById(R.id.MovieRecyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        mMovieGrid.setLayoutManager(gridLayoutManager);

        mAdapter = new MovieAdapter(this);

        // Setup preferences
        setupSharedPreferences();

        // Setup movies lists. This will change with the use of ViewModel

        mMovies = new ArrayList<>();
     //   mFavoriteMovies = new ArrayList<>();

        // Setup database

        movieDataBase = MovieDataBase.getInstance(getApplicationContext());

        // Retrieve fav_movies and set LiveData observer

        retrieveFavoriteMovies();

        queryMovies(sort_option);

        mMovieGrid.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        // The movies (api_movies and fav_movies) will be handled by a ModelView
        // This way we don't need to store and recover them
//        outState.putParcelableArrayList(movies_token, Movies);

        outState.putString(sort_option_token,sort_option);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        sort_option = savedInstanceState.getString(sort_option_token);

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

        sort_option = sharedPreferences.getString(getString(R.string.sort_key),getString(R.string.sort_popular_label));
    }

    /**
     * Method in charge of creating a DetailActivity to presents the details of the movie
     * clicked by the user
     * @param clickedItemIndex index of the MovieViewHolder object clicked
     */

    @Override
    public void onGridItemClick(int clickedItemIndex) {

        Bundle bundle = new Bundle();

        if(sort_option.equals(getString(R.string.sort_fav_value))){
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

            sort_option = sharedPreferences.getString(getString(R.string.sort_key), getString(R.string.sort_popular_label));

            queryMovies(sort_option);
/*
            if (sort_option.equals(getString(R.string.sort_popular_value))) {
                new FetchMoviesTask().execute(NetworkUtils.popular);
            }

            if (sort_option.equals(getString(R.string.sort_top_rated_value))) {
                new FetchMoviesTask().execute(NetworkUtils.top_rated);
            }

            if (sort_option.equals(getString(R.string.sort_fav_value))) {
                mAdapter.updateData(mFavoriteMovies);
                //mAdapter.notifyDataSetChanged();
                //retrieveFavoriteMovies();
            }*/
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void retrieveFavoriteMovies(){
        final LiveData<List<Movie>> movies = movieDataBase.movieDAO().getMovies();

        movies.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                    mFavoriteMovies = movies;
            }
        });

    }

    private void QueryFavMovies() {

    }

    private void queryMovies(final String query){

        sort_option = query;
        final ProgressDialog progDailog = new ProgressDialog(MainActivity.this);
        progDailog.setMessage("Loading " + sort_option + " movies");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(true);
        progDailog.show();

        if(sort_option.equals(getString(R.string.sort_fav_value))){
            AppExecutor.getsInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("DONE__", "diskIO calls to updateData");
                    mFavoriteMovies = movieDataBase.movieDAO().getMoviesNoLiveData();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.updateData(mFavoriteMovies);
                            progDailog.dismiss();
                            Log.d("DONE__", "I UPDATED FAV MOVIES!!");
                        }
                    });
                }
            });
        }

        else {
            AppExecutor.getsInstance().networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        Log.d("DONE__", "START QUERY " + query);

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

                        Log.d("DONE__", "IM GOING TO UPDATE!!");

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
                        Log.d("DONE__", "I FAILED!!");
                        e.printStackTrace();
                    }
                }
            });
        }

    }
    /**
     * AsyncTask that request the movies to the API and initialize the MovieAdapter if needed
     */

    class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie> > {

        ProgressDialog progDailog;


        /**
         * Creates and show the ProgressDialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Loading movies!!");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
        }

        /**
         * Checks if there is internet connection.
         * This method has been obtaion from this StackOverflow post:
         * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
         * @return true if there is internet connection, false otherwise
         */

        private boolean isOnline() {
            try {
                int timeoutMs = 1500;
                Socket sock = new Socket();
                SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

                sock.connect(sockaddr, timeoutMs);
                sock.close();

                return true;
            } catch (IOException e) { return false; }
        }

        /**
         * Fetch the movie data from the API and returns an ArrayList of Movies
         *
         * It uses previousSortOption and currentSortOption to avoid unnecessary calls to the API
         * @param strings option to sort the movies
         * @return ArrayList of Movies
         */

        @Override
        protected ArrayList<Movie> doInBackground(String... strings) {

            if (isOnline()) {

                if (strings.length == 0) {
                    return null;
                }

      //          if (!previousSortOption.equals(strings[0])) {

                    try {

                        sort_option = strings[0];

                        String jsonMovies;

                        switch (strings[0]) {
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

                        return JsonMovieUtils.parseMoviesJsonArray(jsonMovies);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }

        //        }

          //      return mMovies;
            }
            return null;
        }

        /**
         * Stores the movies in the mMovies variable from the MainActivity class and initialize
         * the MovieAdapter if needed. Updates previousSortOption
         * @param movies ArrayList of Movies
         */

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            progDailog.dismiss();

            if (movies != null && movies.size() > 0) {
                mAdapter.updateData(movies);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
