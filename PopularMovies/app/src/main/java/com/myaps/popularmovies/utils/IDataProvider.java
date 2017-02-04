package com.myaps.popularmovies.utils;

import org.json.JSONObject;

/**
 * Created by mgajewski on 2017-01-29.
 */

public interface IDataProvider {
    int getCount();
    JSONObject getItem(int position);
}
