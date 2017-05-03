package com.myaps.popularmovies.views;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewAnimator;

import com.myaps.popularmovies.R;
import com.myaps.popularmovies.utils.ConnectivityHelper;
import com.myaps.popularmovies.utils.FavBitmapsLoader;
import com.myaps.popularmovies.utils.FavBitmapsLoader_;
import com.myaps.popularmovies.utils.IBindable;
import com.myaps.popularmovies.utils.IItemClickListener;
import com.myaps.popularmovies.utils.JsonUtils;
import com.myaps.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.json.JSONObject;

/**
 * Created by mgajewski on 2017-03-22.
 */

@EViewGroup(R.layout.movie_item)
public class MovieItemView extends LinearLayout implements IBindable<JSONObject>, View.OnClickListener {

    @ViewById(R.id.animator)
    ViewAnimator animator;
    @ViewById(R.id.poster)
    ImageView imageView;

    @Bean
    ConnectivityHelper connectivityHelper;
    @Bean
    FavBitmapsLoader favBitmapsLoader;

    JSONObject currentData;
    IItemClickListener<JSONObject> onClickListener;

    public MovieItemView(Context context) {
        super(context);
        setOnClickListener(this);
    }

    @Override
    public void bind(JSONObject data) {
        currentData = data;
        loadImage();
    }

    public void setOnItemClickListener(IItemClickListener<JSONObject> listener) {
        onClickListener = listener;
    }

    private void loadImage() {
        animator.setDisplayedChild(1);
        if (connectivityHelper.isConnected()) {
            String posterPath = JsonUtils.getImageUrl(currentData);
            Uri uri = NetworkUtils.getImageUrl(posterPath);

            Picasso.with(this.getContext()).load(uri).error(R.mipmap.placeholder).into(imageView, new Callback.EmptyCallback() {
                @Override public void onSuccess() {
                    animator.setDisplayedChild(0);
                }
                @Override public void onError() {
                    animator.setDisplayedChild(0);
                }
            });
        } else {
            int movieId = JsonUtils.getMovieId(currentData);
            if (movieId >= 0) {
                favBitmapsLoader.with(this.getContext()).load(movieId).error(R.mipmap.placeholder).into(imageView, new FavBitmapsLoader.Callback() {
                    @Override
                    public void onSuccess() {
                        animator.setDisplayedChild(0);
                    }

                    @Override
                    public void onError() {
                        animator.setDisplayedChild(0);
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(currentData);
        }
    }
}
