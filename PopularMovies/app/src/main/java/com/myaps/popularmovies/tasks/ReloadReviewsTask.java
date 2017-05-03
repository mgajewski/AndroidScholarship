package com.myaps.popularmovies.tasks;

import com.myaps.popularmovies.DetailsActivity;
import com.myaps.popularmovies.services.ReviewService;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

/**
 * Created by mgajewski on 2017-03-27.
 */

@EBean
public class ReloadReviewsTask {
    @RootContext
    DetailsActivity activity;

    @Bean
    ReviewService reviewService;

    @Background
    public void reloadReviews(int movieId, boolean fromFav) {
        ReviewService.ReviewProvider provider = reviewService.getDataProvider(movieId);
        boolean result = true;
        if (provider == null) {
            result = reviewService.reloadReviews(movieId, fromFav);
        }
        updateUI(result);
    }

    @UiThread
    void updateUI(boolean result) {
        activity.reviewsReloadTaskFinished(result);
    }
}
