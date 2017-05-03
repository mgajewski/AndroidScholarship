package com.myaps.popularmovies.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mgajewski on 2017-03-05.
 */

public abstract class RecyclerViewAdapterBase<T, V extends View> extends RecyclerView.Adapter<ViewWrapper<V>> {

    protected IDataProvider<T> dataProvider;

    @Override
    public int getItemCount() {
        if (dataProvider != null) {
            return dataProvider.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public final ViewWrapper<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewWrapper<>(onCreateItemView(parent, viewType));
    }

    @Override
    public void onBindViewHolder(ViewWrapper<V> holder, int position) {
        T item = null;
        if (dataProvider != null) {
            item = dataProvider.getItem(position);
        }
        V view = holder.getView();
        if (view instanceof IBindable) {
            ((IBindable<T>)view).bind(item);
        }
    }

    public void setDataProvider(IDataProvider<T> provider) {
        dataProvider = provider;
        notifyDataSetChanged();
    }

    protected abstract V onCreateItemView(ViewGroup parent, int viewType);
}
