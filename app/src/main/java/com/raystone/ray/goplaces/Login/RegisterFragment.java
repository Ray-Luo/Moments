package com.raystone.ray.goplaces.Login;

import android.content.Context;
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

import com.raystone.ray.goplaces.Helper.MoveAmongFragments;
import com.raystone.ray.goplaces.Helper.Place;
import com.raystone.ray.goplaces.Map.MyMapFragment;
import com.raystone.ray.goplaces.R;

/**
 * Created by Ray on 11/14/2015.
 */
public class RegisterFragment extends android.app.Fragment {

    private AutoCompleteTextView mRegisterEmail;
    private EditText mRegisterPassword;
    private Button mRegisterButton;
    private View mView;

    public static RegisterFragment newInstance()
    {
        RegisterFragment registerFragment = new RegisterFragment();
        return registerFragment;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mView = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.register,container,false);
        mRegisterEmail = (AutoCompleteTextView)mView.findViewById(R.id.register_email);
        mRegisterPassword = (EditText)mView.findViewById(R.id.register_password);
        mRegisterButton = (Button)mView.findViewById(R.id.register);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValues())
                {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("register_data", Context.MODE_PRIVATE).edit();
                    editor.putString("email",mRegisterEmail.getText().toString());
                    editor.putString("password",mRegisterPassword.getText().toString());
                    editor.commit();
                    Place.mUserName = mRegisterEmail.getText().toString();
                    Place.mUserProfilePic = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                    jumpToMap();
                }
            }
        });
        return mView;
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

    private boolean checkValues()
    {
        boolean isFieldValid = true;
        mRegisterEmail.setError(null);
        mRegisterPassword.setError(null);

        String email = mRegisterEmail.getText().toString();
        String password = mRegisterPassword.getText().toString();
        if(!TextUtils.isEmpty(password) && !isPasswordValid(password))
        {
            mRegisterPassword.setError("Password is too short");
            isFieldValid = false;
            return isFieldValid;
        }

        if(TextUtils.isEmpty(email))
        {
            mRegisterEmail.setError("This field is required");
            isFieldValid = false;
            return isFieldValid;
        }
        else if(!isEmailValid(email))
        {
            mRegisterEmail.setError("The email format is invalid");
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

}
