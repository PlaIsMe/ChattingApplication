package com.chattingapplication.chattingclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattingapplication.chattingclient.Model.User;
import com.chattingapplication.chattingclient.R;

import java.util.List;

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
        return convertView;
    }
}