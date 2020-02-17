package com.example.popularmoviesstage2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.popularmoviesstage2.movie.Movie;
import com.example.popularmoviesstage2.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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

                    // Toggle button for favorite movie
                    ToggleButton toggle = (ToggleButton) findViewById(R.id.fav_togglebutton);


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

                    // Movie trailers
                    AddMovieTrailers(movie);
                    // Movie reviews
                    addMovieReviews(movie);

                }

            }

        }
    }


    private void AddMovieTrailers(Movie movie){

        // Get parent layout
        LinearLayout bottomLinearLayout = findViewById(R.id.bottom_linear_layout);

        TextView trailer_section_title = new TextView(this);

        // Add a title for the review trailer
        String trailer_section_title_text = "Trailers";

        trailer_section_title.setText(trailer_section_title_text);

        // Conversion formula obtained here:
        // https://stackoverflow.com/questions/4275797/
        // view-setpadding-accepts-only-in-px-is-there-anyway-to-setpadding-in-dp

        int padding_in_dp = 20;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        trailer_section_title.setPadding(padding_in_px,0,0,0);

        trailer_section_title.setTextColor(getResources().getColor(android.R.color.black));

        trailer_section_title.setTextSize(25);

        bottomLinearLayout.addView(trailer_section_title,bottomLinearLayout.getChildCount());

        // Create LinearLayout for the Trailers

        LinearLayout parent = new LinearLayout(this);

        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.setPadding(padding_in_px,padding_in_px,padding_in_px,padding_in_px);

        parent.setOrientation(LinearLayout.VERTICAL);

        parent.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        parent.setDividerDrawable(getResources().getDrawable(R.drawable.shape));

        // Iterate over the reviews and add them as textviews

        padding_in_dp = 15;
        padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        for(int i = 0; i < movie.getTrailers().size(); i++){
            ImageView trailerImageView = new ImageView(this);
            TextView trailerTextView = new TextView(this);

            trailerTextView.append("Trailer" + " " + (i+1));

            trailerTextView.setPadding(0,padding_in_px,0,padding_in_px);

            //trailerImageView.setText(movie.getTrailers().get(i));

            trailerImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_video_poster));

            trailerImageView.setPadding(0,padding_in_px,0,padding_in_px);

            setOnClick(trailerImageView,movie.getTrailers().get(i));

            parent.addView(trailerTextView,parent.getChildCount());
            parent.addView(trailerImageView,parent.getChildCount());
        }

        bottomLinearLayout.addView(parent,bottomLinearLayout.getChildCount());
    }

    private void setOnClick(final ImageView imageView, final String trailer_path){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(trailer_path));
                DetailActivity.this.startActivity(webIntent);
            }
        });
    }

    private void addMovieReviews(Movie movie){


        // Get parent layout
        LinearLayout bottomLinearLayout = findViewById(R.id.bottom_linear_layout);

        TextView review_section_title = new TextView(this);

        // Add a title for the review section
        String review_section_title_text = "Reviews";

        review_section_title.setText(review_section_title_text);

        // Conversion formula obtained here:
        // https://stackoverflow.com/questions/4275797/
        // view-setpadding-accepts-only-in-px-is-there-anyway-to-setpadding-in-dp

        int padding_in_dp = 20;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        review_section_title.setPadding(padding_in_px,0,0,0);

        review_section_title.setTextColor(getResources().getColor(android.R.color.black));

        review_section_title.setTextSize(25);

        bottomLinearLayout.addView(review_section_title,bottomLinearLayout.getChildCount());

        // Create LinearLayout for the Reviews

        LinearLayout parent = new LinearLayout(this);

        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.setPadding(padding_in_px,padding_in_px,padding_in_px,padding_in_px);


        parent.setOrientation(LinearLayout.VERTICAL);

        parent.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        parent.setDividerDrawable(getResources().getDrawable(R.drawable.shape));


        // Iterate over the reviews and add them as textviews

        padding_in_dp = 15;
        padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        for(int i = 0; i < movie.getReviews().size(); i++){
            TextView reviewTextView = new TextView(this);

            reviewTextView.setText(movie.getReviews().get(i));

            reviewTextView.setPadding(0,padding_in_px,0,padding_in_px);

            parent.addView(reviewTextView,parent.getChildCount());
        }

        bottomLinearLayout.addView(parent,bottomLinearLayout.getChildCount());
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
