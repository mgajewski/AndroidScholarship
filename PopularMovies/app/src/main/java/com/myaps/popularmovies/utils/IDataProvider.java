package com.myaps.popularmovies.utils;

/**
 * Created by mgajewski on 2017-01-29.
 */

public interface IDataProvider<T> {
    int getCount();
    T getItem(int position);
}
