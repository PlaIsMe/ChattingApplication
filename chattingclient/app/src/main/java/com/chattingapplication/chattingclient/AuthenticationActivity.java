package com.chattingapplication.chattingclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Utils.Utils;

public class AuthenticationActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private Fragment loginFragment;
    private Fragment registerFragment = new RegisterFragment();
    private Fragment subRegisterFragment = new SubRegisterFragment();
    public static Account currentAccount;
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