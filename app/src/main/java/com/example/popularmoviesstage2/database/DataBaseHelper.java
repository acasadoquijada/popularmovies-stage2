package com.example.popularmoviesstage2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.popularmoviesstage2.movie.Movie;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * This class is in charge to store in a DataBase and manipulate
 * the movies marked as favorite by the user.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Movies.db";
    private static final String MOVIE_TABLE_NAME = "MovieTable";
    private static final String MOVIE_ID = "id";
    private static final String MOVIE = "movie";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createQuery = "CREATE TABLE " +
                MOVIE_TABLE_NAME + " (" +
                MOVIE_ID + " INTEGER PRIMARY KEY ," +
                MOVIE + " TEXT NOT NULL) ";

        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS MOVIE_TABLE_NAME");
        onCreate(db);
    }

    /**
     * Insert a favorite movie in the DB if not exists already
     * @param movie to insert
     */
    public void insertMovie(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();

        if(!movieInDataBase(movie)){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MOVIE_ID, movie.getId());
            // Convert
            Gson gson = new Gson();
            String toStoreObject = gson.toJson(movie, Movie.class);

            contentValues.put(MOVIE, toStoreObject);
            db.insert(MOVIE_TABLE_NAME, null, contentValues);
        }

    }

    /**
     * Delete a movie from the DB
     * @param movie to delete
     */
    public void deleteMovie(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();

        String deleteQuery =
                "DELETE FROM " + MOVIE_TABLE_NAME + " WHERE " + MOVIE_ID + "=" + movie.getId();

        db.execSQL(deleteQuery);
    }

    /**
     * Checks if a given movie is already in the DB
     * @param movie to consult
     * @return true if the movie is in the DB. Otherwise false
     */

    public boolean movieInDataBase(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery =
                "SELECT * FROM " + MOVIE_TABLE_NAME + " WHERE " + MOVIE_ID + "=" + movie.getId();

        Cursor cursor = db.rawQuery(selectQuery,null);

        boolean movieStored = cursor.moveToFirst();

        cursor.close();

        return movieStored;
    }

    /**
     * Returns all the movies in the DB
     * @return ArrayList with all the movies
     */

    public ArrayList<Movie> getMovies(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        ArrayList<Movie> movies = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + MOVIE_TABLE_NAME;

        cursor = db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){

            Gson gson = new Gson();
            do{
                String movie_string = cursor.getString(1);
                Movie movie = gson.fromJson(movie_string, Movie.class);

                movies.add(movie);

            }while(cursor.moveToNext());
        }

        cursor.close();

        return movies;

        //Model modelObject = gson.fromJson(storedObjectString, Model.class);

    }

}
