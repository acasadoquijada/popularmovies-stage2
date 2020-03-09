package com.example.popularmoviesstage2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.popularmoviesstage2.movie.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
@TypeConverters({MovieConverter.class, ListConverter.class})
public abstract class MovieDataBase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "moviesdatabase";
    private static MovieDataBase sInstance;

    public static MovieDataBase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        MovieDataBase.class,
                        MovieDataBase.DATABASE_NAME)
                        .build();
            }
        }
        return sInstance;
    }

    public abstract MovieDAO movieDAO();
}
