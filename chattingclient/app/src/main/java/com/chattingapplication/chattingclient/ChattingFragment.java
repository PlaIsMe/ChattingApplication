package com.chattingapplication.chattingclient;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.chattingapplication.chattingclient.Adapter.MessageAdapter;
import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.SendTask;
import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.Message;
import com.chattingapplication.chattingclient.Model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.chattingapplication.chattingclient.Service.NotificationService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChattingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChattingFragment extends Fragment {
    private ChattingActivity chattingActivity;
    private EditText editTxtMessage;
    private ListView listViewMessage;
    private List<Message> listMessages = new ArrayList<>();
    private MessageAdapter adapter;

    public ChattingFragment() {}
    public static ChattingFragment newInstance() {
        ChattingFragment fragment = new ChattingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chattingActivity = (ChattingActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);
        listViewMessage = (ListView) view.findViewById(R.id.listViewMessage);
        editTxtMessage = view.findViewById(R.id.editTxtMessage);

        EditText editTxtMessage = view.findViewById(R.id.editTxtMessage);
        FrameLayout layoutSend = view.findViewById(R.id.layoutSend);

        editTxtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layoutSend.setVisibility(editTxtMessage.getText().toString().trim().length() == 0 ? View.INVISIBLE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        layoutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chattingActivity.isRoomAvailable()) {
                    String jsonString;
                    Gson gson = new Gson();
                    User currentUser = AuthenticationActivity.currentAccount.getUser();
                    currentUser.setChatRooms(null);
                    User targetUser = chattingActivity.getTargetUser();
                    targetUser.setChatRooms(null);
                    try {
                        jsonString = new JSONObject()
                                .put("createUser", gson.toJson(currentUser))
                                .put("targetUser", gson.toJson(targetUser))
                                .put("message", editTxtMessage.getText())
                                .toString();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    SendTask sendTask = new SendTask();
                    sendTask.execute("createPrivateRoomRequest", jsonString);
                    adapter = new MessageAdapter(MainActivity.mainContext, listMessages);
                    listViewMessage.setAdapter(adapter);
                    appendMessage(new Message(editTxtMessage.getText().toString(), AuthenticationActivity.currentAccount.getUser()));
                    editTxtMessage.setText("");
                } else {
                    sendMessage();
                }
            }
        });
        return view;
    }

    public void sendMessage() {
        String messageJson;
        try {
            messageJson = new JSONObject()
                    .put("content", editTxtMessage.getText().toString())
                    .put("roomId", chattingActivity.getCurrentChatRoom().getId())
                    .put("userId", AuthenticationActivity.currentAccount.getUser().getId())
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        SendTask sendTask = new SendTask();
        sendTask.execute("chattingRequest", messageJson);
        appendMessage(new Message(editTxtMessage.getText().toString(), AuthenticationActivity.currentAccount.getUser()));
    }

    public void joinPrivateRoom(int responseCode, String jsonString) {
        Gson gson = new Gson();
        if (responseCode == 200) {
            chattingActivity.setCurrentChatRoom(gson.fromJson(jsonString, ChatRoom.class));
            chattingActivity.setRoomAvailable(true);
            GetRequestTask getRequestTask = new GetRequestTask(chattingActivity);
            getRequestTask.execute(String.format("message/%s", chattingActivity.getCurrentChatRoom().getId()), "loadMessage", "ChattingFragment", "ChattingActivity");
        } else if (responseCode == 500) {
            chattingActivity.setRoomAvailable(false);
        }
    }

    public void appendMessage(Message message) {
        listMessages.add(message);
        adapter.notifyDataSetChanged();
    }

    public void loadMessage(int responseCode, String jsonString) throws JSONException {
        Gson gson = new Gson();
        JSONArray jsonArray = new JSONArray(jsonString);
        Type userListType = new TypeToken<List<Message>>() {}.getType();
        listMessages = gson.fromJson(jsonArray.toString(), userListType);
        adapter = new MessageAdapter(this.getContext(), listMessages);
        listViewMessage.setAdapter(adapter);
        Log.d("debugListMessage", String.valueOf(listMessages.size()));
    }
}