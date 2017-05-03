package com.myaps.popularmovies.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by mgajewski on 2017-03-05.
 */

public class ViewWrapper<V extends View> extends RecyclerView.ViewHolder {
    private V view;

    public ViewWrapper(V itemView) {
        super(itemView);
        view = itemView;
    }

    public V getView() {
        return view;
    }
}
