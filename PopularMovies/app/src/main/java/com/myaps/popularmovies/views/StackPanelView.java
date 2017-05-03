package com.myaps.popularmovies.views;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * Created by mgajewski on 2017-04-30.
 *
 * Easy stack panel for show all views from adapter
 * Don't use it for big collections!!!!
 * TODO: This is not general component it need much more work
 */

public class StackPanelView extends LinearLayout {

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            refreshViews();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };

    Context context;
    BaseAdapter adapter;
    OnItemClickListener itemClickListener;

    public StackPanelView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }

    public void setAdapter(BaseAdapter adapter) {
        cleanCurrentAdapter();
        setNewAdapter(adapter);
        refreshViews();
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return itemClickListener;
    }

    private void cleanCurrentAdapter() {
        removeAllViews();
        if (adapter != null) {
            adapter.unregisterDataSetObserver(dataSetObserver);
            adapter = null;
        }
    }

    private void setNewAdapter(BaseAdapter newAdapter) {
        if (newAdapter != null) {
            adapter = newAdapter;
            adapter.registerDataSetObserver(dataSetObserver);
        }
    }

    private void refreshViews() {
        if (adapter != null) {
            int count = adapter.getCount();
            for( int i = 0; i < count; ++i) {
                View currentView = this.getChildAt(i);
                View child = adapter.getView(i, currentView, this);
                if (child != currentView) {
                    if (currentView != null) {
                        currentView.setOnClickListener(null);
                        removeView(currentView);
                    }
                    if (child != null) {
                        child.setOnClickListener(new ChildClickListener(i));
                        addView(child, i);
                    }
                }
                else {
                    invalidate();
                }
            }
        }
    }

    private class ChildClickListener implements OnClickListener {
        int position;
        public ChildClickListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, position);
            }
        }
    }
}
