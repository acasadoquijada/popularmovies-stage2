package com.example.popularmoviesstage2.database;

import androidx.room.TypeConverter;

import com.example.popularmoviesstage2.movie.Movie;
import com.google.gson.Gson;

/**
 * Converts a Movie object to String and viceversa
 */

public class MovieConverter {

    @TypeConverter
    public static Movie toMovie(String movie_string){
        Gson gson = new Gson();

        return gson.fromJson(movie_string, Movie.class);
    }

    @TypeConverter
    public static String toString(Movie movie){
        Gson gson = new Gson();

        return gson.toJson(movie, Movie.class);
    }
}
