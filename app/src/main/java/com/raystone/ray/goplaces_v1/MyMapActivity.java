package com.raystone.ray.goplaces_v1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Ray on 11/15/2015.
 */
public class MyMapActivity extends BaseActivity {

    @Override
    protected Fragment createFragment()
    {
        return MyMapFragment.newInstance();
    }


}
