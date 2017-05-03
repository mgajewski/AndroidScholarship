package com.myaps.popularmovies.views;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myaps.popularmovies.R;
import com.myaps.popularmovies.model.Video;
import com.myaps.popularmovies.utils.IBindable;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by mgajewski on 2017-03-27.
 */

@EViewGroup(R.layout.trailer_item)
public class TrailerItemView extends LinearLayout implements IBindable<Video> {

    @ViewById(R.id.tv_video_name)
    TextView trailerNameView;

    Video currentData;

    public TrailerItemView(Context context) {
        super(context);
        setLayoutParams(generateDefaultLayoutParams());
    }

    @Override
    public void bind(Video data) {
        currentData = data;
        trailerNameView.setText(data.getName());
    }
}
