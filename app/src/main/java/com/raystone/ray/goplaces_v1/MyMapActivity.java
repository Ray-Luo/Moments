package com.raystone.ray.goplaces_v1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.raystone.ray.goplaces_v1.Login.LoginActivity;

/**
 * Created by Ray on 11/15/2015.
 */
public class MyMapActivity extends BaseActivity {

    @Override
    protected Fragment createFragment()
    {
        return MyMapFragment.newInstance();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        MoveAmongFragments.markerToDetail = false;
        finish();
    }

}
