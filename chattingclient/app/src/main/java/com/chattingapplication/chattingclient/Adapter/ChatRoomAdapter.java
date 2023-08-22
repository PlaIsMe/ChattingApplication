package com.chattingapplication.chattingclient.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattingapplication.chattingclient.AsyncTask.DownloadFromURLTask;
import com.chattingapplication.chattingclient.AuthenticationActivity;
import com.chattingapplication.chattingclient.LoadActivity;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.User;
import com.chattingapplication.chattingclient.R;

import java.util.List;

public class ChatRoomAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<ChatRoom> chatRooms;

    public ChatRoomAdapter(Context context, List<ChatRoom> chatRooms){
        this.chatRooms = chatRooms;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.chatRooms.size();
    }

    @Override
    public Object getItem(int position) {
        return chatRooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.chatroom_list_view, null);
        ChatRoom currentChatRoom = (ChatRoom) getItem(position);
        ImageView avatar = convertView.findViewById(R.id.userAvatar);
        TextView userName = convertView.findViewById(R.id.userName);
        TextView message = convertView.findViewById(R.id.txtMessage);
        userName.setText(String.format("%s %s", currentChatRoom.getTargetUser().getLastName(), currentChatRoom.getTargetUser().getFirstName()));
        message.setText(currentChatRoom.getLatestMessage().getUser().getId().equals(LoadActivity.currentAccount.getUser().getId()) ?
                String.format("%s: %s", "You", currentChatRoom.getLatestMessage().getContent()) :
                String.format("%s: %s", currentChatRoom.getTargetUser().getFirstName(), currentChatRoom.getLatestMessage().getContent()));
        if(currentChatRoom.getTargetUser().getDownloadAvatar() == null){
            if(currentChatRoom.getTargetUser().getAvatar() == null ||
                    currentChatRoom.getTargetUser().getAvatar().isEmpty()){
                currentChatRoom.getTargetUser().setAvatar(context.getResources().
                                getString(R.string.default_avatar_url)
                );
                Log.d("DefaultURL", context.getResources()
                        .getString(R.string.default_avatar_url));
            }
            new DownloadFromURLTask(currentChatRoom.getTargetUser(), avatar)
                    .execute(currentChatRoom.getTargetUser().getAvatar());
        }
        else{
            avatar.setImageBitmap(currentChatRoom.getTargetUser().getDownloadAvatar());
        }

//        if (LoadActivity.idList.contains(currentChatRoom.getTargetUser().getId())) {
//            ImageView status = convertView.findViewById(R.id.activeStatus);
//            status.setImageResource(R.drawable.online);
//        }
        return convertView;
    }

    public int getPositionByChatRoom(ChatRoom chatRoom) {
        for (int i = 0; i < getCount(); i++) {
            ChatRoom currentChatRoom = (ChatRoom) getItem(i);
            if (currentChatRoom.getId().equals(chatRoom.getId())) {
                return i;
            }
        }
        return -1; // Return -1 if the attribute is not found
    }
}
