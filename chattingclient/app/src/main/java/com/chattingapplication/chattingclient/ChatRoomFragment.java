package com.chattingapplication.chattingclient;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chattingapplication.chattingclient.Adapter.ChatRoomAdapter;
import com.chattingapplication.chattingclient.Adapter.UserAdapter;
import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.Message;
import com.chattingapplication.chattingclient.Model.User;
import com.google.gson.Gson;

import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatRoomFragment extends Fragment {
    private ListView listViewChatRoom;
    private MainActivity mainActivity;
    private ChatRoomAdapter chatRoomAdapter;
    private List<ChatRoom> chatRoomList;

    public ChatRoomFragment() {
        // Required empty public constructor
    }
    public static ChatRoomFragment newInstance() {
        ChatRoomFragment fragment = new ChatRoomFragment();
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
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);
        LoadActivity.currentContext = this.getContext();

        listViewChatRoom = view.findViewById(R.id.listChatRoom);
        chatRoomList = AuthenticationActivity.currentAccount.getUser().getChatRooms();
        chatRoomAdapter = new ChatRoomAdapter(this.getContext(), chatRoomList);
        listViewChatRoom.setAdapter(chatRoomAdapter);

        listViewChatRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Gson gson = new Gson();
                ChatRoom clickedChatRoom = (ChatRoom) listViewChatRoom.getItemAtPosition(position);
                Intent chattingActivity = new Intent(((MainActivity) mainActivity).getApplicationContext(), ChattingActivity.class);
                chattingActivity.putExtra("currentChatRoom", gson.toJson(clickedChatRoom));
                startActivity(chattingActivity);
            }
        });

        return view;
    }

    public void realTimeUiChatRoom(Message message) {
//        C++ swap
        int oldPosition = chatRoomAdapter.getPositionByChatRoom(message.getChatRoom());
        ChatRoom updatedChatRoom = (ChatRoom) listViewChatRoom.getItemAtPosition(oldPosition);
        updatedChatRoom.setLatestMessage(message);
        chatRoomList.remove(oldPosition);
        chatRoomList.add(0, updatedChatRoom);
        chatRoomAdapter.notifyDataSetChanged();
    }
}