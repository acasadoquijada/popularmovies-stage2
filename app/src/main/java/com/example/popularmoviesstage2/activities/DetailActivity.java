package com.example.popularmoviesstage2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.popularmoviesstage2.movie.Movie;
import com.example.popularmoviesstage2.R;
import com.squareup.picasso.Picasso;

/**
 * Activity class that presents the movie details to the user
 *
 */

public class DetailActivity extends AppCompatActivity {

    Movie movie;
    ToggleButton toggle;
    private String toogle_button_token = "toogle_button";
    private boolean toggle_button_pressed;

    /**
     * onCreate method run when the Activity is created
     * To avoid errors such as: Android E/Parcel﹕ Class not found when unmarshalling
     *
     * It reconstructs a Movie object passed as Parcelable within a Bundle
     *
     * This StackOverflow post have been followed:
     * https://stackoverflow.com/questions/28589509/
     * android-e-parcel-class-not-found-when-unmarshalling-only-on-samsung-tab3
     *
     * @param savedInstanceState bundle object
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView textViewTitle;
        TextView textViewOverview;
        TextView textViewReleaseDate;
        TextView textViewVoteAverage;
        ImageView imageViewMoviePoster;
        LinearLayout bottomLinearLayout;
        Intent intent;

        intent = getIntent();

        if (intent != null) {

            Bundle b = intent.getBundleExtra(MainActivity.bundle_token);

            if (b != null && b.getParcelable(MainActivity.parcelable_token) != null) {

                movie = b.getParcelable(MainActivity.parcelable_token);

                if (movie != null) {
                    // Movie poster
                    imageViewMoviePoster = findViewById(R.id.movie_poster);
                    Picasso.get().load(movie.getPoster_path()).into(imageViewMoviePoster);

                    // Movie original_title
                    textViewTitle = findViewById(R.id.title);
                    textViewTitle.setText(movie.getOriginal_title());

                    // Movie overview
                    textViewOverview = findViewById(R.id.overview);
                    textViewOverview.setText(movie.getOverview());

                    // Movie release date
                    String release_date_help_text = "Release date: ";
                    textViewReleaseDate = findViewById(R.id.release_date);
                    textViewReleaseDate.setText(release_date_help_text);
                    textViewReleaseDate.append(movie.getRelease_date());

                    // Movie vote average
                    String vote_average_help_text = "Vote average: ";
                    textViewVoteAverage = findViewById(R.id.vote_average);
                    textViewVoteAverage.setText(vote_average_help_text);
                    textViewVoteAverage.append(movie.getVote_average() + "/10");

                    // Movie trailers
                    Log.d("REVIEWS",movie.getReviews().toString());


                    ToggleButton toggle = (ToggleButton) findViewById(R.id.fav_togglebutton);
                    Log.d("PRESSED 3",String.valueOf(toggle_button_pressed));



                    if (savedInstanceState != null) {
                        toggle_button_pressed = savedInstanceState.getBoolean(toogle_button_token);
                    } else{
                        toggle_button_pressed = MainActivity.db.movieInDataBase(movie);
                    }

                    toggle.setChecked(toggle_button_pressed);


                    toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                MainActivity.db.insertMovie(movie);
                                toggle_button_pressed = true;


                            } else {
                                MainActivity.db.deleteMovie(movie);
                                toggle_button_pressed = false;

                            }
                        }
                    });



                    /*
                    //Movie reviews
                    String review_text = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
                    bottomLinearLayout = findViewById(R.id.bottom_linear_layout);

                    TextView review_section_title = new TextView(this);

                    review_section_title.setLayoutParams(
                            new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));

                    review_section_title.setText(review_text);

                    review_section_title.setPadding(20,0,0,0);

                    review_section_title.setTextColor(getResources().getColor(android.R.color.black));

                    bottomLinearLayout.addView(review_section_title,bottomLinearLayout.getChildCount());
                    */

                }

            }

        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        toggle_button_pressed = (savedInstanceState.getBoolean(toogle_button_token));
        Log.d("PRESSED 2",String.valueOf(toggle_button_pressed));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        Log.d("PRESSED 1",String.valueOf(toggle_button_pressed));
        outState.putBoolean(toogle_button_token, toggle_button_pressed);
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }


}
