package com.example.popularmoviesstage2.utilities;


import com.example.popularmoviesstage2.movie.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Class in charge of creating the movies from JSON
 */

public class JsonMovieUtils {

    private final static String results_token = "results";
    private final static String popularity_token = "popularity";
    private final static String vote_count_token = "vote_count";
    private final static String video_token = "video";
    private final static String poster_path_token = "poster_path";
    private final static String id_token = "id";
    private final static String adult_token = "adult";
    private final static String backdrop_path_token = "backdrop_path";
    private final static String original_language_token = "original_language";
    private final static String original_title_token= "original_title";
    private final static String title_token = "title";
    private final static String vote_average_token = "vote_average";
    private final static String overview_token= "overview";
    private final static String release_date_token = "release_date";
    private final static String key_token = "key";
    private final static String review_content_key = "content";


    /**
     * Creates a ArrayList of Movie objects from a String containg the movies in JSON
     * @param json String containing the movies
     * @return an ArrayList of Movie objects
     */
    public static ArrayList<Movie> parseMoviesJsonArray(String json) {

        try {

            ArrayList<Movie> movies = new ArrayList<>();

            // Obtain a JSONArray with all the movies, iterate and create movies
            JSONObject JSONObjectResultQuery = new JSONObject(json);
            JSONArray JSONArrayMovies = JSONObjectResultQuery.getJSONArray(results_token);

            for (int i = 0; i < JSONArrayMovies.length(); i++) {

                JSONObject jsonMovie = JSONArrayMovies.getJSONObject(i);

                Movie movie = parseMovie(jsonMovie);

                if(movie != null){
                    String trailers_string = "";
                    String reviews_string = "";

                    try {
                        trailers_string = NetworkUtils.getTrailersMovie(movie.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    movie.setTrailers(parseTrailersJsonArray(trailers_string));

                    try {
                        reviews_string = NetworkUtils.getReviewsMovie(movie.getId());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    movie.setReviews(parseReviewJsonArray(reviews_string));

                    movies.add(movie);
                }
            }

            return movies;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a Movie object from a JSONObject
     * @param jsonMovie JSONObject with the movie
     * @return a Movie object
     */
    static private Movie parseMovie(JSONObject jsonMovie) {

        try {

            Movie movie = new Movie();
            int popularity;
            int vote_count;
            boolean video;
            String poster_path;
            int id;
            boolean adult;
            String backdrop_path;
            String original_language;
            String original_title;
            String title;
            double vote_average;
            String overview;
            String release_date;

            // Popularity
            popularity = jsonMovie.getInt(popularity_token);

            movie.setPopularity(popularity);

            // Vote count
            vote_count = jsonMovie.getInt(vote_count_token);

            movie.setVote_count(vote_count);

            // Video
            video = jsonMovie.getBoolean(video_token);

            movie.setVideo(video);

            // Poster path
            poster_path = jsonMovie.getString(poster_path_token);

            movie.setPoster_path(poster_path);

            // ID
            id = jsonMovie.getInt(id_token);

            movie.setId(id);

            // Adult
            adult = jsonMovie.getBoolean(adult_token);

            movie.setAdult(adult);

            // Backdrop path
            backdrop_path = jsonMovie.getString(backdrop_path_token);

            movie.setBackdrop_path(backdrop_path);

            // Original language
            original_language = jsonMovie.getString(original_language_token);

            movie.setOriginal_language(original_language);

            // Original title
            original_title = jsonMovie.getString(original_title_token);

            movie.setOriginal_title(original_title);

            // Title
            title = jsonMovie.getString(title_token);

            movie.setOriginal_language(title);

            // Vote average
            vote_average = jsonMovie.getDouble(vote_average_token);

            movie.setVote_average(vote_average);

            // Overview
            overview = jsonMovie.getString(overview_token);

            movie.setOverview(overview);

            // Release date
            release_date = jsonMovie.getString(release_date_token);

            movie.setRelease_date(release_date);

            return movie;

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * Return an ArrayList of strings with all the trailers
     * @param string_trailers JSONArray containing the trailers
     * @return ArrayList with all the trailers
     */
    static private ArrayList<String> parseTrailersJsonArray(String string_trailers){

        try {

            ArrayList<String> trailers = new ArrayList<>();

            JSONObject JSONObjectResultQuery = new JSONObject(string_trailers);
            JSONArray JSONTrailerArray = JSONObjectResultQuery.getJSONArray(results_token);


            for (int i = 0; i < JSONTrailerArray.length(); i++) {

                JSONObject jsonTrailer = JSONTrailerArray.getJSONObject(i);

                trailers.add(parseTrailer(jsonTrailer));
            }

            return trailers;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Obtains a trailer link within a JSONObject
     * @param json with the trailer
     * @return string trailer
     */
    static private String parseTrailer(JSONObject json){

        try {
            String trailer_path = "https://www.youtube.com/watch?v=";
            trailer_path += json.getString(key_token);

            return trailer_path;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns an ArrayList<String> with all the reviews
     * @param string_reviews string containing a JSONArray with the reviews
     * @return all the reviews in an ArrayList
     */

    private static ArrayList<String> parseReviewJsonArray(String string_reviews){

        try {

            ArrayList<String> reviews = new ArrayList<>();

            JSONObject JSONObjectResultQuery = new JSONObject(string_reviews);
            JSONArray JSONTrailerArray = JSONObjectResultQuery.getJSONArray(results_token);


            for (int i = 0; i < JSONTrailerArray.length(); i++) {

                JSONObject jsonTrailer = JSONTrailerArray.getJSONObject(i);

                reviews.add(parseReview(jsonTrailer));
            }

            return reviews;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Obtain a review in String format from a JSON
     * @param json containing the review
     * @return string review
     */
    private static String parseReview(JSONObject json){

        try {
            return json.getString(review_content_key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;

        }
    }
}

