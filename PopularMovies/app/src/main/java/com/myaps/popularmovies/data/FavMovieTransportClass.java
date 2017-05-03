package com.myaps.popularmovies.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONObject;

/**
 * Created by mgajewski on 2017-05-02.
 */

public class FavMovieTransportClass {
    private int movieId;
    private JSONObject movieData;
    private String posterData;

    public FavMovieTransportClass(@NonNull int movieId, @NonNull JSONObject movieData, @Nullable  String posterData) {
        this.movieId = movieId;
        this.movieData = movieData;
        this.posterData = posterData;
    }

    public FavMovieTransportClass(@NonNull int movieId, @NonNull JSONObject movieData) {
        this.movieId = movieId;
        this.movieData = movieData;
    }

    @NonNull
    public int getMovieId() {
        return movieId;
    }

    @NonNull
    public JSONObject getMovieData() {
        return movieData;
    }

    @Nullable
    public String getPosterData() {
        return posterData;
    }
}
