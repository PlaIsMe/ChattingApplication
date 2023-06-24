package com.chattingapplication.chattingclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chattingapplication.chattingclient.R;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<String> usersName;
//    private List<Integer> images;

    public UserAdapter(Context context, List<String> userName){
        this.usersName = userName;
//        this.images = images;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.usersName.size();
    }

    @Override
    public Object getItem(int position) {
        return usersName.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.user_list_view, null);
        ImageView avatar = convertView.findViewById(R.id.userAvatar);
        TextView userName = convertView.findViewById(R.id.userName);
        userName.setText(usersName.get(position));
        return convertView;
    }
}
