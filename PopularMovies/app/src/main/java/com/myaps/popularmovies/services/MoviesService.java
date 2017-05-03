package com.myaps.popularmovies.services;

import com.myaps.popularmovies.data.FavMovieTransportClass;
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
import java.util.List;

/**
 * Created by mgajewski on 2017-01-26.
 */

@EBean(scope = EBean.Scope.Singleton)
public class MoviesService {

    private static final String PAGE_QUERY_PARAM = "page";

    private static final String JSON_RESULTS_ARRAY = "results";

    private static int MAX_PAGE = 5;
    private HashMap<String, String> queryMap = new HashMap<>();

    private static ArrayList<JSONObject> topRatedData = new ArrayList<>();
    private static ArrayList<JSONObject> mostPopularData = new ArrayList<>();
    private static ArrayList<JSONObject> favouritesData = new ArrayList<>();

    //public static final MoviesService instance = new MoviesService();

    MoviesService() {
        queryMap.put(PAGE_QUERY_PARAM, "1");
    }

    public enum SortType {
        TOP_RATED("movie/top_rated", topRatedData),
        MOST_POPULAR("movie/popular", mostPopularData),
        FAVOURITES(null, favouritesData);

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

    private IDataProvider<JSONObject> topRatedProvider = new IDataProvider<JSONObject>() {
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

    private IDataProvider<JSONObject> mostPopularProvider = new IDataProvider<JSONObject>() {
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

    private IDataProvider<JSONObject> favouritesProvider = new IDataProvider<JSONObject>() {
        @Override
        public int getCount() {
            return favouritesData.size();
        }

        @Override
        public JSONObject getItem(int position) {
            if (position < favouritesData.size()) {
                return favouritesData.get(position);
            }
            return null;
        }
    };

    @Bean
    FavouritesHelper favHelepr;

    LoadingProgressListener loadingProgressListener;

    public IDataProvider<JSONObject> getDataProvider(SortType sortType) {
        if (sortType == SortType.MOST_POPULAR) {
            return mostPopularProvider;
        } else if (sortType == SortType.TOP_RATED) {
            return topRatedProvider;
        } else if (sortType == SortType.FAVOURITES) {
            return favouritesProvider;
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

    public boolean reloadFavouritesMovies() {
        favouritesData.clear();

        if (loadingProgressListener != null) {
            loadingProgressListener.onLoadingProgress(0);
        }

        List<JSONObject> favMovies = favHelepr.getFavouritesMovies();
        SortType.FAVOURITES.dataContainer.addAll(favMovies);

        if (loadingProgressListener != null) {
            loadingProgressListener.onLoadingProgress(1);
        }
        return favouritesProvider.getCount() > 0;
    }

    public boolean reloadMovies(SortType sortType) {
        if (sortType == SortType.MOST_POPULAR) {
            return reloadMostPopularMovies();
        } else if (sortType == SortType.TOP_RATED) {
            return reloadTopRatedMovies();
        } else if (sortType == SortType.FAVOURITES) {
            return reloadFavouritesMovies();
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
