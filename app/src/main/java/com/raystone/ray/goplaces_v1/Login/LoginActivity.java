package com.raystone.ray.goplaces_v1.Login;

import android.support.v4.app.Fragment;

import com.raystone.ray.goplaces_v1.ActivityCollector;
import com.raystone.ray.goplaces_v1.BaseActivity;

/**
 * Created by Ray on 11/14/2015.
 * This class hosts the login interface(fragment). After the back button being pressed, all the activities and their fragments
 * will be ended.
 */
public class LoginActivity extends BaseActivity {

    @Override
    protected Fragment createFragment()
    {
        return LoginFragment.newInstance();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(LoginFragment.mFacebookLoginManager != null)
            LoginFragment.mFacebookLoginManager.logOut();
        ActivityCollector.finishAll();
    }

}
