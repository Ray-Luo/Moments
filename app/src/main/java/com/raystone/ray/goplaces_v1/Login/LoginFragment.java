package com.raystone.ray.goplaces_v1.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.raystone.ray.goplaces_v1.MyMapActivity;
import com.raystone.ray.goplaces_v1.Place;
import com.raystone.ray.goplaces_v1.R;
import com.raystone.ray.goplaces_v1.RegisterActivity;
import com.facebook.FacebookSdk;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Ray on 11/14/2015.
 */
public class LoginFragment extends Fragment {

    //UI reference
    private AutoCompleteTextView mEmailView;     // input Email
    private EditText mPasswordView;             //  input password
    private Button mSignInButton;               //  the sign in button
    private LoginButton mSignInWithFacebook;   //  sign in with facebook button
    private TextView mSignUp;                   //  sign up
    private TextView mSkip;                      //  skip sign in
    private CallbackManager mCallbackManager;   //  callback manager for managing facebook login in/out result
    public static LoginManager mFacebookLoginManager;   //  This will be used to help force log out facebook when return to home.
    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override     // This callback tells what to do when successfully logged in/out
        public void onSuccess(LoginResult loginResult) {
            AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {   // This is used to detected will one has logged
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

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.login,container,false);
        mEmailView = (AutoCompleteTextView)v.findViewById(R.id.email);
        mPasswordView = (EditText)v.findViewById(R.id.password);
        mSignInButton = (Button)v.findViewById(R.id.sign_in);
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
                        Intent intent = new Intent(getActivity(),MyMapActivity.class);
                        startActivity(intent);
                        LoginFragment.this.onDestroy();
                        Place.mUserName = email;
                    }else
                    {
                        Toast.makeText(getActivity(),"Incorrect user name or password",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Some more work with the login with facebook button
        mSignInWithFacebook = (LoginButton)v.findViewById(R.id.sign_in_facebook);
        mSignInWithFacebook.setBackgroundResource(R.drawable.rounded_button_facebook);
        mSignInWithFacebook.setReadPermissions("user_friends");
        mSignInWithFacebook.setFragment(this);
        mSignInWithFacebook.registerCallback(mCallbackManager, mCallback);

        //  This leads to the sign up interface.
        mSignUp = (TextView)v.findViewById(R.id.sign_up);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        // This help skip the sign in/up process
        mSkip = (TextView)v.findViewById(R.id.skip);
        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyMapActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    // This is intend to detect whether one has logged in/out with facebook
    private void updateWithToken(AccessToken currentAccessToken) {
        if (currentAccessToken != null) {
            // if logged in, it will try to retrieve the profile info from facebook and set the user name and profile picture
            mFacebookLoginManager = LoginManager.getInstance();
            Profile profile = Profile.getCurrentProfile();
            if(profile != null) {
                Place.mUserName = profile.getName();   // set the user name from facebook
                Place.mUserProfileUri = profile.getProfilePictureUri(72, 72);   // set profile picture from facebook

                // Starting a new thread to get the picture from facebook http
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            URL newURL = new URL(Place.mUserProfileUri.toString());
                            Place.mUserProfilePic = BitmapFactory.decodeStream(newURL.openConnection().getInputStream());}
                        catch (IOException e)
                        {e.printStackTrace();}
                    }
                }).start();

                // After the above happened, it will jump to the main app interface
                Intent intent = new Intent(getActivity(),MyMapActivity.class);
                startActivity(intent);
            }
        } else {
            // If logged out, set the user name and profile pic to "undefined"
            Place.mUserName = "Not Signed In";
            Place.mUserProfileUri = null;
        }
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
