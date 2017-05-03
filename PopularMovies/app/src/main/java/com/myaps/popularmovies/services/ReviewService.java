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
import java.util.HashMap;

/**
 * Created by mgajewski on 2017-03-27.
 */

@EBean(scope = EBean.Scope.Singleton)
public class ReviewService {

    @Bean
    FavouritesHelper favHelper;

    public class ReviewProvider implements IDataProvider<JSONObject> {

        private ArrayList<JSONObject> reviewData;

        ReviewProvider(ArrayList<JSONObject> data) {
            reviewData = data;
        }

        @Override
        public int getCount() {
            if (reviewData != null) {
                return reviewData.size();
            }
            return 0;
        }

        @Override
        public JSONObject getItem(int position) {
            if (reviewData != null && position < reviewData.size()) {
                return reviewData.get(position);
            }
            return null;
        }
    }

    private static final String PAGE_QUERY_PARAM = "page";
    private HashMap<String, String> queryMap = new HashMap<>();
    private static final String ENDPOINT_PATTERN = "movie/%1d/reviews";
    private static final String JSON_RESULTS_ARRAY = "results";
    private static int MAX_PAGE = 5;

    private static LruCache<Integer, ReviewProvider> reviewProviders = new LruCache<>(100);
    //public static final ReviewService instance = new ReviewService();

    ReviewService() {queryMap.put(PAGE_QUERY_PARAM, "1");}

    public ReviewProvider getDataProvider(int movieId) {
        return reviewProviders.get(movieId);
    }

    public boolean reloadReviews(int movieId) {
        return reloadReviews(movieId, false);
    }

    public boolean reloadReviews(int movieId, boolean fromFav) {
        reviewProviders.remove(movieId);
        if (fromFav) {
            return loadFromFavourites(movieId);
        } else {
            return loadFromNetwork(movieId);
        }
    }

    private boolean loadFromFavourites(int movieId) {
        ArrayList<JSONObject> reviewData = favHelper.getFavouritesReviews(movieId);
        if (reviewData != null) {
            ReviewProvider provider = new ReviewProvider(reviewData);
            reviewProviders.put(movieId, provider);
            return true;
        }
        return false;
    }

    private boolean loadFromNetwork(int movieId) {
        ArrayList<JSONObject> reviewData = loadReviewData(movieId);
        if (reviewData != null) {
            ReviewProvider provider = new ReviewProvider(reviewData);
            reviewProviders.put(movieId, provider);
            return true;
        }
        return false;
    }

    private ArrayList<JSONObject> loadReviewData(int movieId) {
        ArrayList<JSONObject> reviewData = new ArrayList<>();

        boolean hasNext = true;
        int page = 1;
        try {
            while (hasNext) {
                if (page > MAX_PAGE) {
                    break;
                }
                queryMap.put(PAGE_QUERY_PARAM, ""+page);
                String response = NetworkUtils.GET_FromEndpoint(String.format(ENDPOINT_PATTERN, movieId), queryMap);
                JSONObject json = new JSONObject(response);
                JSONArray resultsArray = json.getJSONArray(JSON_RESULTS_ARRAY);
                if (resultsArray.length() > 0) {
                    for (int i = 0; i < resultsArray.length(); ++i) {
                        JSONObject object = resultsArray.getJSONObject(i);
                        reviewData.add(object);
                    }
                } else {
                    hasNext = false;
                }
                ++page;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return reviewData;
    }
}
