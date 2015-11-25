package com.raystone.ray.goplaces_v1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/**
 * Created by Ray on 11/23/2015.
 */
public class PicGridView extends GridView {

    public PicGridView(Context context)
    {
        super(context);
    }

    public PicGridView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec,expandSpec);
    }
}
