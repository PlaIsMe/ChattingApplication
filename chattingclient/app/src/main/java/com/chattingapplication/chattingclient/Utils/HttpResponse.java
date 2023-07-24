package com.chattingapplication.chattingclient.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.chattingapplication.chattingclient.Adapter.MessageAdapter;
import com.chattingapplication.chattingclient.Adapter.UserAdapter;
import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.SendTask;
import com.chattingapplication.chattingclient.AuthenticationActivity;
import com.chattingapplication.chattingclient.ChattingActivity;
import com.chattingapplication.chattingclient.ChattingFragment;
import com.chattingapplication.chattingclient.LoadActivity;
import com.chattingapplication.chattingclient.MainActivity;
import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.ExceptionError;
import com.chattingapplication.chattingclient.Model.Message;
import com.chattingapplication.chattingclient.Model.User;
import com.chattingapplication.chattingclient.PeopleFragment;
import com.chattingapplication.chattingclient.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpResponse {
    private Activity activity;
    public HttpResponse(Activity activity) {
        this.activity = activity;
    }

    public void loginResponse(int responseCode, String jsonResponse) {
        Gson gson = new Gson();
        CheckBox remember = activity.findViewById(R.id.cbRememberMeLogin);
        if (responseCode == 200) {
            LoadActivity.currentAccount = gson.fromJson(jsonResponse, Account.class);
            try {
                if (remember.isChecked()) {
                    Log.d("debugRemember", "true");
                    SharedPreferences.Editor editorCheck = LoadActivity.preferencesCheck.edit();
                    editorCheck.putString("check", "true");
                    editorCheck.apply();

                    SharedPreferences.Editor editorAccount = LoadActivity.preferencesAccount.edit();
                    editorAccount.putString("account", jsonResponse);
                    editorAccount.apply();
                }
                String checkName = LoadActivity.currentAccount.getUser().getFirstName();
                Log.d("debugCheckName", checkName);
                SendTask sendTask = new SendTask();
                sendTask.execute("updateRequest", String.format("%s", gson.toJson(LoadActivity.currentAccount)));
            } catch (NullPointerException e) {
                ((AuthenticationActivity) activity).swapFragment(R.id.fragmentContainerAuthentication, ((AuthenticationActivity) activity).getSubRegisterFragment());
            }
        } else {
            ExceptionError exceptionError = gson.fromJson(jsonResponse, ExceptionError.class);
            Toast.makeText(((AuthenticationActivity) activity).getApplicationContext(), exceptionError.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void registerResponse(int responseCode, String jsonResponse) {
        Gson gson = new Gson();
        CheckBox remember = activity.findViewById(R.id.cbRememberMeRegister);
        if (responseCode == 200) {
            if (remember.isChecked()) {
                SharedPreferences.Editor editorCheck = LoadActivity.preferencesCheck.edit();
                editorCheck.putString("check", "true");
                editorCheck.apply();

                SharedPreferences.Editor editorAccount = LoadActivity.preferencesAccount.edit();
                editorAccount.putString("account", jsonResponse);
                editorAccount.apply();
            }
            LoadActivity.currentAccount = gson.fromJson(jsonResponse, Account.class);
            ((AuthenticationActivity) activity).swapFragment(R.id.fragmentContainerAuthentication, ((AuthenticationActivity) activity).getSubRegisterFragment());
        } else {
            ExceptionError exceptionError = gson.fromJson(jsonResponse, ExceptionError.class);
            Toast.makeText(((AuthenticationActivity) activity).getApplicationContext(), exceptionError.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void handleResponseSubRegister(int responseCode, String responeBody) {
        Gson gson = new Gson();
        if (responseCode == 200) {
            String loginSuccess = LoadActivity.preferencesCheck.getString("check", "false");
            User newUser = gson.fromJson(responeBody, User.class);
            newUser.setChatRooms(new ArrayList<>());
            LoadActivity.currentAccount.setUser(newUser);
            if (loginSuccess.equals("true")) {
                SharedPreferences.Editor editorAccount = LoadActivity.preferencesAccount.edit();
                editorAccount.putString("account", gson.toJson(LoadActivity.currentAccount));
                editorAccount.apply();
            }
            SendTask sendTask = new SendTask();
            sendTask.execute("updateRequest", String.format("%s", gson.toJson(LoadActivity.currentAccount)));
        } else {
            ExceptionError exceptionError = gson.fromJson(responeBody, ExceptionError.class);
            Toast.makeText(((AuthenticationActivity) activity).getApplicationContext(), exceptionError.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void loadUser(int responseCode, String jsonString) {
        Gson gson = new Gson();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            Type userListType = new TypeToken<List<User>>() {}.getType();
            List<User> rawUserList = gson.fromJson(jsonArray.toString(), userListType);
            List<User> userList = rawUserList.stream().filter(user ->
                            (user.getLastName() != null
                                    && user.getFirstName() != null
                                    && !Objects.equals(user.getId(), LoadActivity.currentAccount.getUser().getId())))
                    .collect(Collectors.toList());
            MainActivity.listPeople = userList;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void joinPrivateRoom(int responseCode, String jsonString) {
        Gson gson = new Gson();
        if (responseCode == 200) {
            Log.d("debugJoinPrivateRoom", "200");
            ((ChattingActivity) activity).setCurrentChatRoom(gson.fromJson(jsonString, ChatRoom.class));
            ((ChattingActivity) activity).setRoomAvailable(true);
            GetRequestTask getRequestTask = new GetRequestTask(new HttpResponse(activity));
            getRequestTask.execute(String.format("message/%s", ((ChattingActivity) activity).getCurrentChatRoom().getId()), "loadMessage", "ChattingFragment", "ChattingActivity");
        } else if (responseCode == 500) {
            Log.d("debugJoinPrivateRoom", "500");
            ((ChattingActivity) activity).setRoomAvailable(false);
        }
        ((ChattingActivity) activity).init();
    }

    public void loadMessage(int responseCode, String jsonString) throws JSONException {
        Gson gson = new Gson();
        JSONArray jsonArray = new JSONArray(jsonString);
        Type userListType = new TypeToken<List<Message>>() {}.getType();
        ((ChattingFragment) (((ChattingActivity) activity).getChattingFragment())).setListMessages(gson.fromJson(jsonArray.toString(), userListType));
        ((ChattingFragment) (((ChattingActivity) activity).getChattingFragment())).setAdapter(new MessageAdapter((ChattingActivity) activity, activity.getApplicationContext(), ((ChattingFragment) (((ChattingActivity) activity).getChattingFragment())).getListMessages()));
        ((ChattingFragment) (((ChattingActivity) activity).getChattingFragment())).getListViewMessage().setAdapter(((ChattingFragment) (((ChattingActivity) activity).getChattingFragment())).getAdapter());
    }
}
