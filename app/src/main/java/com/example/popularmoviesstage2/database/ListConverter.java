package com.example.popularmoviesstage2.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;



// Code obtain from this stackoverflow post:
// https://stackoverflow.com/questions/44986626/
// android-room-database-how-to-handle-arraylist-in-an-entity/51083865

/**
 * Class needed for Room. It converts a List<String> to String and viceversa
 *
 * Code obtain from this stackoverflow post:
 * https://stackoverflow.com/questions/44986626/
 * android-room-database-how-to-handle-arraylist-in-an-entity/51083865
 */
class ListConverter {

    @TypeConverter
    public static List<String> fromString(String value) {

        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }



}
