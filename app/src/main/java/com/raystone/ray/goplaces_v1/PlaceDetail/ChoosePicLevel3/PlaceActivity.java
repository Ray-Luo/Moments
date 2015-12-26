package com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.raystone.ray.goplaces_v1.BaseActivity;
import com.raystone.ray.goplaces_v1.Login.LoginActivity;
import com.raystone.ray.goplaces_v1.MoveAmongFragments;
import com.raystone.ray.goplaces_v1.MyMapActivity;
import com.raystone.ray.goplaces_v1.Place;
import com.raystone.ray.goplaces_v1.R;

/**
 * Created by Ray on 11/21/2015.
 */
public class PlaceActivity extends BaseActivity {

    @Override
    protected Fragment createFragment()
    {
        return PlaceDetailFragment.newInstance();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);;
        if(fragment != null){
            fragment.onDestroy();
            fragment = createFragment();}
        fm.beginTransaction().add(R.id.fragment_container,fragment).commitAllowingStateLoss();;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(this, MyMapActivity.class);
        startActivity(intent);
        finish();
    }

}
