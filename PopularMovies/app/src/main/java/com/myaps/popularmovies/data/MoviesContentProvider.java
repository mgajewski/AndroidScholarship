package com.myaps.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.myaps.popularmovies.data.MoviesContract.ReviewsEntry;
import com.myaps.popularmovies.data.MoviesContract.TrailersEntry;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EProvider;

import static com.myaps.popularmovies.data.MoviesContract.FavouritesEntry;

/**
 * Created by mgajewski on 2017-05-01.
 */

@EProvider
public class MoviesContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int REVIEWS = 200;
    public static final int TRAILERS = 300;

    public static final int MOVIE_FOR_MOVIE_ID = 101;
    /**
     * Get movies by movie_id instead of record id so don't need this one for now
    public static final int MOVIE_FOR_MOVIE_ID = 101;
     */

    /**
     * Not implemented as I don't need query/update/delete single review or trailer for now
    public static final int REVIEW_WITH_ID = 201;
    public static final int TRAILER_WITH_ID = 301;
    */
    public static final int REVIEWS_FOR_MOVIE_ID = 210;     //Filtered Reviews folder
    public static final int TRAILERS_FOR_MOVIE_ID = 310;    //Filtered Trailers folder

    private static final UriMatcher uriMatcher = buildUriMatcher();

    @Bean
    MoviesDbHelper moviesDbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_FAVOURITES, MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_FAVOURITES + "/#", MOVIE_FOR_MOVIE_ID);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_REVIEWS + "/#", REVIEWS_FOR_MOVIE_ID);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_TRAILERS, TRAILERS);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_TRAILERS + "/#", TRAILERS_FOR_MOVIE_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        //
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = moviesDbHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);
        Cursor cursor;

        String processedSelection = selection;
        String[] processedSelectionArgs = selectionArgs;
        String tableName;
        String movieId;

        switch (match) {
            case MOVIES:
                tableName = FavouritesEntry.TABLE_NAME;
                break;
            case REVIEWS:
                tableName = ReviewsEntry.TABLE_NAME;
                break;
            case TRAILERS:
                tableName = TrailersEntry.TABLE_NAME;
                break;
            case MOVIE_FOR_MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                tableName = FavouritesEntry.TABLE_NAME;
                processedSelection = FavouritesEntry.COLUMN_MOVIE_ID + "=?";
                processedSelectionArgs = new String[]{ id };
                break;
            case REVIEWS_FOR_MOVIE_ID:
                movieId = uri.getPathSegments().get(1);
                tableName = ReviewsEntry.TABLE_NAME;
                processedSelection = ReviewsEntry.COLUMN_MOVIE_ID + "=?";
                processedSelectionArgs = new String[]{ movieId };
                break;
            case TRAILERS_FOR_MOVIE_ID:
                movieId = uri.getPathSegments().get(1);
                tableName = TrailersEntry.TABLE_NAME;
                processedSelection = TrailersEntry.COLUMN_MOVIE_ID + "=?";
                processedSelectionArgs = new String[]{ movieId };
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor =  db.query(tableName, projection, processedSelection, processedSelectionArgs, null, null, sortOrder);
        Context ctx = getContext();
        if (ctx != null) {
            cursor.setNotificationUri(ctx.getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        String mimeType;
        switch (match) {
            case MOVIES:
                mimeType = "vnd.android.cursor.dir" + "/" + MoviesContract.AUTHORITY + "/" + MoviesContract.PATH_FAVOURITES;
                break;
            case REVIEWS:
            case REVIEWS_FOR_MOVIE_ID:
                mimeType = "vnd.android.cursor.dir" + "/" + MoviesContract.AUTHORITY + "/" + MoviesContract.PATH_REVIEWS;
                break;
            case TRAILERS:
            case TRAILERS_FOR_MOVIE_ID:
                mimeType = "vnd.android.cursor.dir" + "/" + MoviesContract.AUTHORITY + "/" + MoviesContract.PATH_TRAILERS;
                break;
            case MOVIE_FOR_MOVIE_ID:
                mimeType = "vnd.android.cursor.item" + "/" + MoviesContract.AUTHORITY + "/" + MoviesContract.PATH_FAVOURITES;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return mimeType;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (values == null) return null;

        final SQLiteDatabase db = moviesDbHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);
        Uri returnUri;
        String tableName;
        Uri uriBase;

        switch (match) {
            case MOVIES:
                tableName = FavouritesEntry.TABLE_NAME;
                uriBase = FavouritesEntry.CONTENT_URI;
                break;
            case REVIEWS:
                tableName = ReviewsEntry.TABLE_NAME;
                uriBase = ReviewsEntry.CONTENT_URI;
                break;
            case TRAILERS:
                tableName = TrailersEntry.TABLE_NAME;
                uriBase = TrailersEntry.CONTENT_URI;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        long id = db.insert(tableName, null, values);
        if (id >= 0) {
            returnUri = ContentUris.withAppendedId(uriBase, id);
        } else {
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }

        Context ctx = getContext();
        if (ctx != null) {
            ctx.getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);

        String processedSelection = selection;
        String[] processedSelectionArgs = selectionArgs;
        String tableName;
        String movieId;

        switch (match) {
            case MOVIES:
                tableName = FavouritesEntry.TABLE_NAME;
                break;
            case REVIEWS:
                tableName = ReviewsEntry.TABLE_NAME;
                break;
            case TRAILERS:
                tableName = TrailersEntry.TABLE_NAME;
                break;
            case MOVIE_FOR_MOVIE_ID:
                String id = uri.getPathSegments().get(1);
                tableName = FavouritesEntry.TABLE_NAME;
                processedSelection = FavouritesEntry.COLUMN_MOVIE_ID + "=?";
                processedSelectionArgs = new String[]{ id };
                break;
            case REVIEWS_FOR_MOVIE_ID:
                movieId = uri.getPathSegments().get(1);
                tableName = ReviewsEntry.TABLE_NAME;
                processedSelection = ReviewsEntry.COLUMN_MOVIE_ID + "=?";
                processedSelectionArgs = new String[]{ movieId };
                break;
            case TRAILERS_FOR_MOVIE_ID:
                movieId = uri.getPathSegments().get(1);
                tableName = TrailersEntry.TABLE_NAME;
                processedSelection = TrailersEntry.COLUMN_MOVIE_ID + "=?";
                processedSelectionArgs = new String[]{ movieId };
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        int rowsDeleted = db.delete(tableName, processedSelection, processedSelectionArgs);
        if (rowsDeleted != 0) {
            Context ctx = getContext();
            if (ctx != null) {
                ctx.getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = moviesDbHelper.getReadableDatabase();

        int match = uriMatcher.match(uri);
        String tableName;

        switch (match) {
            case MOVIE_FOR_MOVIE_ID:
                tableName = FavouritesEntry.TABLE_NAME;
                selection = FavouritesEntry.COLUMN_MOVIE_ID + "=?";
                break;
            case MOVIES:
                throw new UnsupportedOperationException("Please use single item uri: " + uri + "/{movieId}");
            case REVIEWS_FOR_MOVIE_ID:
            case REVIEWS:
                throw new UnsupportedOperationException("Reviews updating not implemented");
            case TRAILERS_FOR_MOVIE_ID:
            case TRAILERS:
                throw new UnsupportedOperationException("Trailers updating not implemented");
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        String id = uri.getPathSegments().get(1);
        int rowsUpdated = db.update(tableName, values, selection, new String[] { id });
        if (rowsUpdated != 0) {
            Context ctx = getContext();
            if (ctx != null) {
                ctx.getContentResolver().notifyChange(uri, null);
            }
        }

        return rowsUpdated;
    }
}
