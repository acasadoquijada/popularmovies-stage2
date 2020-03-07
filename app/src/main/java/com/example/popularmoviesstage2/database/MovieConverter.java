package com.example.popularmoviesstage2.database;

import androidx.room.TypeConverter;

import com.example.popularmoviesstage2.movie.Movie;
import com.google.gson.Gson;

public class MovieConverter {

    @TypeConverter
    public static Movie toMovie(String movie_string){
        Gson gson = new Gson();
        Movie movie = gson.fromJson(movie_string, Movie.class);

        return movie;
    }

    @TypeConverter
    public static String toString(Movie movie){
        Gson gson = new Gson();
        String toStoreObject = gson.toJson(movie, Movie.class);

        return toStoreObject;
    }
}
