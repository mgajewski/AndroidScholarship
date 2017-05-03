package com.myaps.popularmovies.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.myaps.popularmovies.data.FavMovieTransportClass;
import com.myaps.popularmovies.data.MoviesContract.FavouritesEntry;
import com.myaps.popularmovies.data.MoviesContract.ReviewsEntry;
import com.myaps.popularmovies.data.MoviesContract.TrailersEntry;

import org.androidannotations.annotations.EBean;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mgajewski on 2017-05-02.
 */

@EBean(scope = EBean.Scope.Singleton)
public class FavouritesHelper {
    private ContentResolver contentResolver;
    private ArrayList<Integer> currentFavouritesIds;

    public void init(ContentResolver resolver) {
        currentFavouritesIds = new ArrayList<>();
        contentResolver = resolver;
        if (contentResolver != null) {
            Uri uri = FavouritesEntry.CONTENT_URI;
            String[] projection = new String[] { FavouritesEntry.COLUMN_MOVIE_ID };
            Cursor favMoviesCursor = contentResolver.query(uri, projection, null, null, null);
            if (favMoviesCursor != null) {
                int colIndex = favMoviesCursor.getColumnIndex(FavouritesEntry.COLUMN_MOVIE_ID);
                while (favMoviesCursor.moveToNext()) {
                    int movieId = favMoviesCursor.getInt(colIndex);
                    currentFavouritesIds.add(movieId);
                }
                favMoviesCursor.close();
            }
        }
    }

    public boolean isFavourite(int movieId) {
        return currentFavouritesIds != null && currentFavouritesIds.contains(movieId);
    }

    public void addToFavourites(FavMovieTransportClass movieDesc,
                                @Nullable IDataProvider<JSONObject> trailers,
                                @Nullable IDataProvider<JSONObject> reviews)
    {
        if (currentFavouritesIds != null) {
            int movieId = movieDesc.getMovieId();
            if (!currentFavouritesIds.contains(movieId)) {
                if (contentResolver != null) {
                    addMovie(movieDesc.getMovieId(), movieDesc.getMovieData(), movieDesc.getPosterData());
                    if (trailers != null) {
                        addTrailers(movieDesc.getMovieId(), trailers);
                    }
                    if (reviews != null) {
                        addReviews(movieDesc.getMovieId(), reviews);
                    }
                    currentFavouritesIds.add(movieDesc.getMovieId());
                }
            }
        }
    }

    public void removeFromFavourites(int movieId) {
        if (currentFavouritesIds != null) {
            if (currentFavouritesIds.contains(movieId)) {
                if (contentResolver != null) {
                    Uri favUri = ContentUris.withAppendedId(FavouritesEntry.CONTENT_URI, movieId);
                    Uri trailersUri = ContentUris.withAppendedId(TrailersEntry.CONTENT_URI, movieId);
                    Uri reviewsUri = ContentUris.withAppendedId(ReviewsEntry.CONTENT_URI, movieId);

                    contentResolver.delete(favUri, null, null);
                    contentResolver.delete(trailersUri, null, null);
                    contentResolver.delete(reviewsUri, null, null);

                    currentFavouritesIds.remove((Object)movieId);
                }
            }
        }
    }

