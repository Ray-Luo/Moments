package com.raystone.ray.goplaces_v1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 * Created by Ray on 11/14/2015.
 */
public class LoginFragment extends Fragment {

    //UI reference
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button mSignInButton;
    private TextView signUp;
    private TextView skip;

    public static LoginFragment newInstance()
    {
        LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                if(checkValues())
                {
                    SharedPreferences preferences = getActivity().getSharedPreferences("register_data", Context.MODE_PRIVATE);
                    String email = preferences.getString("email","");
                    String password = preferences.getString("password","");
                    if (mEmailView.getText().toString().equals(email) && mPasswordView.getText().toString().equals(password))
                    {
                        Intent intent = new Intent(getActivity(),MyMapActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        signUp = (TextView)v.findViewById(R.id.sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        skip = (TextView)v.findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MyMapActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

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
        else if(!isEmailVaild(email))
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

    private boolean isEmailVaild(String email)
    {
        return email.contains("@");
    }

}
