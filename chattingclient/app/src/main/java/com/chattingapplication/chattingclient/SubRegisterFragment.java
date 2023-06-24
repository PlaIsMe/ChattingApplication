package com.chattingapplication.chattingclient;

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
import com.chattingapplication.chattingclient.Model.ExceptionError;
import com.chattingapplication.chattingclient.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubRegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubRegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MainActivity mainActivity;

    public SubRegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubRegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubRegisterFragment newInstance(String param1, String param2) {
        SubRegisterFragment fragment = new SubRegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_sub_register, container, false);
        Button btnSubmitSubRegister = (Button) view.findViewById(R.id.btnSubmitSubRegister);

        btnSubmitSubRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTxtFirstName = (EditText) view.findViewById(R.id.editTxtFirstName);
                EditText editTxtLastName = (EditText) view.findViewById(R.id.editTxtLastName);
                EditText editTxtGender = (EditText) view.findViewById(R.id.editTxtGender);

                PutRequestTask putRequestTask = new PutRequestTask((MainActivity) getActivity());
                String path = String.format("user/%s", MainActivity.currentAccount.getUser().getId());

                try {
                    String jsonString = new JSONObject()
                            .put("firstName", editTxtFirstName.getText())
                            .put("lastName", editTxtLastName.getText())
                            .put("gender", editTxtGender.getText())
                            .toString();
                    putRequestTask.execute(path, jsonString, "handleResponseSubRegister", "SubRegisterFragment");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return view;
    }

    public void handleResponseSubRegister(int responseCode) {
        if (responseCode == 200) {
            MainActivity.currentAccount.setUser(MainActivity.gson.fromJson(MainActivity.httpResponse, User.class));
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.swapFragment(R.id.fragmentContainerViewFullContent, mainActivity.getMainMenuFragment());
                }
            });
        } else {
            ExceptionError exceptionError = MainActivity.gson.fromJson(MainActivity.httpResponse, ExceptionError.class);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainActivity.getApplicationContext(), exceptionError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}