package com.myaps.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.myaps.popularmovies.data.MoviesContract.FavouritesEntry;
import com.myaps.popularmovies.data.MoviesContract.ReviewsEntry;
import com.myaps.popularmovies.data.MoviesContract.TrailersEntry;

import org.androidannotations.annotations.EBean;

/**
 * Created by mgajewski on 2017-05-01.
 */

@EBean
public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "moviesDb.db";
    private static final int VERSION = 1;

    private static final String CREATE_MOVIES_TABLE = "" +
        "CREATE TABLE "  + FavouritesEntry.TABLE_NAME + " (" +
            FavouritesEntry._ID                 + " INTEGER PRIMARY KEY, " +
            FavouritesEntry.COLUMN_MOVIE_ID     + " INTEGER NOT NULL UNIQUE, " +
            FavouritesEntry.COLUMN_JSON_DATA    + " TEXT NOT NULL, " +
            FavouritesEntry.COLUMN_POSTER_B64   + " TEXT);";
    private static final String CREATE_TRAILERS_TABLE = "" +
        "CREATE TABLE "  + TrailersEntry.TABLE_NAME + " (" +
            TrailersEntry._ID                   + " INTEGER PRIMARY KEY, " +
            TrailersEntry.COLUMN_MOVIE_ID       + " INTEGER NOT NULL, " +
            TrailersEntry.COLUMN_JSON_DATA      + " TEXT NOT NULL);";
    private static final String CREATE_REVIEWS_TABLE = "" +
        "CREATE TABLE "  + ReviewsEntry.TABLE_NAME + " (" +
            ReviewsEntry._ID                    + " INTEGER PRIMARY KEY, " +
            ReviewsEntry.COLUMN_MOVIE_ID        + " INTEGER NOT NULL, " +
            ReviewsEntry.COLUMN_JSON_DATA       + " TEXT NOT NULL);";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MOVIES_TABLE);
        db.execSQL(CREATE_TRAILERS_TABLE);
        db.execSQL(CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouritesEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailersEntry.TABLE_NAME);
        onCreate(db);
    }
}
