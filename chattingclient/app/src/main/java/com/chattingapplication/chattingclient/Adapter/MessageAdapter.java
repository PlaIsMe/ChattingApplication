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
import com.chattingapplication.chattingclient.ChattingActivity;
import com.chattingapplication.chattingclient.LoadActivity;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.Message;
import com.chattingapplication.chattingclient.Model.User;
import com.chattingapplication.chattingclient.R;

import java.util.List;

public class MessageAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Message> messages;

    private User targetUser;

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public MessageAdapter(Context chattingActivity, Context context, List<Message> messages){
        this.messages = messages;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.targetUser = ((ChattingActivity) chattingActivity).getTargetUser();
    }

    @Override
    public int getCount() {
        return this.messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == 0) {
            convertView = inflater.inflate(R.layout.my_message, null);
        } else {
            convertView = inflater.inflate(R.layout.orther_message, null);
        }
        Message currentMessage = (Message) getItem(position);
        TextView txtContent = (TextView) convertView.findViewById(R.id.txtMessageContent);
        ImageView img = (ImageView) convertView.findViewById(R.id.imgUserAvatar);
        txtContent.setText(currentMessage.getContent());
//        img.setImageResource(currentMessage.getUser().getAvatar());
        if(img != null && targetUser != null){
            if(targetUser.getDownloadAvatar() == null){
                if(targetUser.getAvatar() == null || targetUser.getAvatar().isEmpty()){
                    targetUser.setAvatar(context.getResources().getString(R.string.default_avatar_url));
                }
                new DownloadFromURLTask(targetUser, img).execute(targetUser.getAvatar());
            } else {
                img.setImageBitmap(targetUser.getDownloadAvatar());
            }
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return (message.getUser().getId().equals(LoadActivity.currentAccount.getUser().getId()) ? 0 : 1);
    }
}
