package com.raystone.ray.goplaces_v1.Login;

import android.support.v4.app.Fragment;

import com.raystone.ray.goplaces_v1.BaseActivity;

/**
 * Created by Ray on 11/14/2015.
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected Fragment createFragment()
    {
        return LoginFragment.newInstance();
    }

}
