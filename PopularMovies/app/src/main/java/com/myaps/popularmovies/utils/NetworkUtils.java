package com.myaps.popularmovies.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by mgajewski on 2017-01-26.
 */

public final class NetworkUtils {

    private static final String API_KEY = "YOUR_API_KEY_HERE";

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String API_KEY_PARAM = "api_key";

    public static String GET_FromEndpoint(String endpoint) throws IOException {
        return GET_FromEndpoint(endpoint, null);
    }

    public static Uri getImageUrl(String posterPath) {
        Uri builtUri  = Uri.parse(IMAGE_BASE_URL)
                      .buildUpon()
                      .appendEncodedPath(posterPath)
                      .build();
        return builtUri;
    }

    public static String GET_FromEndpoint(String endpoint, Map<String, String> query) throws IOException {
        URL httpUrl = buildUrl(endpoint, query);
        HttpURLConnection urlConnection = (HttpURLConnection) httpUrl.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private static URL buildUrl(String endpoint) {
        return buildUrl(endpoint, null);
    }

    private static URL buildUrl(@NonNull String endpoint, Map<String, String> query) {

        //TODO:
        // add argument: String... pathParams
        //Parse and format endpoint with pathParams
        //i.e resolve /movie/{0} to /movie/pathParams[0]

        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon().appendEncodedPath(endpoint)
            .appendQueryParameter(API_KEY_PARAM, API_KEY);
        if (query != null) {
            for (String key : query.keySet()) {
                builder.appendQueryParameter(key, query.get(key));
            }
        }
        Uri builtUri = builder.build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Built URI " + url);

        return url;
    }

}
