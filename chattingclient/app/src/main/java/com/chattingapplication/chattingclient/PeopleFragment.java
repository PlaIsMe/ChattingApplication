package com.chattingapplication.chattingclient;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chattingapplication.chattingclient.Adapter.UserAdapter;
import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.PutRequestTask;
import com.chattingapplication.chattingclient.Model.Message;
import com.chattingapplication.chattingclient.Model.User;
import com.chattingapplication.chattingclient.Utils.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PeopleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeopleFragment extends Fragment {
    private ListView listViewPeople;
    private MainActivity mainActivity;

    public PeopleFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PeopleFragment newInstance() {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        listViewPeople = view.findViewById(R.id.listViewPeople);
        UserAdapter userAdapter = new UserAdapter(mainActivity.getApplicationContext(), MainActivity.listPeople);
        listViewPeople.setAdapter(userAdapter);
        listViewPeople.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new Gson();
                User clickedUser = (User) listViewPeople.getItemAtPosition(position);
                Intent chattingActivity = new Intent(((MainActivity) mainActivity).getApplicationContext(), ChattingActivity.class);
                chattingActivity.putExtra("targetUser",gson.toJson(clickedUser));
                startActivity(chattingActivity);
            }
        });
        return view;
    }
}
