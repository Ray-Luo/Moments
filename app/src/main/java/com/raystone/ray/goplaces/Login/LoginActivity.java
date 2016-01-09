package com.raystone.ray.goplaces.Login;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.raystone.ray.goplaces.Helper.MoveAmongFragments;
import com.raystone.ray.goplaces.Helper.MyBitMap;
import com.raystone.ray.goplaces.PlaceDetail.ChoosePicLevel3.PlaceDetailFragment;
import com.raystone.ray.goplaces.PlaceList.PlaceListFragment;
import com.raystone.ray.goplaces.R;

/**
 * Created by Ray on 11/14/2015.
 * This class hosts the login interface(fragment). After the back button being pressed, it will jump to the certain fragment
 */
public class LoginActivity extends AppCompatActivity {


    private android.app.FragmentTransaction trans;
    protected android.app.Fragment createFragment()
    {
        return LoginFragment.newInstance();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_fragment);
        android.app.FragmentManager fm = getFragmentManager();
        android.app.Fragment fragment = fm.findFragmentById(R.id.login_fragment_container);
        if(fragment == null)
        {
            fragment = createFragment();
            android.app.FragmentTransaction trans = fm.beginTransaction();
            trans.add(R.id.login_fragment_container, fragment,"LOGINFRAGMENT");
            trans.addToBackStack(null);
            trans.commit();
            MoveAmongFragments.currentFragment = "LOGINFRAGMENT";
        }
    }

    @Override
    public void onBackPressed()
    {
        android.app.FragmentManager fm = getFragmentManager();
        trans = fm.beginTransaction();
        switch (MoveAmongFragments.currentFragment)
        {
            case "LOGINFRAGMENT":
            {
                super.onBackPressed();
                MyBitMap.bmp = null;
                MyBitMap.dir = null;
                //Place = null;
                android.app.Fragment fragment = fm.findFragmentByTag("LOGINFRAGMENT");
                trans.remove(fragment);
                trans.commit();
                if(LoginFragment.mFacebookLoginManager != null){
                    LoginFragment.mFacebookLoginManager.logOut();
                    LoginFragment.mFacebookLoginManager = null;}
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                finish();
                break;
            }

            case "MAPFRAGMENT":
            {
                android.app.Fragment fragment = fm.findFragmentByTag("LOGINFRAGMENT");
                trans.replace(R.id.login_fragment_container, fragment, "LOGINFRAGMENT");
                trans.addToBackStack(null);
                trans.commit();
                MoveAmongFragments.currentFragment = "LOGINFRAGMENT";
                break;
            }

            case "PLACEDETAIL":
            {
                android.app.Fragment fragment = fm.findFragmentByTag("MAPFRAGMENT");
                trans.replace(R.id.login_fragment_container, fragment, "MAPFRAGMENT");
                trans.addToBackStack(null);
                trans.commit();
                MoveAmongFragments.currentFragment = "MAPFRAGMENT";
                PlaceDetailFragment. newPlace = null;
                break;
            }

            case "LEVEL1":
            {
                if(MoveAmongFragments.editPlaceMode)
                {
                    android.app.Fragment fragment = fm.findFragmentByTag("EDITPLACE");
                    trans.replace(R.id.login_fragment_container, fragment, "EDITPLACE");
                    trans.addToBackStack(null);
                    trans.commit();
                    MoveAmongFragments.currentFragment = "EDITPLACE";
                }
                else
                {
                    android.app.Fragment fragment = fm.findFragmentByTag("PLACEDETAIL");
                    trans.replace(R.id.login_fragment_container, fragment, "PLACEDETAIL");
                    trans.addToBackStack(null);
                    trans.commit();
                    MoveAmongFragments.currentFragment = "PLACEDETAIL";
                }
                break;
            }

            case "LEVEL2":
            {
                android.app.Fragment level2 = fm.findFragmentByTag("LEVEL2");
                if(level2 != null){
                    trans.remove(level2);
                    trans.commit();
                    fm.popBackStack();
                }
                MoveAmongFragments.currentFragment = "LEVEL1";
                break;
            }

            case "PLACELISTDETAIL":
            {
                android.app.Fragment fragment = fm.findFragmentByTag("MAPFRAGMENT");
                trans.replace(R.id.login_fragment_container, fragment, "MAPFRAGMENT");
                trans.addToBackStack(null);
                trans.commit();
                MoveAmongFragments.currentFragment = "MAPFRAGMENT";
                MoveAmongFragments.editPlace = null;
                break;
            }

            case "VIEWPICS":
            {
                android.app.Fragment fragment = fm.findFragmentByTag("VIEWPICS");
                trans.remove(fragment);
                trans.commit();
                fm.popBackStack();
                if(MoveAmongFragments.fromDetailToViewPics)
                    MoveAmongFragments.currentFragment = "PLACEDETAIL";
                else
                    MoveAmongFragments.currentFragment = "PLACELISTDETAIL";
                break;
            }

            case "EDITPLACE":
            {
                android.app.Fragment fragment = fm.findFragmentByTag("PLACELISTDETAIL");
                if(fragment == null) {
                    fragment = PlaceListFragment.newInstance();
                }
                trans.replace(R.id.login_fragment_container, fragment, "PLACELISTDETAIL");
                trans.addToBackStack(null);
                trans.commit();
                MoveAmongFragments.currentFragment = "PLACELISTDETAIL";
                break;
            }


        }

    }




}
