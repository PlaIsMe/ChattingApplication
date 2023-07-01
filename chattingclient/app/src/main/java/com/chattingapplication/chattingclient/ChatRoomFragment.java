package com.chattingapplication.chattingclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.chattingapplication.chattingclient.Adapter.ChatRoomAdapter;
import com.chattingapplication.chattingclient.Adapter.UserAdapter;
import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.Model.ChatRoom;

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
        listViewChatRoom = view.findViewById(R.id.listChatRoom);

        List<ChatRoom> chatRoomList = AuthenticationActivity.currentAccount.getUser().getChatRooms();
        ChatRoomAdapter chatRoomAdapter = new ChatRoomAdapter(this.getContext(), chatRoomList);
        listViewChatRoom.setAdapter(chatRoomAdapter);

        return view;
    }
}