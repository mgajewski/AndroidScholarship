package com.myaps.popularmovies.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.myaps.popularmovies.model.Video;
import com.myaps.popularmovies.utils.IDataProvider;
import com.myaps.popularmovies.utils.JsonUtils;
import com.myaps.popularmovies.views.TrailerItemView;
import com.myaps.popularmovies.views.TrailerItemView_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgajewski on 2017-03-27.
 */

@EBean
public class TrailersListAdapter extends BaseAdapter {

    private IDataProvider<JSONObject> dataProvider;

    private ArrayList<Video> listItems = new ArrayList<>();

    @RootContext
    Context context;

    @Override
    public int getCount() {
        int count = 0;
        if (dataProvider != null) {
            count = dataProvider.getCount();
        }
        return count;
    }

    @Override
    public Video getItem(int position) {
        if (position < listItems.size()) {
            return listItems.get(position);
        } else if (dataProvider != null) {
            JSONObject json = dataProvider.getItem(position);
            Video trailer = JsonUtils.getFromJson(Video.class, json);
            listItems.add(trailer);
            return trailer;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TrailerItemView trailerItemView;
        if (convertView == null) {
            trailerItemView = TrailerItemView_.build(context);
        } else {
            trailerItemView = (TrailerItemView) convertView;
        }
        Video data = getItem(position);
        if (data != null) {
            trailerItemView.bind(data);
        }
        return trailerItemView;
    }

    public void setDataProvider(IDataProvider<JSONObject> provider) {
        listItems.clear();
        dataProvider = provider;
        notifyDataSetChanged();
    }
}
