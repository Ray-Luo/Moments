package com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel4;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.raystone.ray.goplaces_v1.BaseActivity;

/**
 * Created by Ray on 11/25/2015.
 */
public class ViewPicActivity extends BaseActivity {

    @Override
    protected Fragment createFragment()
    {return ViewPicPagerFragment.newInstance();}


}
