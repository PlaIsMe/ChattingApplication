package com.chattingapplication.chattingclient;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.PutRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.SendTask;
import com.chattingapplication.chattingclient.Model.ExceptionError;
import com.chattingapplication.chattingclient.Model.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubRegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubRegisterFragment extends Fragment {
    private AuthenticationActivity authenticationActivity;
    public SubRegisterFragment() {
        // Required empty public constructor
    }

    public static SubRegisterFragment newInstance() {
        SubRegisterFragment fragment = new SubRegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_sub_register, container, false);
        Button btnSubmitSubRegister = (Button) view.findViewById(R.id.btnSubmitSubRegister);

        btnSubmitSubRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTxtFirstName = (EditText) view.findViewById(R.id.editTxtFirstName);
                EditText editTxtLastName = (EditText) view.findViewById(R.id.editTxtLastName);
                EditText editTxtGender = (EditText) view.findViewById(R.id.editTxtGender);

                PutRequestTask putRequestTask = new PutRequestTask(authenticationActivity);
                String path = String.format("user/%s", AuthenticationActivity.currentAccount.getUser().getId());

                try {
                    String jsonString = new JSONObject()
                            .put("firstName", editTxtFirstName.getText())
                            .put("lastName", editTxtLastName.getText())
                            .put("gender", editTxtGender.getText())
                            .toString();
                    putRequestTask.execute(path, jsonString, "handleResponseSubRegister", "SubRegisterFragment", "AuthenticationActivity");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return view;
    }

    public void handleResponseSubRegister(int responseCode, String responeBody) {
        Gson gson = new Gson();
        if (responseCode == 200) {
            User newUser = gson.fromJson(responeBody, User.class);
            newUser.setChatRooms(new ArrayList<>());
            AuthenticationActivity.currentAccount.setUser(newUser);
            Intent mainActivity = new Intent(this.getContext(), MainActivity.class);
            startActivity(mainActivity);
        } else {
            ExceptionError exceptionError = gson.fromJson(responeBody, ExceptionError.class);
            Toast.makeText(authenticationActivity.getApplicationContext(), exceptionError.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}