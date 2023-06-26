package com.chattingapplication.chattingclient;

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

import com.chattingapplication.chattingclient.AsyncTask.SendTask;
import com.chattingapplication.chattingclient.Model.Account;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MainActivity mainActivity;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mainActivity = (MainActivity) getActivity();
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
                mainActivity.swapFragment(R.id.fragmentContainerViewFullContent,
                        mainActivity.getRegisterFragment());
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
                SendTask sendTask = new SendTask();
                sendTask.execute("loginRequest", jsonString);
            }
        });

        return view;
    }

    public void loginResponse(String jsonString) {
        try {
            MainActivity.currentAccount = MainActivity.gson.fromJson(jsonString, Account.class);
            try {
                String checkName = MainActivity.currentAccount.getUser().getFirstName();
                Log.d("debugCheckName", checkName);
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.swapFragment(R.id.fragmentContainerViewFullContent, mainActivity.getMainMenuFragment());
                    }
                });
            } catch (NullPointerException e) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.swapFragment(R.id.fragmentContainerViewFullContent, mainActivity.getSubRegisterFragment());
                    }
                });
            }
        } catch (JsonSyntaxException e) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainActivity.getApplicationContext(), jsonString, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}