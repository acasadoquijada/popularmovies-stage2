package com.example.popularmoviesstage2.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import com.example.popularmoviesstage2.movie.Movie;
import com.example.popularmoviesstage2.movie.MovieAdapter;
import com.example.popularmoviesstage2.R;
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
    private MovieAPIViewModel mAPIMoviesViewModel;

    public static final String bundle_token = "token";
    public static final String parcelable_token = "parcelable";
    public static final String movie_pos_token = "movie_pos";

    public static ProgressDialog progDailog;

    private final String sort_option_token = "mSortOption";

    /**
     * Initialize everything needed
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

        int columns;

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

        // Setup movies lists

        mMovies = new ArrayList<>();
        mFavoriteMovies = new ArrayList<>();

        // Setup ViewModels

        setupFavMoviesViewModel();

        mAPIMoviesViewModel = ViewModelProviders.of(this).get(MovieAPIViewModel.class);

        // Query movies
        queryMovies(mSortOption);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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

    /**
     * Setup the shared preferences and obtain the sort option for querying the movies
     */

    private void setupSharedPreferences(){

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        mSortOption =
                sharedPreferences.getString(getString(R.string.sort_key),getString(R.string.sort_popular_value));
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

    /**
     * Obtain the query option selected by the user by using the Setting Activity
     * @param sharedPreferences needed to extract the option selected by the user
     * @param key identifies the option selected by the user
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals((getString(R.string.sort_key)))) {

            String query =
                    sharedPreferences.getString(
                            getString(R.string.sort_key),
                            getString(R.string.sort_popular_label));

            queryMovies(query);
        }
    }

    /**
     * Unregister the onSharedPreferenceChangeListener
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Setup everything to ensure we get notified of the LiveData object changes
     */

    private void setupFavMoviesViewModel(){

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
                mFavoriteMovies = movies;
                if (mSortOption.equals(getString(R.string.sort_fav_value))) {
                    mAdapter.updateData(mFavoriteMovies);
                }
            }
        });
    }

    /**
     * This method is used in MovieAPIViewModel to ensure we don't have inconsistent state
     * that may lead to app crashes
     * @param m this are the movies queried by the MovieAPIViewModel class
     */
    public static void setMovies(List<Movie> m){
        mMovies = m;
    }

    /**
     * In charge of querying the movies according to the user's selection
     * @param query option selected by the user
     */
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
            if(NetworkUtils.isOnline()){
                mAPIMoviesViewModel.requestMovie(mSortOption);
            } else {

                // If there is no internet connection show message
                // Code obtained from this stackoverflow post
                // https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android

                progDailog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("There is no internet connection. " +
                        "\nPlease connect to the internet and restart the application");
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
    }
}