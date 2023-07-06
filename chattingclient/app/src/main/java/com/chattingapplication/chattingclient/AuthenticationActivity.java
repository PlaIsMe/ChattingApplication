package com.chattingapplication.chattingclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chattingapplication.chattingclient.AsyncTask.GetRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.PostRequestTask;
import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Model.ChatRoom;
import com.chattingapplication.chattingclient.Model.User;
import com.chattingapplication.chattingclient.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private Fragment loginFragment;
    private Fragment registerFragment = new RegisterFragment();
    private Fragment subRegisterFragment = new SubRegisterFragment();
    public Fragment getLoginFragment() {
        return loginFragment;
    }

    public Fragment getRegisterFragment() {
        return registerFragment;
    }

    public Fragment getSubRegisterFragment() {
        return subRegisterFragment;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        fragmentManager = getSupportFragmentManager();
        loginFragment = fragmentManager.findFragmentById(R.id.fragmentContainerAuthentication);
        Intent prevIntent = getIntent();
        try {
            if (prevIntent.getStringExtra("navigate").equals("true")) {
                swapFragment(R.id.fragmentContainerAuthentication, subRegisterFragment);
            }
        } catch (NullPointerException e) {}
    }

    public void swapFragment(int fragmentContainerId, Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(fragmentContainerId, fragment, null)
                .setReorderingAllowed(true)
                .addToBackStack("name")
                .commit();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}