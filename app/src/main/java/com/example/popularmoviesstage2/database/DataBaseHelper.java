package com.example.popularmoviesstage2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.popularmoviesstage2.movie.Movie;
import com.google.gson.Gson;

import java.util.ArrayList;

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

    public void insertMovie(Movie movie){
        SQLiteDatabase db = this.getWritableDatabase();
        // First we check if the record already exists

        String selectQuery =
                "SELECT * FROM " + MOVIE_TABLE_NAME + " WHERE " + MOVIE_ID + "=" + movie.getId();

        Cursor cursor = db.rawQuery(selectQuery,null);

        // moveToFirst == false, no record
        if(!cursor.moveToFirst()){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MOVIE_ID, movie.getId());

            Log.d("MOVIE_ID",String.valueOf(movie.getId()));

            // Convert
            Gson gson = new Gson();
            String toStoreObject = gson.toJson(movie, Movie.class);

            contentValues.put(MOVIE, toStoreObject);
            db.insert(MOVIE_TABLE_NAME, null, contentValues);
        }

        cursor.close();

    }

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

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        String clearDBQuery = "DELETE FROM "+ MOVIE_TABLE_NAME;
        db.execSQL(clearDBQuery);
    }
}
