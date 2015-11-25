package com.raystone.ray.goplaces_v1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Ray on 11/21/2015.
 */
public class PlaceActivity extends BaseActivity {

    @Override
    protected Fragment createFragment()
    {
        return PlaceDetailFragment.newInstance();
    }

}
