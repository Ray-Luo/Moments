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

/**
 * Created by Ray on 11/14/2015.
 */
public class RegisterFragment extends Fragment {

    private AutoCompleteTextView mRegisterEmail;
    private EditText mRegisterPassword;
    private Button mRegisterButton;

    public static RegisterFragment newInstance()
    {
        RegisterFragment registerFragment = new RegisterFragment();
        return registerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.register,container,false);
        mRegisterEmail = (AutoCompleteTextView)v.findViewById(R.id.register_email);
        mRegisterPassword = (EditText)v.findViewById(R.id.register_password);
        mRegisterButton = (Button)v.findViewById(R.id.register);
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
                    Intent intent = new Intent(getActivity(),MyMapActivity.class);
                    startActivity(intent);
                }
            }
        });
        return v;
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
        else if(!isEmailVaild(email))
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

    private boolean isEmailVaild(String email)
    {
        return email.contains("@");
    }

}
