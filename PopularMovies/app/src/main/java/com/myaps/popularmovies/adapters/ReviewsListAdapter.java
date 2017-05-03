package com.myaps.popularmovies.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.myaps.popularmovies.model.Review;
import com.myaps.popularmovies.utils.IDataProvider;
import com.myaps.popularmovies.utils.JsonUtils;
import com.myaps.popularmovies.views.ReviewItemView;
import com.myaps.popularmovies.views.ReviewItemView_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mgajewski on 2017-03-27.
 */

@EBean
public class ReviewsListAdapter extends BaseAdapter {

    private IDataProvider<JSONObject> dataProvider;
    private ArrayList<Review> listItems = new ArrayList<>();

    @RootContext
    Context context;

    @Override
    public int getCount() {
        if (dataProvider != null) {
            return dataProvider.getCount();
        }
        return 0;
    }

    @Override
    public Review getItem(int position) {
        if (position < listItems.size()) {
            return listItems.get(position);
        } else if (dataProvider != null) {
            JSONObject json = dataProvider.getItem(position);
            Review review = JsonUtils.getFromJson(Review.class, json);
            listItems.add(review);
            return  review;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReviewItemView reviewItemView;
        if (convertView == null) {
            reviewItemView = ReviewItemView_.build(context);
        } else {
            reviewItemView = (ReviewItemView) convertView;
        }
        Review data = getItem(position);
        if (data != null) {
            reviewItemView.bind(data);
        }
        return reviewItemView;
    }

    public void setDataProvider(IDataProvider<JSONObject> provider) {
        listItems.clear();
        dataProvider = provider;
        notifyDataSetChanged();
    }
}
