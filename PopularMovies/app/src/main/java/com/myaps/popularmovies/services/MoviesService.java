package com.myaps.popularmovies.services;

import com.myaps.popularmovies.utils.IDataProvider;
import com.myaps.popularmovies.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mgajewski on 2017-01-26.
 */

public class MoviesService {

    private static final String PAGE_QUERY_PARAM = "page";

    private static final String JSON_RESULTS_ARRAY = "results";

    private static int MAX_PAGE = 5;
    private HashMap<String, String> queryMap = new HashMap<>();

    private static ArrayList<JSONObject> topRatedData = new ArrayList<>();
    private static ArrayList<JSONObject> mostPopularData = new ArrayList<>();

    public static final MoviesService instance = new MoviesService();

    private MoviesService() {
        queryMap.put(PAGE_QUERY_PARAM, "1");
    }

    public enum SortType {
        TOP_RATED("movie/top_rated", topRatedData),
        MOST_POPULAR("movie/popular", mostPopularData);

        SortType(String endpoint, List<JSONObject> dataContainer) {
            this.endpoint = endpoint;
            this.dataContainer = dataContainer;
        }
        String endpoint;
        List<JSONObject> dataContainer;
    }

    public interface LoadingProgressListener {
        void onLoadingProgress(float progress);
    }

    private IDataProvider topRatedProvider = new IDataProvider() {
        @Override
        public int getCount() {
            return topRatedData.size();
        }

        @Override
        public JSONObject getItem(int position) {
            if (position < topRatedData.size()) {
                return topRatedData.get(position);
            }
            return null;
        }
    };

    private IDataProvider mostPopularProvider = new IDataProvider() {
        @Override
        public int getCount() {
            return mostPopularData.size();
        }

        @Override
        public JSONObject getItem(int position) {
            if (position < mostPopularData.size()) {
                return mostPopularData.get(position);
            }
            return null;
        }
    };

    LoadingProgressListener loadingProgressListener;

    public IDataProvider getDataProvider(SortType sortType) {
        if (sortType == SortType.MOST_POPULAR) {
            return mostPopularProvider;
        } else if (sortType == SortType.TOP_RATED) {
            return topRatedProvider;
        }
        return null;
    }

    public void setLoadingProgressListener(LoadingProgressListener listener) {
        loadingProgressListener = listener;
    }

    public void removeLoadingProgressListener() {
        loadingProgressListener = null;
    }

    public boolean reloadTopRatedMovies() {
        topRatedData.clear();
        boolean hasNext = true;
        int page = 1;
        while (hasNext) {
            hasNext = loadPage(page++, SortType.TOP_RATED);
            if (loadingProgressListener != null) {
                loadingProgressListener.onLoadingProgress((float)(page-1) / (float)MAX_PAGE);
            }
        }
        return topRatedProvider.getCount() > 0;
    }

    public boolean reloadMostPopularMovies() {
        mostPopularData.clear();
        boolean hasNext = true;
        int page = 1;
        while (hasNext) {
            hasNext = loadPage(page++, SortType.MOST_POPULAR);
            if (loadingProgressListener != null) {
                loadingProgressListener.onLoadingProgress((float)(page-1) / (float)MAX_PAGE);
            }
        }
        return mostPopularProvider.getCount() > 0;
    }

    public boolean reloadMovies(SortType sortType) {
        if (sortType == SortType.MOST_POPULAR) {
            return reloadMostPopularMovies();
        } else if (sortType == SortType.TOP_RATED) {
            return reloadTopRatedMovies();
        }
        return false;
    }

    private boolean loadPage(int page, SortType sortType) {
        if (page > MAX_PAGE) return false;

        queryMap.put(PAGE_QUERY_PARAM, ""+page);
        try {
            String response = NetworkUtils.GET_FromEndpoint(sortType.endpoint, queryMap);
            JSONObject json = new JSONObject(response);
            JSONArray resultsArray = json.getJSONArray(JSON_RESULTS_ARRAY);
            if (resultsArray.length() == 0)
                return false;
            for (int i = 0; i < resultsArray.length(); ++i) {
                JSONObject object = resultsArray.getJSONObject(i);
                sortType.dataContainer.add(object);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
