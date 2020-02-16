package com.example.popularmoviesstage2.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.popularmoviesstage2.movie.Movie;
import com.example.popularmoviesstage2.R;
import com.squareup.picasso.Picasso;

/**
 * Activity class that presents the movie details to the user
 *
 */

public class DetailActivity extends AppCompatActivity {

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
        Movie movie;

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

                }

            }

        }
    }
}
