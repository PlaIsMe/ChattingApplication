package com.chattingapplication.chattingclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chattingapplication.chattingclient.AsyncTask.PostRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.SendTask;
import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Model.ExceptionError;
import com.chattingapplication.chattingclient.Utils.HttpResponse;
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
    private CheckBox remember;
    private View main_content;

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
        remember = view.findViewById(R.id.cbRememberMeLogin);

//        LINH: animation when open
        main_content = (View) view.findViewById(R.id.main_content);
        main_content.setVisibility(View.INVISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                main_content.setVisibility(View.VISIBLE);
                TranslateAnimation animate = new TranslateAnimation(
                        0,
                        0,
                        main_content.getHeight(),
                        0
                );
                animate.setDuration(500);
                animate.setFillAfter(true);
                main_content.startAnimation(animate);
            }
        }, 1000);

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
                EditText editTextEmail = (EditText) view.findViewById(R.id.editTxtEmailLogin);
                EditText editTextPassword = (EditText) view.findViewById(R.id.editTxtPasswordLogin);
                String jsonString;
                try {
                    jsonString = new JSONObject()
                            .put("email", editTextEmail.getText())
                            .put("password", editTextPassword.getText())
                            .toString();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                PostRequestTask postRequestTask = new PostRequestTask(new HttpResponse(authenticationActivity));
                postRequestTask.execute("account/signin", jsonString, "loginResponse");
            }
        });
        return view;
    }
}