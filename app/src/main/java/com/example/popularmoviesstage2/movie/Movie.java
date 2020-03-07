package com.example.popularmoviesstage2.movie;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.popularmoviesstage2.database.MovieConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a movie obtained from api.themoviedb.org
 */

@Entity(tableName = "movies")
public class Movie implements Parcelable {

    @PrimaryKey
    private int id;
    private int popularity;
    private int vote_count;
    private String poster_path;
    private String backdrop_path;
    private String original_language;
    private String original_title;
    private String title;
    private double vote_average;
    private String overview;
    private String release_date;
    private List<String> trailers;
    private List<String> reviews;

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Empty constructor
     */
    public Movie() {

    }

    /**
     * Constructor that creates a Movie object from a Parcel object
     * @param in Parcel object needed to recreate a Movie object
     */

    private Movie(Parcel in) {
        popularity = in.readInt();
        vote_count = in.readInt();
        poster_path = in.readString();
        id = in.readInt();
        backdrop_path = in.readString();
        original_language = in.readString();
        original_title = in.readString();
        title = in.readString();
        vote_average = in.readDouble();
        overview = in.readString();
        release_date = in.readString();
        trailers = in.createStringArrayList();
        reviews = in.createStringArrayList();
    }

    /**
     * Methods needed to implement Parcelable
     *
     */

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public int getPopularity() {
        return popularity;
    }

    public int getVote_count() {
        return vote_count;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Sets the correct url to the movie's poster taking into account
     * the size of the poster
     *
     * As Room uses this set, a check is performed to to avoid path malformation
     * @param poster_path String that identifies movie's poster
     */

    public void setPoster_path(String poster_path) {

        // The Movie object is stored with its correct path (https://image...)
        // Because of this, once Room recreate the object, we can just set
        // the store path as the correct path
        if(poster_path.substring(0,4).equals("http")){
            this.poster_path = poster_path;
        } else {
            final String base_url = "https://image.tmdb.org/t/p/";
            final String size = "w185";
            this.poster_path =  base_url + size + poster_path;
        }
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    /**
     * To create a Parcel from a Movie object
     * @param dest Parcel to be written
     * @param flags flags of the written Parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(popularity);
        dest.writeInt(vote_count);
        dest.writeString(poster_path);
        dest.writeInt(id);
        dest.writeString(backdrop_path);
        dest.writeString(original_language);
        dest.writeString(original_title);
        dest.writeString(title);
        dest.writeDouble(vote_average);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeStringList(trailers);
        dest.writeStringList(reviews);
    }

    @TypeConverters({MovieConverter.class})
    public List<String> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<String> trailers) {
        this.trailers = trailers;
    }

    @TypeConverters({MovieConverter.class})
    public List<String> getReviews() {
        return reviews;
    }

    public void setReviews(List<String> reviews) {
        this.reviews = reviews;
    }
}
