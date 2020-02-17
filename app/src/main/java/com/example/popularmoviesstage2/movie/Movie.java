package com.example.popularmoviesstage2.movie;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Represents a movie obtained from api.themoviedb.org
 */

public class Movie implements Parcelable {

    private int popularity;
    private int vote_count;
    private boolean video;
    private String poster_path;
    private int id;
    private boolean adult;
    private String backdrop_path;
    private String original_language;
    private String original_title;
    private int[] genre_ids;
    private String title;
    private double vote_average;
    private String overview;
    private String release_date;
    private ArrayList<String> trailers;
    private ArrayList<String> reviews;

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
        video = (Boolean) in.readValue(null);
        poster_path = in.readString();
        id = in.readInt();
        adult = (Boolean) in.readValue(null);
        backdrop_path = in.readString();
        original_language = in.readString();
        original_title = in.readString();
        genre_ids = in.createIntArray();
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

    public void setVideo(boolean video) {
        this.video = video;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
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

    /**
     * Sets the correct url to the movie's poster taking into account
     * the size of the poster
     * @param poster_path String identificator of the movie's poster
     */

    public void setPoster_path(String poster_path) {
        final String base_url = "https://image.tmdb.org/t/p/";
        final String size = "w185";
        this.poster_path =  base_url + size + poster_path;
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
        dest.writeValue(video);
        dest.writeString(poster_path);
        dest.writeInt(id);
        dest.writeValue(adult);
        dest.writeString(backdrop_path);
        dest.writeString(original_language);
        dest.writeString(original_title);
        dest.writeIntArray(genre_ids);
        dest.writeString(title);
        dest.writeDouble(vote_average);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeStringList(trailers);
        dest.writeStringList(reviews);
    }

    public ArrayList<String> getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<String> trailers) {
        this.trailers = trailers;
    }

    public ArrayList<String> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<String> reviews) {
        this.reviews = reviews;
    }
}
