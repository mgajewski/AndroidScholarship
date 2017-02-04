package com.myaps.popularmovies.utils;

/**
 * Created by mgajewski on 2017-01-31.
 */

@FunctionalInterface
public interface IStaticSetter<O, T> {
    void setValue(O object, T value);
}
