package com.example.popularmoviesstage2.activities;

import androidx.appcompat.app.AppCompatActivity;
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
import com.example.popularmoviesstage2.movie.Movie;
import com.example.popularmoviesstage2.movie.MovieAdapter;
import com.example.popularmoviesstage2.R;
import com.example.popularmoviesstage2.utilities.JsonMovieUtils;
import com.example.popularmoviesstage2.utilities.NetworkUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

/**
 * Main Activity of the Popular Movies Stage 2 application
 */
public class MainActivity extends AppCompatActivity
        implements MovieAdapter.GridItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static MovieAdapter mAdapter;
    private RecyclerView mMovieGrid;
    private ArrayList<Movie> mMovies;
    private ArrayList<Movie> mFavoriteMovies;
    private ArrayList<Movie> mMyMovies;

    private String previousSortOption = "";
    private String currentSortOption = "";
    private String appName = "";
    private boolean favoriteMoviesShowing;

    public static final String bundle_token = "token";
    public static final String parcelable_token = "parcelable";
    public static final String movie_pos_token = "movie_pos";

    private final String movies_token = "movies";
    private final String favorite_movies_token = "favorite_movies";
    private final String favorite_movies_showing_token = "favorite_movies_showing";
    private final String previous_sort_option_token = "previousSortOption";
    private final String current_sort_option_token = "currentSortOption";
    private final String title_token = "title";

    public static DataBaseHelper db;

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
            mMovies = savedInstanceState.getParcelableArrayList(movies_token);
            mFavoriteMovies = savedInstanceState.getParcelableArrayList(favorite_movies_token);
            previousSortOption = savedInstanceState.getString(previous_sort_option_token);
            currentSortOption = savedInstanceState.getString(current_sort_option_token);
            favoriteMoviesShowing = savedInstanceState.getBoolean(favorite_movies_showing_token);

        } else{
            previousSortOption = "";
            mMovies = new ArrayList<>();
            mFavoriteMovies = new ArrayList<>();

            favoriteMoviesShowing = false;
        }

        setupSharedPreferences();

        mMyMovies = new ArrayList<>();
        // Create database

        db = new DataBaseHelper(this);
        mMovieGrid = findViewById(R.id.MovieRecyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        mMovieGrid.setLayoutManager(gridLayoutManager);

        // Get the movies according to the sort option selected

        if(currentSortOption.equals(getString(R.string.sort_fav_value))){
            mFavoriteMovies = db.getMovies();
            mAdapter = new MovieAdapter(mFavoriteMovies.size(),this, mFavoriteMovies);

        } else{
            mAdapter = new MovieAdapter(mMovies.size(),this, mMovies);
            new FetchMoviesTask().execute(currentSortOption);
        }

        mMovieGrid.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(movies_token, mMovies);
        outState.putParcelableArrayList(favorite_movies_token, mFavoriteMovies);

        outState.putString(previous_sort_option_token, previousSortOption);
        outState.putString(current_sort_option_token, currentSortOption);
        outState.putBoolean(favorite_movies_showing_token,favoriteMoviesShowing);
        outState.putString(title_token, appName);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mMovies = savedInstanceState.getParcelableArrayList(movies_token);
        mFavoriteMovies = savedInstanceState.getParcelableArrayList(favorite_movies_token);

        appName = savedInstanceState.getString(title_token);
        previousSortOption = savedInstanceState.getString(previous_sort_option_token);
        currentSortOption = savedInstanceState.getString(current_sort_option_token);

        favoriteMoviesShowing = savedInstanceState.getBoolean(favorite_movies_showing_token);
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

        currentSortOption =
                sharedPreferences.getString(getString(R.string.sort_key),getString(R.string.sort_popular_label));

    }


    /**
     * Method in charge of creating a DetailActivity to presents the details of the movie
     * clicked by the user
     * @param clickedItemIndex index of the MovieViewHolder object clicked
     */

    @Override
    public void onGridItemClick(int clickedItemIndex) {

        Bundle bundle = new Bundle();

        if(favoriteMoviesShowing){
            bundle.putParcelable(parcelable_token, mFavoriteMovies.get(clickedItemIndex));
        } else{
            bundle.putParcelable(parcelable_token, mMovies.get(clickedItemIndex));
        }

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

        intent.putExtra(bundle_token,bundle);

        intent.putExtra(movie_pos_token,clickedItemIndex);

        startActivity(intent);

    }

    private boolean needToRequestMovies(String current, String previous){
        return false;
    }

    // Fav -> rotate -> popular

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals((getString(R.string.sort_key)))){

            String option = sharedPreferences.getString(getString(R.string.sort_key),getString(R.string.sort_popular_label));

            if(option.equals(getString(R.string.sort_popular_value))) {
                new FetchMoviesTask().execute(NetworkUtils.popular);
            }

            if(option.equals(getString(R.string.sort_top_rated_value))) {
                new FetchMoviesTask().execute(NetworkUtils.top_rated);
            }

            if(option.equals(getString(R.string.sort_fav_value))) {
                ArrayList<Movie> m = db.getMovies();

                if(m.size() > 0) {
                    mFavoriteMovies = m;
                    mAdapter.updateData(mFavoriteMovies);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
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

                        currentSortOption = strings[0];

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

            if (movies != null) {
                mAdapter.updateData(movies);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
