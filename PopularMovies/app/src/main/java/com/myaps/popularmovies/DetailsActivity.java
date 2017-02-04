package com.myaps.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.myaps.popularmovies.model.Movie;
import com.myaps.popularmovies.utils.JsonUtils;
import com.myaps.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {

    private Movie movieData;

    private TextView titleTextView;
    private TextView orgTitleTextView;
    private TextView releaseTextView;
    private TextView ratingTextView;
    private TextView overviewTextView;
    private ImageView posterImageView;
    private ViewAnimator loadingImageAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        titleTextView = (TextView) findViewById(R.id.tv_movie_title);
        orgTitleTextView = (TextView) findViewById(R.id.tv_original_title);
        releaseTextView = (TextView) findViewById(R.id.tv_release_date);
        ratingTextView = (TextView) findViewById(R.id.tv_rating);
        overviewTextView = (TextView) findViewById(R.id.tv_overview);
        posterImageView = (ImageView) findViewById(R.id.iv_detail_poster);
        loadingImageAnimator = (ViewAnimator) findViewById(R.id.va_load_image);

        Intent startedIntent = getIntent();

        if (startedIntent != null) {
            if (startedIntent.hasExtra(Intent.EXTRA_TEXT)) {
                String data = startedIntent.getStringExtra(Intent.EXTRA_TEXT);
                try {
                    JSONObject jsonData = new JSONObject(data);
                    movieData = JsonUtils.getFromJson(Movie.class, jsonData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (movieData != null) {
            bindData();
        }
    }

    private void bindData() {
        loadImage();
        titleTextView.setText(movieData.getTitle());
        orgTitleTextView.setText(movieData.getOriginalTitle());
        releaseTextView.setText(movieData.getReleaseDate());
        overviewTextView.setText(movieData.getOverview());
        String rating = movieData.getVoteAverage() + "/10";
        ratingTextView.setText(rating);
    }

    private void loadImage() {
        loadingImageAnimator.setDisplayedChild(1);
        String posterPath = movieData.getPosterPath();
        Uri uri = NetworkUtils.getImageUrl(posterPath);
        Picasso.with(this).load(uri).error(R.mipmap.placeholder).into(posterImageView, new Callback.EmptyCallback() {
            @Override public void onSuccess() {
                loadingImageAnimator.setDisplayedChild(0);
            }
            @Override public void onError() {
                loadingImageAnimator.setDisplayedChild(0);
            }
        });
    }
}
