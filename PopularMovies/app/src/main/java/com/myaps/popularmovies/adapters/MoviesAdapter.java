package com.myaps.popularmovies.adapters;

import android.content.Context;
import android.view.ViewGroup;

import com.myaps.popularmovies.utils.IItemClickListener;
import com.myaps.popularmovies.utils.RecyclerViewAdapterBase;
import com.myaps.popularmovies.views.MovieItemView;
import com.myaps.popularmovies.views.MovieItemView_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.json.JSONObject;

/**
 * Created by mgajewski on 2017-03-22.
 */

@EBean
public class MoviesAdapter extends RecyclerViewAdapterBase<JSONObject, MovieItemView> {

    @RootContext
    Context context;

    private IItemClickListener<JSONObject> itemClickListener;

    public void setOnItemClickListener(IItemClickListener<JSONObject> clickListener) {
        itemClickListener = clickListener;
    }

    @Override
    protected MovieItemView onCreateItemView(ViewGroup parent, int viewType) {
        MovieItemView view = MovieItemView_.build(context);
        view.setOnItemClickListener(itemClickListener);
        return view;
    }
}
