package com.myaps.popularmovies.services;

import android.util.LruCache;

import com.myaps.popularmovies.utils.FavouritesHelper;
import com.myaps.popularmovies.utils.IDataProvider;
import com.myaps.popularmovies.utils.NetworkUtils;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mgajewski on 2017-03-24.
 */

@EBean(scope = EBean.Scope.Singleton)
public class VideoService {

    @Bean
    FavouritesHelper favHelper;

    public class VideoProvider implements IDataProvider<JSONObject>  {

        private ArrayList<JSONObject> videoData;

        VideoProvider(ArrayList<JSONObject> data) {
            videoData = data;
        }

        @Override
        public int getCount() {
            if (videoData != null) {
                return videoData.size();
            }
            return 0;
        }

        @Override
        public JSONObject getItem(int position) {
            if (videoData != null && position < videoData.size()) {
                return videoData.get(position);
            }
            return null;
        }
    }

    private static final String JSON_RESULTS_ARRAY = "results";
    private static final String ENDPOINT_PATTERN = "movie/%1d/videos";

    private static LruCache<Integer, VideoProvider> videoProviders = new LruCache<>(100);


    //public static final VideoService instance = new VideoService();

    VideoService() {}

    public VideoProvider getDataProvider(int movieId) {
        return videoProviders.get(movieId);
    }

    public boolean reloadVideos(int movieId) {
        return reloadVideos(movieId, false);
    }

    public boolean reloadVideos(int movieId, boolean fromFav) {
        videoProviders.remove(movieId);
        if (fromFav) {
            return loadFromFavourites(movieId);
        } else {
            return loadFromNetwork(movieId);
        }
    }

    private boolean loadFromFavourites(int movieId) {
        ArrayList<JSONObject> trailersData = favHelper.getFavouritesTrailers(movieId);
        if (trailersData != null) {
            VideoProvider provider = new VideoProvider(trailersData);
            videoProviders.put(movieId, provider);
            return true;
        }
        return false;
    }

    private boolean loadFromNetwork(int movieId) {
        ArrayList<JSONObject> videoData = new ArrayList<>();
        try {
            String response = NetworkUtils.GET_FromEndpoint(String.format(ENDPOINT_PATTERN, movieId));
            JSONObject json = new JSONObject(response);
            JSONArray resultsArray = json.getJSONArray(JSON_RESULTS_ARRAY);
            for (int i = 0; i < resultsArray.length(); ++i) {
                JSONObject object = resultsArray.getJSONObject(i);
                videoData.add(object);
            }
            VideoProvider provider = new VideoProvider(videoData);
            videoProviders.put(movieId, provider);
            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
