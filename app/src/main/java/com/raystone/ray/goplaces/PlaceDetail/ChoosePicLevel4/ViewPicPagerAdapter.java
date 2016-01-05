package com.raystone.ray.goplaces.PlaceDetail.ChoosePicLevel4;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Ray on 11/25/2015.
 */
public class ViewPicPagerAdapter extends PagerAdapter {

    private ArrayList<View> listViews;         // contents
    private int size;                         //  size of pagers
    public ViewPicPagerAdapter(ArrayList<View> listViews) {    //  constructor
        // 初始化viewpager的时候给的一个页面
        this.listViews = listViews;
        size = listViews == null ? 0 : listViews.size();
    }

    // when the contents of the adapter changes, reset its contents
    public void setListViews(ArrayList<View> listViews) {
        this.listViews = listViews;
        size = listViews == null ? 0 : listViews.size();
    }

    // return the size of pagers
    public int getCount() {
        return size;
    }

    //  force refresh view
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    //  destroy one view
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(listViews.get(position % size));
    }


    //  return the current view
    public Object instantiateItem(ViewGroup container, int position) {
        try {
            ((ViewPager) container).addView(listViews.get(position % size), 0);

        } catch (Exception e) {
        }
        return listViews.get(position % size);
    }

    //  when sliding the ViewPager, use this to see the view returned by the "instantiateItem" belongs to which children of the ViewPager, that is, to locate its position
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

}
