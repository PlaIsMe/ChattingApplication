package com.chattingapplication.chattingclient;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chattingapplication.chattingclient.AsyncTask.PostRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.SendTask;
import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Model.ExceptionError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private AuthenticationActivity authenticationActivity;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authenticationActivity = (AuthenticationActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextView txtInformRegister = (TextView) view.findViewById(R.id.txtInformRegister);
        Button btnLogin = (Button) view.findViewById(R.id.btnLogin);

        txtInformRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticationActivity.swapFragment(R.id.fragmentContainerAuthentication,
                        authenticationActivity.getRegisterFragment());
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextEmail = (EditText) view.findViewById(R.id.editTxtEmail);
                EditText editTextPassword = (EditText) view.findViewById(R.id.editTxtPassword);
                String jsonString;
                try {
                    jsonString = new JSONObject()
                            .put("email", editTextEmail.getText())
                            .put("password", editTextPassword.getText())
                            .toString();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                PostRequestTask postRequestTask = new PostRequestTask((AuthenticationActivity) getActivity());
                postRequestTask.execute("account/signin", jsonString, "loginResponse", "LoginFragment", "AuthenticationActivity");
            }
        });

        return view;
    }

    public void loginResponse(int responseCode, String jsonResponse) {
        Gson gson = new Gson();
        if (responseCode == 200) {
            authenticationActivity.currentAccount = gson.fromJson(jsonResponse, Account.class);
            try {
                String checkName = AuthenticationActivity.currentAccount.getUser().getFirstName();
                Intent mainActivity = new Intent(this.getContext(), MainActivity.class);
                startActivity(mainActivity);
            } catch (NullPointerException e) {
                authenticationActivity.swapFragment(R.id.fragmentContainerAuthentication, authenticationActivity.getSubRegisterFragment());
            }
        } else {
            ExceptionError exceptionError = gson.fromJson(jsonResponse, ExceptionError.class);
            Toast.makeText(authenticationActivity.getApplicationContext(), exceptionError.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}