package com.example.popularmoviesstage2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.popularmoviesstage2.database.MovieDataBase;
import com.example.popularmoviesstage2.movie.Movie;
import com.example.popularmoviesstage2.R;
import com.example.popularmoviesstage2.utilities.AppExecutor;
import com.squareup.picasso.Picasso;

/**
 * Activity class that presents the movie details to the user
 *
 */

public class DetailActivity extends AppCompatActivity {

    private Movie movie;
    private MovieDataBase movieDataBase;
    private final String toogle_button_token = "toogle_button";
    private boolean toggle_button_pressed;
    private int pos;

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

        movieDataBase = MovieDataBase.getInstance(getApplicationContext());

        Intent intent;

        intent = getIntent();

        if (intent != null) {

            Bundle b = intent.getBundleExtra(MainActivity.bundle_token);

            if (b != null && b.getParcelable(MainActivity.parcelable_token) != null) {

                movie = b.getParcelable(MainActivity.parcelable_token);

                pos = intent.getIntExtra(MainActivity.movie_pos_token,-1);

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
                    final ToggleButton toggle = findViewById(R.id.fav_togglebutton);

                    // We check if the movie is in the fav database. If so, we update the toggle
                    setUpToggleButton(toggle);
                    // Add the trailers and reviews to the layout
                    addTrailers(movie);
                    addReviews(movie);
                }

            }

        }
    }

    private void setUpToggleButton(final ToggleButton toggleButton){
        updateToggleButton(toggleButton);
        setOnCheckedChangeListener(toggleButton);
    }

    private void setOnCheckedChangeListener(final ToggleButton toggleButton){

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppExecutor.getsInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            if(movieDataBase.movieDAO().getMovie(movie.getId()) == null){
                                movieDataBase.movieDAO().insertMovie(movie);
                            }
                        }
                    });
                } else {
                    AppExecutor.getsInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            movieDataBase.movieDAO().deleteMovie(movie);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.mAdapter.removeMovie(pos);
                                }
                            });
                        }
                    });
                }
            }
        });
    }
    private void updateToggleButton(final ToggleButton toggle) {
        AppExecutor.getsInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {

                if (movieDataBase.movieDAO().getMovie(movie.getId()) != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggle.setChecked(true);
                        }
                    });
                }
            }
        });
    }

    private void noReviewOrTrailer(int layout_id){

        LinearLayout contraConstraintLayout = findViewById(layout_id);

        TextView textView = new TextView(this);

        String no_reviews_text = "";
        if(layout_id == R.id.review_linear_layout){
            no_reviews_text = "There is no reviews for this movie"; // ADD TO STRINGS.xml

        } else if(layout_id == R.id.trailer_linear_layout){
            no_reviews_text = "There is no trailers for this movie"; // ADD TO STRINGS.xml
        }
        textView.setText(no_reviews_text);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        textView.setLayoutParams(layoutParams);

        contraConstraintLayout.addView(textView,contraConstraintLayout.getChildCount());
    }
    private void addReviews(Movie movie){

        // Get the parent layout

        LinearLayout contraConstraintLayout = findViewById(R.id.review_linear_layout);

        if(contraConstraintLayout != null) {

            // This is because the reviews are stored as follow:
            // 0: Author, 1: Review, 2: Author, 3: Review...
            // See parseContentReview and parseAuthor review for more details

            int review_number = movie.getReviews().size() / 2;

            if(review_number == 0){
                noReviewOrTrailer(R.id.review_linear_layout);
            }

            for (int i = 0; i < review_number; i+=2) {
                // Inflate review_layout

                LayoutInflater inflater = LayoutInflater.from(this);

                View reviewLayout = inflater.inflate(R.layout.review_layout, null);

                // Get the review author textView

                TextView authorTextView = reviewLayout.findViewById(R.id.review_author);

                // Get the review content textView

                TextView contentTextView = reviewLayout.findViewById(R.id.review_text);

                // Set review author text

                String author_review = movie.getReviews().get(i);

                authorTextView.setText(author_review);

                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD_ITALIC);

                authorTextView.setTypeface(boldTypeface);

                // Set review content text

                String content_review = movie.getReviews().get(i + 1);

                contentTextView.setText(content_review);

                // Set review layout params

                ConstraintLayout.LayoutParams l = new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                reviewLayout.setLayoutParams(l);

                contraConstraintLayout.addView(reviewLayout, contraConstraintLayout.getChildCount());
            }
        }
    }

    private void addTrailers(Movie movie){

        // This is because the trailers are stored as follow:
        // 0: Name, 1: Trailer, 2: Name, 3: Trailer...
        // See parseTrailerURL and parseTrailerName review for more details
        int trailer_number = movie.getTrailers().size()/2;

        if(trailer_number == 0){
            noReviewOrTrailer(R.id.trailer_linear_layout);
        }

        LinearLayout contraConstraintLayout = findViewById(R.id.trailer_linear_layout);

        for(int i = 0; i < trailer_number; i+=2) {

            // Inflate trailer_layout

            LayoutInflater inflater = LayoutInflater.from(this);

            View trailerLayout = inflater.inflate(R.layout.trailer_layout, null);

            // Get the trailer textView

            TextView trailerTextView = trailerLayout.findViewById(R.id.trailer_text_view);

            // Set trailer textView text
            trailerTextView.setText(movie.getTrailers().get(i));

            setOnClick(trailerLayout,movie.getTrailers().get(i+1));

            // Set trailer layout params

            ConstraintLayout.LayoutParams l = new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            trailerLayout.setLayoutParams(l);

            contraConstraintLayout.addView(trailerLayout, contraConstraintLayout.getChildCount());

        }
    }

    /**
     * onClick method to open a trailer in the Youtube app
     * @param imageView corresponding to the trailer
     * @param trailer_path trailer youtube link
     */
    private void setOnClick(final View imageView, final String trailer_path){

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
     * Convert the dp unit to px.
     *
     * Conversion formula obtained here:
     * https://stackoverflow.com/questions/4275797/
     * view-setpadding-accepts-only-in-px-is-there-anyway-to-setpadding-in-dp
     * @param dp to be converted into px
     * @return px
     */

    private int calculatePX(int dp){

        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * Adds the views necessaries to display the reviews of a given movie
     * movie to obtain the reviews from
     */

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
