package com.myaps.popularmovies.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.myaps.popularmovies.R;

/**
 * Created by mgajewski on 2017-03-27.
 */

public class ExpandableTextView extends android.support.v7.widget.AppCompatTextView implements View.OnClickListener
{
    private static final int MAX_LINES = 3;
    private int currentMaxLines = Integer.MAX_VALUE;

    public ExpandableTextView(Context context)
    {
        super(context);
        setOnClickListener(this);
    }
    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setOnClickListener(this);
    }

    public ExpandableTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setOnClickListener(this);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
    {
        post(() -> {
            if (getLineCount()>MAX_LINES)
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.mipmap.more);
            else
                setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            setMaxLines(MAX_LINES);
        });
    }


    @Override
    public void setMaxLines(int maxLines)
    {
        currentMaxLines = maxLines;
        super.setMaxLines(maxLines);
    }

    /* Custom method because standard getMaxLines() requires API > 16 */
    public int getMaxLines()
    {
        return currentMaxLines;
    }

    @Override
    public void onClick(View v)
    {
        /* Toggle between expanded collapsed states */
        if (getMaxLines() == Integer.MAX_VALUE)
            setMaxLines(MAX_LINES);
        else
            setMaxLines(Integer.MAX_VALUE);
    }

}
