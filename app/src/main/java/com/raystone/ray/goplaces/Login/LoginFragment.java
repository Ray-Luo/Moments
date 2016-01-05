package com.raystone.ray.goplaces.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.raystone.ray.goplaces.Helper.MoveAmongFragments;
import com.raystone.ray.goplaces.Helper.Place;
import com.raystone.ray.goplaces.Helper.RecycleThread;
import com.raystone.ray.goplaces.Map.MyMapFragment;
import com.raystone.ray.goplaces.R;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Ray on 11/14/2015.
 */
public class LoginFragment extends android.app.Fragment {

    //UI reference
    private View mView;
    private AutoCompleteTextView mEmailView;     // input Email
    private EditText mPasswordView;             //  input password
    private Button mSignInButton;               //  the sign in button
    private LoginButton mSignInWithFacebook;   //  sign in with facebook button
    private TextView mSignUp;                   //  sign up
    private TextView mSkip;                      //  skip sign in
    private CallbackManager mCallbackManager;   //  callback manager for managing facebook login in/out result
    public static LoginManager mFacebookLoginManager;   //  This will be used to help force log out facebook when return to home.
    //public AccessTokenTracker accessTokenTracker;
    private RecycleThread mRecycleLoadProfilePicThread;
    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override     // This callback tells what to do when successfully logged in/out
        public void onSuccess(LoginResult loginResult) {
            AccessTokenTracker  accessTokenTracker = new AccessTokenTracker() {   // This is used to detected will one has logged
                @Override                                                        // in/out facebook
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                    updateWithToken(newAccessToken);
                }
            };
            updateWithToken(AccessToken.getCurrentAccessToken());
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException error) {
        }
    };

    public static LoginFragment newInstance()
    {
        LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }



    private void load()
    {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginManager = LoginManager.getInstance();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mSignInButton = null;
        mSignInWithFacebook = null;
        mSignUp = null;
        mSkip = null;
        mCallbackManager = null;
        mPasswordView = null;
        mEmailView = null;
        mView = null;
        if(mRecycleLoadProfilePicThread != null){
            mRecycleLoadProfilePicThread.exit = false;}
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        load();
        mView = inflater.inflate(R.layout.login,container,false);
        mEmailView = (AutoCompleteTextView)mView.findViewById(R.id.email);
        mPasswordView = (EditText)mView.findViewById(R.id.password);
        mSignInButton = (Button)mView.findViewById(R.id.sign_in);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValues())  // This will check if the input user name and password meet certain format requirements
                {   // This is intended to check if the user name and password match with whatever one has registered
                    SharedPreferences preferences = getActivity().getSharedPreferences("register_data", Context.MODE_PRIVATE);
                    String email = preferences.getString("email","");
                    String password = preferences.getString("password","");
                    if (mEmailView.getText().toString().equals(email) && mPasswordView.getText().toString().equals(password))
                    {
                        jumpToMap();
                        Place.mUserName = email;
                        Place.mUserProfilePic = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    }else
                    {
                        Toast.makeText(getActivity(),"Incorrect user name or password",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Some more work with the login with facebook button
        mSignInWithFacebook = (LoginButton)mView.findViewById(R.id.sign_in_facebook);
        mSignInWithFacebook.setBackgroundResource(R.drawable.rounded_button_facebook);
        mSignInWithFacebook.setReadPermissions("user_friends");
        mSignInWithFacebook.setFragment(this);
        mSignInWithFacebook.registerCallback(mCallbackManager, mCallback);

        //  This leads to the sign up interface.
        mSignUp = (TextView)mView.findViewById(R.id.sign_up);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToRegister();
            }
        });

        // This help skip the sign in/up process
        mSkip = (TextView)mView.findViewById(R.id.skip);
        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToMap();
                Place.mUserName = "Not Signed In";
                Place.mUserProfilePic = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            }
        });
        return mView;
    }

    // This is intend to detect whether one has logged in/out with facebook
    private void updateWithToken(AccessToken currentAccessToken) {
        if(getActivity() != null){
        if (currentAccessToken != null) {
            // if logged in, it will try to retrieve the profile info from facebook and set the user name and profile picture
            Profile profile = Profile.getCurrentProfile();
            if(profile != null) {
                Place.mUserName = profile.getName();   // set the user name from facebook
                Place.mUserProfileUri = profile.getProfilePictureUri(72, 72);   // set profile picture from facebook

                // Starting a new thread to get the picture from facebook http
                mRecycleLoadProfilePicThread = new RecycleThread() {
                    @Override
                    public void run() {
                        super.run();
                        while(mRecycleLoadProfilePicThread.exit)
                        {
                            try{
                                URL newURL = new URL(Place.mUserProfileUri.toString());
                                Place.mUserProfilePic = BitmapFactory.decodeStream(newURL.openConnection().getInputStream());}
                            catch (IOException e)
                            {e.printStackTrace();}
                        }
                    }
                };
                mRecycleLoadProfilePicThread.start();
                // After the above happened, it will jump to the main app interface
                jumpToMap();
                }
            }
        }
    }

    public void jumpToMap()
    {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("MAPFRAGMENT");
        if(fragment == null) {
            fragment = MyMapFragment.newInstance();
        }
        trans.replace(R.id.login_fragment_container, fragment,"MAPFRAGMENT");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "MAPFRAGMENT";

    }

    public void jumpToRegister()
    {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        android.app.FragmentTransaction trans = fm.beginTransaction();
        android.app.Fragment fragment = fm.findFragmentByTag("REGISTER");
        if(fragment == null) {
            fragment = RegisterFragment.newInstance();
        }
        trans.replace(R.id.login_fragment_container, fragment,"REGISTER");
        trans.addToBackStack(null);
        trans.commit();
        MoveAmongFragments.currentFragment = "REGISTER";

    }

    // This helps to check whether the input user name and email address meet certain requirements
    private boolean checkValues()
    {
        boolean isFieldValid = true;
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        if(!TextUtils.isEmpty(password) && !isPasswordValid(password))
        {
            mPasswordView.setError("Password is too short");
            isFieldValid = false;
            return isFieldValid;
        }

        if(TextUtils.isEmpty(email))
        {
            mEmailView.setError("This field is required");
            isFieldValid = false;
            return isFieldValid;
        }
        else if(!isEmailValid(email))
        {
            mEmailView.setError("The email format is invalid");
            isFieldValid = false;
            return isFieldValid;
        }
        return isFieldValid;

    }

    private boolean isPasswordValid(String password)
    {
        return password.length()>8;
    }

    private boolean isEmailValid(String email)
    {
        return email.contains("@");
    }


    // This is mainly intended for what to do after the facebook log in/out process finished and returned to the login interface
    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

}
