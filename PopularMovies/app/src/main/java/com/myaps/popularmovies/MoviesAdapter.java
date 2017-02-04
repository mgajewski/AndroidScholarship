package com.myaps.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewAnimator;

import com.myaps.popularmovies.model.Movie;
import com.myaps.popularmovies.utils.IDataProvider;
import com.myaps.popularmovies.utils.JsonUtils;
import com.myaps.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

/**
 * Created by mgajewski on 2017-01-28.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieAdapterViewHolder> {

    IDataProvider currentDataProvider;

    public interface MovieClickListener {
        void onClick(JSONObject movie);
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ViewAnimator animator;
        ImageView imageView;
        Context context;
        JSONObject currentData;
        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            animator = (ViewAnimator) itemView.findViewById(R.id.animator);
            imageView = (ImageView) itemView.findViewById(R.id.poster);
            itemView.setOnClickListener(this);
        }

        public void setData(JSONObject data) {
            currentData = data;
            loadImage();
        }

        @Override
        public void onClick(View v) {
            JSONObject item = currentData;
            if (itemClickListener != null) {
                itemClickListener.onClick(item);
            }
        }

        private void loadImage() {
            animator.setDisplayedChild(1);
            String posterPath = JsonUtils.getImageUrl(currentData);
            Uri uri = NetworkUtils.getImageUrl(posterPath);
            Picasso.with(context).load(uri).error(R.mipmap.placeholder).into(imageView, new Callback.EmptyCallback() {
                @Override public void onSuccess() {
                    animator.setDisplayedChild(0);
                }
                @Override public void onError() {
                    animator.setDisplayedChild(0);
                }
            });
        }
    }

    private MovieClickListener itemClickListener;

    public MoviesAdapter(MovieClickListener clickListener) {
        itemClickListener = clickListener;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        JSONObject item = null;
        if (currentDataProvider != null) {
            item = currentDataProvider.getItem(position);
        }
        holder.setData(item);
    }

    @Override
    public int getItemCount() {
        if (currentDataProvider != null) {
            return currentDataProvider.getCount();
        } else {
            return 0;
        }
    }

    public void setDataProvider(IDataProvider provider) {
        currentDataProvider = provider;
        notifyDataSetChanged();
    }
}
