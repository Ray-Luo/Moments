package com.raystone.ray.goplaces_v1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.raystone.ray.goplaces_v1.R;

/**
 * Created by Ray on 11/14/2015.
 * The activities hosting fragments will inherit this class which facilitates adding fragment to their corresponding activity.
 * The BaseActivity will be added into the activity collector, so every class inheriting the BaseActivity will also be added into the
 * the activity collector.
 */
public abstract class BaseActivity extends AppCompatActivity{

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment == null)
        {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container,fragment).commit();
        }
        ActivityCollector.addActivity(this);
    }




}
