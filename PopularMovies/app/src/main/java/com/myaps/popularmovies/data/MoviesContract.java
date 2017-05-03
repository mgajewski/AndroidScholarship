package com.myaps.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mgajewski on 2017-05-01.
 */

public class MoviesContract {
    public static final String AUTHORITY = "com.myaps.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVOURITES = "favourites";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_REVIEWS = "reviews";

    public static final class FavouritesEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();
        public static final String TABLE_NAME = "favourites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_JSON_DATA = "json_data";
        public static final String COLUMN_POSTER_B64 = "poster_b64";
    }

    public static final class TrailersEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();
        public static final String TABLE_NAME = "trailers";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_JSON_DATA = "json_data";
    }

    public static final class ReviewsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();
        public static final String TABLE_NAME = "reviews";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_JSON_DATA = "json_data";
    }
}
