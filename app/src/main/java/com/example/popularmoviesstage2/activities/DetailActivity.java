package com.example.popularmoviesstage2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
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

    private Movie movie;
    private final String toogle_button_token = "toogle_button";
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
                    ToggleButton toggle = findViewById(R.id.fav_togglebutton);


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


    /**
     * Adds the views necessaries to display the trailers of a given movie
     * @param movie to obtain the trailers from
     */
    private void AddMovieTrailers(Movie movie){

        // Get parent layout
        LinearLayout bottomLinearLayout = findViewById(R.id.bottom_linear_layout);

        // Create TextView to present trailer section
        TextView trailer_section_title = getTitleSection("Trailers");
        bottomLinearLayout.addView(trailer_section_title,bottomLinearLayout.getChildCount());

        // Create LinearLayout for the Trailers

        LinearLayout trailerLinearLayout = createLinearLayout();

        // Iterate over the trailers

        int padding_in_dp = 15;
        int padding_in_px = calculatePX(padding_in_dp);

        for(int i = 0; i < movie.getTrailers().size(); i++){
            ImageView trailerImageView = new ImageView(this);
            TextView trailerTextView = new TextView(this);

            trailerTextView.append("Trailer" + " " + (i+1));

            trailerTextView.setPadding(0,padding_in_px,0,padding_in_px);

            trailerImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_media_video_poster));

            trailerImageView.setPadding(0,padding_in_px,0,padding_in_px);

            setOnClick(trailerImageView,movie.getTrailers().get(i));

            trailerLinearLayout.addView(trailerTextView,trailerLinearLayout.getChildCount());
            trailerLinearLayout.addView(trailerImageView,trailerLinearLayout.getChildCount());
        }

        bottomLinearLayout.addView(trailerLinearLayout,bottomLinearLayout.getChildCount());
    }

    /**
     * onClick method to open a trailer in the Youtube app
     * @param imageView corresponding to the trailer
     * @param trailer_path trailer youtube link
     */
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

    /**
     * Creates a LinearLayout. This is used for the Reviews and Trailers.
     * @return a LinearLayout
     */

    private LinearLayout createLinearLayout(){

        LinearLayout linearLayout = new LinearLayout(this);

        linearLayout.setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

        int padding_in_dp = 20;
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

        linearLayout.setPadding(padding_in_px,padding_in_px,padding_in_px,padding_in_px);

        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);

        linearLayout.setDividerDrawable(getResources().getDrawable(R.drawable.shape));

        return linearLayout;
    }

    /**
     * Creates a TextView that works as a title for a new section.
     * This is used for Reviews and trailers
     * @param section_name to be set in the TextView
     * @return a TextView object
     */
    private TextView getTitleSection(String section_name){

        TextView section_title = new TextView(this);

        section_title.setText(section_name);

        int padding_in_dp = 20;
        int padding_in_px = calculatePX(padding_in_dp);

        section_title.setPadding(padding_in_px,0,0,0);

        section_title.setTextColor(getResources().getColor(android.R.color.black));

        section_title.setTextSize(25);

        return section_title;
    }

    /**
     * Convert the dp unit to px.
     *
     * Conversion formula obtained here:
     * https://stackoverflow.com/questions/4275797/
     * view-setpadding-accepts-only-in-px-is-there-anyway-to-setpadding-in-dp
     * @param dp to be converted into px
     * @return px
     */

    private int calculatePX(int dp){
        // Conversion formula obtained here:
        // https://stackoverflow.com/questions/4275797/
        // view-setpadding-accepts-only-in-px-is-there-anyway-to-setpadding-in-dp

        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Adds the views necessaries to display the reviews of a given movie
     * @param movie to obtain the reviews from
     */
    private void addMovieReviews(Movie movie){

        // Get trailerLinearLayout layout
        LinearLayout bottomLinearLayout = findViewById(R.id.bottom_linear_layout);

        TextView review_section_title = getTitleSection("Reviews");
        bottomLinearLayout.addView(review_section_title,bottomLinearLayout.getChildCount());

        // Create LinearLayout for the Reviews

        LinearLayout reviewLinearLayout = createLinearLayout();

        // Iterate over the reviews and add them as textviews

        int padding_in_dp = 15;
        int padding_in_px = calculatePX(padding_in_dp);

        // In case there is not reviews we let the user know
        if(movie.getReviews().size() == 0){
            TextView reviewTextView = new TextView(this);

            String no_reviews = "There is no reviews for this movie";
            reviewTextView.setText(no_reviews);

            reviewTextView.setPadding(0,padding_in_px,0,padding_in_px);

            reviewLinearLayout.addView(reviewTextView,reviewLinearLayout.getChildCount());
        } else {
            for(int i = 0; i < movie.getReviews().size(); i++){
                TextView reviewTextView = new TextView(this);

                reviewTextView.setText(movie.getReviews().get(i));

                reviewTextView.setPadding(0,padding_in_px,0,padding_in_px);

                reviewLinearLayout.addView(reviewTextView,reviewLinearLayout.getChildCount());
            }
        }

        bottomLinearLayout.addView(reviewLinearLayout,bottomLinearLayout.getChildCount());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        toggle_button_pressed = (savedInstanceState.getBoolean(toogle_button_token));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(toogle_button_token, toggle_button_pressed);
        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }


}
