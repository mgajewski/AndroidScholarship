package com.myaps.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.myaps.popularmovies.adapters.ReviewsListAdapter;
import com.myaps.popularmovies.adapters.TrailersListAdapter;
import com.myaps.popularmovies.data.FavMovieTransportClass;
import com.myaps.popularmovies.model.Movie;
import com.myaps.popularmovies.model.Video;
import com.myaps.popularmovies.services.ReviewService;
import com.myaps.popularmovies.services.VideoService;
import com.myaps.popularmovies.tasks.Base64Task;
import com.myaps.popularmovies.tasks.ReloadReviewsTask;
import com.myaps.popularmovies.tasks.ReloadTrailersTask;
import com.myaps.popularmovies.utils.FavouritesHelper;
import com.myaps.popularmovies.utils.IDataProvider;
import com.myaps.popularmovies.utils.JsonUtils;
import com.myaps.popularmovies.utils.NetworkUtils;
import com.myaps.popularmovies.views.StackPanelView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.InjectMenu;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

@EActivity(R.layout.activity_details)
public class DetailsActivity extends AppCompatActivity {

    private Movie movieData;
    private JSONObject movieRawData;
    private IDataProvider trailersRawData;
    private IDataProvider reviewsRawData;
    private Bitmap posterBitmap;

    private boolean favToggleState = false;

    @Extra(Intent.EXTRA_TEXT)
    void setTextExtra(String data) {
        try {
            JSONObject jsonData = new JSONObject(data);
            movieData = JsonUtils.getFromJson(Movie.class, jsonData);
            movieRawData = jsonData;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @ViewById(R.id.tv_movie_title)
    TextView titleTextView;

    @ViewById(R.id.tv_original_title)
    TextView orgTitleTextView;

    @ViewById(R.id.tv_release_date)
    TextView releaseTextView;

    @ViewById(R.id.tv_rating)
    TextView ratingTextView;

    @ViewById(R.id.tv_overview)
    TextView overviewTextView;

    @ViewById(R.id.iv_detail_poster)
    ImageView posterImageView;

    @ViewById(R.id.va_load_image)
    ViewAnimator loadingImageAnimator;

    @ViewById(R.id.sp_trailers)
    StackPanelView trailersView;

    @ViewById(R.id.sp_reviews)
    StackPanelView reviewsView;

    @ViewById(R.id.cb_favourite)
    CheckBox favButtonView;

    MenuItem menuShareItem;
    ShareActionProvider shareActionProvider;
    @InjectMenu
    void setMenu(Menu detailsMenu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, detailsMenu);
        menuShareItem = detailsMenu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuShareItem);
    }
//    @OptionsItem(R.id.action_share)
//    void actionShare(MenuItem item) {
//
//    }

    private void setShareUri(Video trailer) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        Uri uri = Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey());
        shareIntent.putExtra(Intent.EXTRA_TEXT, uri.toString());
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Bean
    ReviewService reviewService;

    @Bean
    VideoService videoService;

    @Bean
    TrailersListAdapter trailersAdapter;

    @Bean
    ReviewsListAdapter reviewsAdapter;

    @Bean
    FavouritesHelper favHelper;

    @NonConfigurationInstance
    @Bean
    ReloadTrailersTask reloadTrailersTask;

    @NonConfigurationInstance
    @Bean
    ReloadReviewsTask reloadReviewsTask;

    @NonConfigurationInstance
    @Bean
    Base64Task base64Encoder;

    @AfterViews
    void bindData() {
        trailersView.setOnItemClickListener((view, position) -> {
            if (trailersAdapter != null) {
                Video videoInfo = trailersAdapter.getItem(position);
                trailersListViewItemClicked(videoInfo);
            }
        });
        trailersView.setAdapter(trailersAdapter);
        reviewsView.setAdapter(reviewsAdapter);
        if (movieData != null) {
            loadImage();
            titleTextView.setText(movieData.getTitle());
            orgTitleTextView.setText(movieData.getOriginalTitle());
            releaseTextView.setText(movieData.getReleaseDate());
            overviewTextView.setText(movieData.getOverview());
            String rating = movieData.getVoteAverage() + "/10";
            ratingTextView.setText(rating);

            favToggleState = favHelper.isFavourite(movieData.getId());
            favButtonView.setChecked(favToggleState);
            favButtonView.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    addToFavourites();
                } else {
                    removeFromFavourites();
                }
            });

            //TODO: Load from fav only when no internet connection
            reloadTrailersTask.reloadTrailers(movieData.getId(), favToggleState);
            reloadReviewsTask.reloadReviews(movieData.getId(), favToggleState);
        }
    }

    void trailersListViewItemClicked(Video trailer) {
        if (trailer != null && trailer.getSite().equals("YouTube")) {
            Uri uri = Uri.parse("vnd.youtube:" + trailer.getKey());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(getPackageManager()) == null) {
                uri = Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey());
                intent = new Intent(Intent.ACTION_VIEW, uri);
            }
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadImage() {
        loadingImageAnimator.setDisplayedChild(1);
        String posterPath = movieData.getPosterPath();
        Uri uri = NetworkUtils.getImageUrl(posterPath);

        Picasso.with(this).load(uri).error(R.mipmap.placeholder).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                posterBitmap = bitmap;
                posterImageView.setImageBitmap(bitmap);
                loadingImageAnimator.setDisplayedChild(0);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                posterImageView.setImageDrawable(errorDrawable);
                loadingImageAnimator.setDisplayedChild(0);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                posterImageView.setImageDrawable(placeHolderDrawable);
                loadingImageAnimator.setDisplayedChild(0);
            }
        });
    }

    private void addToFavourites() {
        if (posterBitmap != null) {
            base64Encoder.encode(posterBitmap, new Base64Task.Callback() {
                @Override
                public void result(String base64Image) {
                    FavMovieTransportClass favMovie = new FavMovieTransportClass(movieData.getId(), movieRawData, base64Image);
                    favHelper.addToFavourites(favMovie, trailersRawData, reviewsRawData);
                }
            });
        } else {
            FavMovieTransportClass favMovie = new FavMovieTransportClass(movieData.getId(), movieRawData, null);
            favHelper.addToFavourites(favMovie, trailersRawData, reviewsRawData);
        }
        favToggleState = true;
    }

    private void removeFromFavourites() {
        favHelper.removeFromFavourites(movieData.getId());
        favToggleState = false;
    }

    public void trailersReloadTaskFinished(boolean result) {
        if (result == true && movieData != null) {
            IDataProvider dataProvider = videoService.getDataProvider(movieData.getId());
            trailersRawData = dataProvider;
            trailersAdapter.setDataProvider(dataProvider);
            setShareUri(trailersAdapter.getItem(0));
        }
    }

    public void reviewsReloadTaskFinished(boolean result) {
        if (result == true && movieData != null) {
            IDataProvider dataProvider = reviewService.getDataProvider(movieData.getId());
            reviewsRawData = dataProvider;
            reviewsAdapter.setDataProvider(dataProvider);
        }
    }
}
