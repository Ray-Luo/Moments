package com.raystone.ray.goplaces_v1.PlaceDetail.ChoosePicLevel1;

import android.support.v4.app.Fragment;

import com.raystone.ray.goplaces_v1.BaseActivity;

/**
 * Created by Ray on 11/24/2015.
 */
public class ImageBucketLevel1Activity extends BaseActivity {

    @Override
    protected Fragment createFragment()
    {
        return ImageBucketLevel1Fragment.newInstance();
    }
}