    public ArrayList<JSONObject> getFavouritesMovies() {
        if (contentResolver != null) {
            ArrayList<JSONObject> result = new ArrayList<>();
            Uri uri = FavouritesEntry.CONTENT_URI;
            String[] projection = new String[] { FavouritesEntry.COLUMN_JSON_DATA };
            Cursor favMoviesCursor = contentResolver.query(uri, projection, null, null, null);
            if (favMoviesCursor != null) {
                int colJsonString = favMoviesCursor.getColumnIndex(FavouritesEntry.COLUMN_JSON_DATA);

                while (favMoviesCursor.moveToNext()) {
                    String jsonString = favMoviesCursor.getString(colJsonString);
                    try {
                        JSONObject jsonMovie = new JSONObject(jsonString);
                        result.add(jsonMovie);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                favMoviesCursor.close();
            }
            return result;
        }
        return null;
    }

    public ArrayList<JSONObject> getFavouritesReviews(int movieId) {
        if (contentResolver != null) {
            ArrayList<JSONObject> result = new ArrayList<>();
            Uri uri = ContentUris.withAppendedId(ReviewsEntry.CONTENT_URI, movieId);
            Cursor reviewsCursor = contentResolver.query(uri, null, null, null, null);
            if (reviewsCursor != null) {
                int colJsonString = reviewsCursor.getColumnIndex(ReviewsEntry.COLUMN_JSON_DATA);

                while (reviewsCursor.moveToNext()) {
                    String jsonString = reviewsCursor.getString(colJsonString);
                    try {
                        JSONObject jsonReview = new JSONObject(jsonString);
                        result.add(jsonReview);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                reviewsCursor.close();
            }
            return result;
        }
        return null;
    }

    public ArrayList<JSONObject> getFavouritesTrailers(int movieId) {
        if (contentResolver != null) {
            ArrayList<JSONObject> result = new ArrayList<>();
            Uri uri = ContentUris.withAppendedId(TrailersEntry.CONTENT_URI, movieId);
            Cursor trailersCursor = contentResolver.query(uri, null, null, null, null);
            if (trailersCursor != null) {
                int colJsonString = trailersCursor.getColumnIndex(TrailersEntry.COLUMN_JSON_DATA);

                while (trailersCursor.moveToNext()) {
                    String jsonString = trailersCursor.getString(colJsonString);
                    try {
                        JSONObject jsonTrailer = new JSONObject(jsonString);
                        result.add(jsonTrailer);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                trailersCursor.close();
            }
            return result;
        }
        return null;
    }

    public String getFavouriteMoviePosterData(int movieId) {
        if (contentResolver != null) {
            String base64Poster = null;
            Uri uri = ContentUris.withAppendedId(FavouritesEntry.CONTENT_URI, movieId);
            String[] projection = new String[] { FavouritesEntry.COLUMN_POSTER_B64 };
            Cursor favMoviesCursor = contentResolver.query(uri, projection, null, null, null);
            if (favMoviesCursor != null) {
                int colPoster = favMoviesCursor.getColumnIndex(FavouritesEntry.COLUMN_POSTER_B64);
                if (favMoviesCursor.moveToFirst()) {
                    base64Poster = favMoviesCursor.getString(colPoster);
                }
                favMoviesCursor.close();
            }
            return base64Poster;
        }
        return null;
    }

    private void addMovie(int movieId, JSONObject movieJson, @Nullable String posterData) {
        Uri moviesUri = FavouritesEntry.CONTENT_URI;
        ContentValues movieValues = buildMovieValues(movieId, movieJson, posterData);
        contentResolver.insert(moviesUri, movieValues);
    }

    private void addTrailers(int movieId, IDataProvider<JSONObject> trailersData) {
        Uri trailersUri = TrailersEntry.CONTENT_URI;
        int count = trailersData.getCount();
        for (int i = 0; i < count; ++i) {
            JSONObject trailer = trailersData.getItem(i);
            ContentValues values = buildTrailerValues(movieId, trailer);
            contentResolver.insert(trailersUri, values);
        }
    }

    private void addReviews(int movieId, IDataProvider<JSONObject> reviewsData) {
        Uri reviewsUri = ReviewsEntry.CONTENT_URI;
        int count = reviewsData.getCount();
        for (int i = 0; i < count; ++i) {
            JSONObject review = reviewsData.getItem(i);
            ContentValues values = buildReviewValues(movieId, review);
            contentResolver.insert(reviewsUri, values);
        }
    }

    private ContentValues buildMovieValues(int movieId, JSONObject movieJson, @Nullable String posterData) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(FavouritesEntry.COLUMN_MOVIE_ID, movieId);
        movieValues.put(FavouritesEntry.COLUMN_JSON_DATA, movieJson.toString());
        if (posterData != null) {
            movieValues.put(FavouritesEntry.COLUMN_POSTER_B64, posterData);
        }
        return movieValues;
    }

    private ContentValues buildTrailerValues(int movieId, JSONObject trailerJson) {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(TrailersEntry.COLUMN_MOVIE_ID, movieId);
        trailerValues.put(TrailersEntry.COLUMN_JSON_DATA, trailerJson.toString());
        return trailerValues;
    }

    private ContentValues buildReviewValues(int movieId, JSONObject reviewJson) {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(ReviewsEntry.COLUMN_MOVIE_ID, movieId);
        reviewValues.put(ReviewsEntry.COLUMN_JSON_DATA, reviewJson.toString());
        return reviewValues;
    }
}
