package com.raystone.ray.goplaces_v1.PlaceList;

import android.support.v4.app.Fragment;

import com.raystone.ray.goplaces_v1.BaseActivity;

/**
 * Created by Ray on 12/2/2015.
 */
public class PlaceListActivity extends BaseActivity {

    @Override
    protected Fragment createFragment()
    {
        return PlaceListFragment.newInstance();
    }

}
