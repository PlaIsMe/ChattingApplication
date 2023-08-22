package com.chattingapplication.chattingclient.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattingapplication.chattingclient.AsyncTask.DownloadFromURLTask;
import com.chattingapplication.chattingclient.LoadActivity;
import com.chattingapplication.chattingclient.Model.User;
import com.chattingapplication.chattingclient.R;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<User> user;

    public UserAdapter(Context context, List<User> user){
        this.user = user;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.user.size();
    }

    @Override
    public Object getItem(int position) {
        return user.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.user_list_view, null);
        User currentUser = (User) getItem(position);
        ImageView avatar = convertView.findViewById(R.id.userAvatar);
        TextView userName = convertView.findViewById(R.id.userName);
        userName.setText(String.format("%s %s", currentUser.getLastName(), currentUser.getFirstName()));
        // if the current user avatar is null will get the deafult avatar instead
        // otherwise using user avatar
        if(currentUser.getDownloadAvatar() == null){
            if(currentUser.getAvatar() == null || currentUser.getAvatar().isEmpty()){
                currentUser.setAvatar(context.getResources().getString(R.string.default_avatar_url));
                Log.d("DefaultURL", context.getResources().getString(R.string.default_avatar_url));
            }
            new DownloadFromURLTask(currentUser, avatar).execute(currentUser.getAvatar());
        }
        else{
            avatar.setImageBitmap(currentUser.getDownloadAvatar());
        }

//        if (LoadActivity.idList.contains(currentUser.getId())) {
//            ImageView status = convertView.findViewById(R.id.activeStatus);
//            status.setImageResource(R.drawable.online);
//        }

        return convertView;
    }

}