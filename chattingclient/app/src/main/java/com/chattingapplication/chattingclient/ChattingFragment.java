package com.chattingapplication.chattingclient;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
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

import androidx.appcompat.widget.Toolbar;

import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.SendTask;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChattingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChattingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private User targetUser;
    private ChatRoom currentChatRoom;
    private MainActivity mainActivity;
    private boolean isRoomAvailable = true;
    private EditText editTxtMessage;

    public ChattingFragment(User targetUser) {
        this.targetUser = targetUser;
    }
    public User getTargetUser() {
        Log.d("debugGetTargetUser", targetUser.toString());
        return targetUser;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChattingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChattingFragment newInstance(String param1, String param2) {
        ChattingFragment fragment = new ChattingFragment(new User());
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
        GetRequestTask getRequestTask = new GetRequestTask((MainActivity) getActivity());
        String path = String.format("chat_room/%s/%s/true", MainActivity.currentAccount.getUser().getId(), targetUser.getId());
        getRequestTask.execute(path, "joinPrivateRoom", "ChattingFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);
        editTxtMessage = view.findViewById(R.id.editTxtMessage);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        TextView txtUserName = view.findViewById(R.id.txtViewUserName);
        txtUserName.setText(String.format("%s %s", targetUser.getLastName(), targetUser.getFirstName()));

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
                if (!isRoomAvailable) {
                    String jsonString;
                    try {
                        jsonString = new JSONObject()
                                .put("createUser", MainActivity.currentAccount.getUser().toJsonString())
                                .put("targetUser", targetUser.toJsonString())
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
                    .put("roomId", currentChatRoom.getId())
                    .put("userId", MainActivity.currentAccount.getUser().getId())
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        SendTask sendTask = new SendTask();
        sendTask.execute("chattingRequest", messageJson);
        appendMyMsg(editTxtMessage.getText().toString());
    }

    public void joinPrivateRoom(int responseCode, String jsonString) {
        if (responseCode == 200) {
            Log.d("debugGetCurrentRoom", jsonString);
            currentChatRoom = MainActivity.gson.fromJson(jsonString, ChatRoom.class);
        } else if (responseCode == 500) {
            isRoomAvailable = false;
        }
    }

    public void appendOtherMsg(String message) {
        try {
            LinearLayout linearLayout = mainActivity.findViewById(R.id.layoutReceive);
            TextView otherMsg = new TextView(this.getContext());
            otherMsg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            otherMsg.setText(message);
            otherMsg.setBackgroundColor(Color.parseColor("#808080"));
            otherMsg.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayout.addView(otherMsg);
        } catch (NullPointerException e) {

        }
    }

    public void appendMyMsg(String message) {
        editTxtMessage.setText("");
        LinearLayout linearLayout = mainActivity.findViewById(R.id.layoutReceive);
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

    public void chattingResponse(String message) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appendOtherMsg(message);
            }
        });
    }

    public void createPrivateRoomResponse(String chatRoom) {
        currentChatRoom = MainActivity.gson.fromJson(chatRoom, ChatRoom.class);
    }
}