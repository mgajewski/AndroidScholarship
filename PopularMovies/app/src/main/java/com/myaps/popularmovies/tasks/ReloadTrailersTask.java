package com.myaps.popularmovies.tasks;
import com.myaps.popularmovies.DetailsActivity;
import com.myaps.popularmovies.services.VideoService;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

/**
 * Created by mgajewski on 2017-03-27.
 */

@EBean
public class ReloadTrailersTask {
    @RootContext
    DetailsActivity activity;

    @Bean
    VideoService videoService;

    @Background
    public void reloadTrailers(int movieId, boolean fromFav) {
        VideoService.VideoProvider provider = videoService.getDataProvider(movieId);
        boolean result = true;
        if (provider == null) {
            result = videoService.reloadVideos(movieId, fromFav);
        }
        updateUI(result);
    }

    @UiThread
    void updateUI(boolean result) {
        activity.trailersReloadTaskFinished(result);
    }
}
