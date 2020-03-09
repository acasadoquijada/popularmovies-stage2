package com.example.popularmoviesstage2.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Scanner;

/**
 * Class used to request movies to http://api.themoviedb.org
 */


public class NetworkUtils {

    private static final String BASE_URL =
            "http://api.themoviedb.org/3/movie/";

    public static final String top_rated = "top_rated";
    public static final String popular = "popular";


    private static final String KEY = ""; // Your key goes here!!
    private static final String api_key_sort = "api_key";
    private static final String videos_token = "videos";
    private static final String reviews_token = "reviews";

    private static boolean mIsOnline;

    /**
     * Creates a URL with the specified sort option
     * @param sort_option it can be top_rated or popular
     * @return The URL to fetch the HTTP response from.
     */
    private static URL buildMovieUrl(String sort_option){
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(sort_option)
                .appendQueryParameter(api_key_sort,KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    private static URL buildMovieExtraInfoUrl(int id, String extra_info){
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(String.valueOf(id))
                .appendPath(extra_info)
                .appendQueryParameter(api_key_sort,KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    /**
     * Obtain the top rate movies in JSON
     * @return String object containing the movies in JSON format
     * @throws IOException Related to network and stream reading
     */

    public static String getTopRateMovies() throws IOException {
        URL topRatedMoviesUrl = buildMovieUrl(top_rated);
        return getResponseFromHttpUrl(topRatedMoviesUrl);

    }

    /**
     * Obtain the popular movies in JSON
     * @return String object containing the movies in JSON format
     * @throws IOException Related to network and stream reading
     */

    public static String getPopularMovies() throws IOException {
        URL topRatedMoviesUrl = buildMovieUrl(popular);
        return getResponseFromHttpUrl(topRatedMoviesUrl);

    }

    /**
     * Obtain movie trailers in JSON
     * @return String object containing the trailers in JSON format
     * @throws IOException Related to network and stream reading
     */

    public static String getTrailersMovie(int movie_id) throws IOException {
        URL trailersMovie = buildMovieExtraInfoUrl(movie_id,videos_token);
        Log.d("TRAILER_TAG",trailersMovie.toString());
        return getResponseFromHttpUrl(trailersMovie);
    }

    /**
     * Obtain movie reviews in JSON
     * @return String object containing the trailers in JSON format
     * @throws IOException Related to network and stream reading
     */

    public static String getReviewsMovie(int movie_id) throws IOException {
        URL trailersMovie = buildMovieExtraInfoUrl(movie_id, reviews_token);

        Log.d("REVIEW_TAG",trailersMovie.toString());
        return getResponseFromHttpUrl(trailersMovie);
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */

    private static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Checks if there is internet connection.
     * This method has been obtaion from this StackOverflow post:
     * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     *
     * This method is used instead of other alternatives because it works in most of Android version
     * and is a lightweight operation
     * @return true if there is internet connection, false otherwise
     */

    public static boolean isOnline() {

        mIsOnline = true;
        AppExecutor.getsInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    int timeoutMs = 1500;
                    Socket sock = new Socket();
                    SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

                    sock.connect(sockaddr, timeoutMs);
                    sock.close();

                    mIsOnline = true;
                } catch (IOException e) {
                    mIsOnline = false;
                }
            }
        });

        return mIsOnline;
    }
}