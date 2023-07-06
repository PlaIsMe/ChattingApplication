package com.chattingapplication.chattingclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.chattingapplication.chattingclient.Utils.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    private AuthenticationActivity authenticationActivity;
    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        TextView txtInformLogin = (TextView) view.findViewById(R.id.txtInformLogin);
        Button btnRegister = (Button) view.findViewById(R.id.btnRegister);

        txtInformLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticationActivity.swapFragment(R.id.fragmentContainerAuthentication,
                        authenticationActivity.getLoginFragment());
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextEmail = (EditText) view.findViewById(R.id.editTxtEmailRegister);
                EditText editTextPassword = (EditText) view.findViewById(R.id.editTxtPasswordRegister);
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
                postRequestTask.execute("account/signup", jsonString, "registerResponse");
            }
        });

        return view;
    }
}