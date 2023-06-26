package com.chattingapplication.chattingclient;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.chattingapplication.chattingclient.AsyncTask.ConnectTask;
import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Model.Response;
import com.chattingapplication.chattingclient.Utils.Utils;
import com.google.gson.Gson;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    public static String IP;
    public static String apiUrl;
    public static Account currentAccount;
    public static Gson gson = new Gson();

    private FragmentManager fragmentManager;
    private Fragment loginFragment;
    private Fragment registerFragment = new RegisterFragment();
    private Fragment subRegisterFragment = new SubRegisterFragment();
    private Fragment mainMenuFragment = new MainMenuFragment();
    private Fragment chattingFragment;
    public void setChattingFragment(Fragment chattingFragment) {
        this.chattingFragment = chattingFragment;
    }

    public Fragment getLoginFragment() {
        return loginFragment;
    }

    public Fragment getRegisterFragment() {
        return registerFragment;
    }

    public Fragment getSubRegisterFragment() {
        return subRegisterFragment;
    }

    public Fragment getMainMenuFragment() {
        return mainMenuFragment;
    }
    public Fragment getChattingFragment() {
        return chattingFragment;
    }

    public Fragment getPeopleFragment() {
        return ((MainMenuFragment) mainMenuFragment).getPeopleFragment();
    }

    public Fragment getChatRoomFragment() {
        return ((MainMenuFragment) mainMenuFragment).getChatRoomFragment();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        loginFragment = fragmentManager.findFragmentById(R.id.fragmentContainerViewFullContent);
        Log.d("RESULT_IPV4", Utils.getIpV4(this, "my_ipv4.json"));
        IP = Utils.getIpV4(this, "my_ipv4.json");
        ConnectTask connectTask = new ConnectTask(this);
        connectTask.execute();
        apiUrl = String.format("http://%s:8080/api/", IP);
    }

    //    Switch fragment
    public void swapFragment(int fragmentContainerId, Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(fragmentContainerId, fragment, null)
                .setReorderingAllowed(true)
                .addToBackStack("name")
                .commit();
    }

    //    Tách jsonString response từ server
    public void handleResponse(String jsonResponse) {
        Response response = gson.fromJson(jsonResponse, Response.class);
        try {
            Class<?> mainClass = Class.forName(getPackageName() + ".MainActivity");
            Method getMethod = mainClass.getDeclaredMethod(String.format("get%s", response.getResponseClass()));
            Object getResult = getMethod.invoke(this);

            Class<?> fragmentClass = Class.forName(String.format("%s.%s", getPackageName(), response.getResponseClass()));
            Method responseMethod = fragmentClass.getDeclaredMethod(response.getResponseFunction(), String.class);
            responseMethod.invoke((Fragment) getResult, response.getResponseParam());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    //    Xử lý giao diện tin nhắn (FIX SAU)
//    public void appendOtherMsg(String message) {
//        try {
//            LinearLayout linearLayout = findViewById(R.id.layoutReceive);
//            TextView otherMsg = new TextView(this);
//            otherMsg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT));
//            otherMsg.setText(message);
//            otherMsg.setBackgroundColor(Color.parseColor("#808080"));
//            otherMsg.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
//            linearLayout.addView(otherMsg);
//        } catch (NullPointerException e) {
//
//        }
//    }
//
//    //    Gửi message
//    public void sendMessage(View view) {
//        EditText editTxtMessage = findViewById(R.id.editTxtMessage);
//        String message = editTxtMessage.getText().toString();
//        editTxtMessage.setText("");
//        SendTask sendTask = new SendTask();
//        sendTask.execute("chattingRequest", message);
//        LinearLayout linearLayout = findViewById(R.id.layoutReceive);
//        TextView myMsg = new TextView(this);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        layoutParams.gravity = Gravity.RIGHT;
//        layoutParams.setMargins(10, 10, 10, 10); // (left, top, right, bottom)
//        myMsg.setLayoutParams(layoutParams);
//        myMsg.setText(message);
//        myMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//        linearLayout.addView(myMsg);
//    }
//
//    public void chattingResponse(String message) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                appendOtherMsg(message);
//            }
//        });
//    }
}