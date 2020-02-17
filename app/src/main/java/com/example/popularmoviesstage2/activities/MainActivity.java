package com.example.popularmoviesstage2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
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
 * Main Activity of the Popular Movies Stage 1 application
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter.GridItemClickListener {

    private MovieAdapter mAdapter;
    private RecyclerView mMovieGrid;
    private ArrayList<Movie> mMovies;

    private String previousSortOption = "";
    private String currentSortOption = "";

    public static String bundle_token = "token";
    public static String parcelable_token = "parcelable";

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

        previousSortOption = "";
        currentSortOption = NetworkUtils.popular;

        mMovies = new ArrayList<>();

        mMovieGrid = findViewById(R.id.MovieRecyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        mMovieGrid.setLayoutManager(gridLayoutManager);

        new FetchMoviesTask().execute(currentSortOption);

        // Create database

        db = new DataBaseHelper(this);
        db.clearDatabase();

    }

    /**
     * Inflates the menu that handles the movie sorting
     * @param menu to inflate
     * @return results of the menu inflating
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_sort_menu, menu);
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

        if (itemThatWasClickedId == R.id.sort_popularity){
            new FetchMoviesTask().execute(NetworkUtils.popular);
            return true;
        }

        if (itemThatWasClickedId == R.id.sort_rate){
            new FetchMoviesTask().execute(NetworkUtils.top_rated);
            return true;
        }

        if(itemThatWasClickedId == R.id.sort_fav){
            ArrayList<Movie> m = db.getMovies();

            if(m.size() > 0) {
                mAdapter.updateData(m);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize the MovieAdapter object.
     * Is only called when the movies are sorted in a different way. Example:
     *
     * Movies are sorted by popular. If I sort again using popular this method won't be called
     */
    private void initializeAdapter(){

        mAdapter = new MovieAdapter(mMovies.size(),this, mMovies);

        mMovieGrid.setAdapter(mAdapter);
    }

    /**
     * Method in charge of creating a DetailActivity to presents the details of the movie
     * clicked by the user
     * @param clickedItemIndex index of the MovieViewHolder object clicked
     */

    @Override
    public void onGridItemClick(int clickedItemIndex) {

        Bundle bundle = new Bundle();

        bundle.putParcelable(parcelable_token, mMovies.get(clickedItemIndex));

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);

        intent.putExtra(bundle_token,bundle);

        startActivity(intent);

    }

    /**
     * AsyncTask that request the movies to the API and initialize the MovieAdapter if needed
     */

    class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie> > {

        ProgressDialog progDailog;

        /**
         * Checks if there is internet connection.
         * This method has been obtaion from this StackOverflow post:
         * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
         * @return true if there is internet connection, false otherwise
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

                if (!previousSortOption.equals(strings[0])) {

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

                }

                return mMovies;
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
                if(previousSortOption.equals("")){
                    mMovies = movies;
                    initializeAdapter();
                }
                if(!currentSortOption.equals(previousSortOption)){
                    mMovies = movies;
                    mAdapter.updateData(movies);
                    //initializeAdapter();
                }
                previousSortOption = currentSortOption;
            }
        }
    }
}
