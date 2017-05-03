package com.myaps.popularmovies.tasks;
import com.myaps.popularmovies.MainActivity;
import com.myaps.popularmovies.services.MoviesService;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

/**
 * Created by mgajewski on 2017-03-24.
 */

@EBean
public class ReloadMoviesTask {

    @RootContext
    MainActivity activity;

    @Bean
    MoviesService moviesService;

    @Background
    public void reloadMovies(MoviesService.SortType currentSortType, boolean rewind) {
        boolean result = moviesService.reloadMovies(currentSortType);
        updateUI(result, rewind);
    }

    @UiThread
    void updateUI(boolean result, boolean rewind) {
        activity.moviesReloadTaskFinished(result, rewind);
    }
}
