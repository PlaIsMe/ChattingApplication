package com.chattingapplication.chattingclient;

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
import com.chattingapplication.chattingclient.Model.User;
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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
//    private LinearLayout linearLayoutUserContainer;

    private ListView listViewPeople;
    private MainActivity mainActivity;

    public PeopleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PeopleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PeopleFragment newInstance(String param1, String param2) {
        PeopleFragment fragment = new PeopleFragment();
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
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        listViewPeople = view.findViewById(R.id.listViewPeople);
        GetRequestTask getRequestTask = new GetRequestTask((MainActivity) getActivity());
        getRequestTask.execute("user", "loadUser", "PeopleFragment");
        return view;
    }

    public void loadUser(int responseCode, String jsonString) {
        Log.d("debugLoadUser", "loading user");
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            Type userListType = new TypeToken<List<User>>() {}.getType();
            List<User> rawUserList = MainActivity.gson.fromJson(jsonArray.toString(), userListType);
            List<User> userList = rawUserList.stream().filter(user ->
                    (user.getLastName() != null
                            && user.getFirstName() != null
                    && !Objects.equals(user.getId(), MainActivity.currentAccount.getUser().getId())))
                    .collect(Collectors.toList());

            UserAdapter userAdapter = new UserAdapter(this.getContext(), userList);
            listViewPeople.setAdapter(userAdapter);
            listViewPeople.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    User clickedUser = (User) listViewPeople.getItemAtPosition(position);
                    mainActivity.setChattingFragment(new ChattingFragment(clickedUser));
                    mainActivity.swapFragment(R.id.fragmentContainerViewFullContent, mainActivity.getChattingFragment());
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
