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


    public void setListViewMessage(ListView listViewMessage) {
        this.listViewMessage = listViewMessage;
    }

    public void setListMessages(List<Message> listMessages) {
        this.listMessages = listMessages;
    }

    public void setAdapter(MessageAdapter adapter) {
        this.adapter = adapter;
    }

    public ListView getListViewMessage() {
        return listViewMessage;
    }

    public List<Message> getListMessages() {
        return listMessages;
    }

    public MessageAdapter getAdapter() {
        return adapter;
    }


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
                    User currentUser = LoadActivity.currentAccount.getUser();
                    User targetUser = chattingActivity.getTargetUser();
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
                    adapter = new MessageAdapter(getActivity(), MainActivity.mainContext, listMessages);
                    listViewMessage.setAdapter(adapter);
                    appendMessage(new Message(editTxtMessage.getText().toString(), LoadActivity.currentAccount.getUser()));
                } else {
                    sendMessage();
                }
                editTxtMessage.setText("");
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
                    .put("userId", LoadActivity.currentAccount.getUser().getId())
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        SendTask sendTask = new SendTask();
        sendTask.execute("chattingRequest", messageJson);
        Message newMessage = new Message(editTxtMessage.getText().toString(), LoadActivity.currentAccount.getUser(), chattingActivity.getCurrentChatRoom());
        appendMessage(newMessage);
        chattingActivity.updateUiChatRoom(newMessage);
    }

    public void appendMessage(Message message) {
        listMessages.add(message);
        adapter.notifyDataSetChanged();
//        ChatRoom updatedChatRoom = chattingActivity.getCurrentChatRoom();
//        updatedChatRoom.setLatestMessage(message);
//        chattingActivity.setCurrentChatRoom(updatedChatRoom);
//        message.setChatRoom(updatedChatRoom);
//        chattingActivity.updateUiChatRoom(message);
    }
}