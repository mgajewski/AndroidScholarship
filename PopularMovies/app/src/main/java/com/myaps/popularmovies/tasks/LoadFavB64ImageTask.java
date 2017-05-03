package com.myaps.popularmovies.tasks;
import com.myaps.popularmovies.utils.FavouritesHelper;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

/**
 * Created by mgajewski on 2017-05-02.
 */

@EBean
public class LoadFavB64ImageTask {
    public interface Callback {
        void onFinish(String data);
    }

    private Callback resultCallback;

    @Bean
    FavouritesHelper favHelper;

    @Background
    public void run(int movieId, Callback callback) {
        resultCallback = callback;
        String result = favHelper.getFavouriteMoviePosterData(movieId);
        updateUI(result);
    }

    @UiThread
    void updateUI(String result) {
        if (resultCallback != null) {
            resultCallback.onFinish(result);
        }
    }
}
