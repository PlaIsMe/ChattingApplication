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
import android.widget.TextView;


import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.SendTask;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
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
                    try {
                        jsonString = new JSONObject()
                                .put("createUser", AuthenticationActivity.currentAccount.getUser().toJsonString())
                                .put("targetUser", chattingActivity.getTargetUser().toJsonString())
                                .put("message", editTxtMessage.getText())
                                .toString();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    SendTask sendTask = new SendTask();
                    sendTask.execute("createPrivateRoomRequest", jsonString);
                    appendMyMsg(editTxtMessage.getText().toString());
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
        appendMyMsg(editTxtMessage.getText().toString());
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

    public void appendOtherMsg(Message message) {
        try {
            LinearLayout linearLayout = chattingActivity.findViewById(R.id.layoutReceive);
            TextView otherMsg = new TextView(this.getContext());
            otherMsg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            otherMsg.setText(message.getContent());
            otherMsg.setBackgroundColor(Color.parseColor("#808080"));
            otherMsg.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayout.addView(otherMsg);
        } catch (NullPointerException e) {
            NotificationService.sendNotification(this.getContext(), message);
        }
    }

    public void appendMyMsg(String message) {
        editTxtMessage.setText("");
        LinearLayout linearLayout = chattingActivity.findViewById(R.id.layoutReceive);
        TextView myMsg = new TextView(this.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        layoutParams.setMargins(10, 10, 10, 10); // (left, top, right, bottom)
        myMsg.setLayoutParams(layoutParams);
        myMsg.setText(message);
        myMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        linearLayout.addView(myMsg);
    }

    public void loadMessage(int responseCode, String jsonString) throws JSONException {
        Gson gson = new Gson();
        JSONArray jsonArray = new JSONArray(jsonString);
        Type userListType = new TypeToken<List<Message>>() {}.getType();
        List<Message> listMessages = gson.fromJson(jsonArray.toString(), userListType);
        listMessages.stream().forEach(message -> {
            if (message.getUser().getId().equals(AuthenticationActivity.currentAccount.getUser().getId())) {
                appendMyMsg(message.getContent());
            } else {
                appendOtherMsg(message);
            }
        });
        Log.d("debugListMessage", String.valueOf(listMessages.size()));
    }
}