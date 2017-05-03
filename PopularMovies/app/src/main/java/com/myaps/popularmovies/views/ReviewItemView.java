package com.myaps.popularmovies.views;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myaps.popularmovies.R;
import com.myaps.popularmovies.model.Review;
import com.myaps.popularmovies.utils.IBindable;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by mgajewski on 2017-03-27.
 */

@EViewGroup(R.layout.review_item)
public class ReviewItemView extends LinearLayout implements IBindable<Review> {

    @ViewById(R.id.tv_review_title)
    TextView reviewTitleView;
    @ViewById(R.id.tv_review_content)
    TextView reviewContentView;

    Review currentData;
    Context context;

    public ReviewItemView(Context context) {
        super(context);
        this.context = context;
        setLayoutParams(generateDefaultLayoutParams());
    }

    @Override
    public void bind(Review data) {
        currentData = data;
        String title = context.getString(R.string.review_title) + " " + data.getAuthor();
        reviewTitleView.setText(title);
        reviewContentView.setText(data.getContent());
    }
}
