package com.example.popularmoviesstage2.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

    /**
     * Creates a URL with the specified sort option
     * @param sort_option it can be top_rated or popular
     * @return The URL to fetch the HTTP response from.
     */
    private static URL buildUrl(String sort_option){
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

    /**
     * Obtain the top rate movies in JSON
     * @return String object containing the movies in JSON format
     * @throws IOException Related to network and stream reading
     */

    public static String getTopRateMovies() throws IOException {
        URL topRatedMoviesUrl = buildUrl(top_rated);
        return getResponseFromHttpUrl(topRatedMoviesUrl);

    }

    /**
     * Obtain the popular movies in JSON
     * @return String object containing the movies in JSON format
     * @throws IOException Related to network and stream reading
     */

    public static String getPopularMovies() throws IOException {
        URL topRatedMoviesUrl = buildUrl(popular);
        return getResponseFromHttpUrl(topRatedMoviesUrl);

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
}