package com.chattingapplication.chattingclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.PutRequestTask;
import com.chattingapplication.chattingclient.Model.User;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

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
    private LinearLayout linearLayoutUserContainer;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        linearLayoutUserContainer = view.findViewById(R.id.linearLayoutUserContainer);
        GetRequestTask getRequestTask = new GetRequestTask((MainActivity) getActivity());
        getRequestTask.execute("user", "loadUser", "PeopleFragment");
        return view;
    }

    public void loadUser(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            Type userListType = new TypeToken<List<User>>() {}.getType();
            List<User> userList = MainActivity.gson.fromJson(jsonArray.toString(), userListType);

            for (User user : userList) {
                if (user.getFirstName() != null && user.getLastName() != null) {
//                    Log.d("debugUser", user.getFirstName());
                    ((MainActivity) getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            appendUser(user);
                        }
                    });
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendUser(User user) {
        LinearLayout linearLayout = new LinearLayout(this.getContext());
        linearLayout.setTag(String.format("user_%d", user.getId()));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100));
        linearLayout.setPadding(10,10,10,10);

        ImageView imageView = new ImageView(this.getContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.MATCH_PARENT));
        imageView.setImageResource(R.drawable.default_avatar);

        TextView textView = new TextView(this.getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        textView.setLayoutParams(params);
        textView.setPadding(10, 0, 0, 0);
        textView.setText(String.format("%s %s", user.getLastName(), user.getFirstName()));

        linearLayout.addView(imageView);
        linearLayout.addView(textView);
        linearLayoutUserContainer.addView(linearLayout);
    }
}
