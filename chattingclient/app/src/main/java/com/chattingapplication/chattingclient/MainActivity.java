package com.chattingapplication.chattingclient;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.chattingapplication.chattingclient.AsyncTask.ConnectTask;
import com.chattingapplication.chattingclient.AsyncTask.PutRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.SendTask;
import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Model.ExceptionError;
import com.chattingapplication.chattingclient.Model.Response;
import com.chattingapplication.chattingclient.Model.User;
import com.chattingapplication.chattingclient.Utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    public static String IP;
    public static String apiUrl;
    public static String httpResponse;
    Account currentAccount;
    Gson gson = new Gson();
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("RESULT_IPV4",Utils.getIpV4(this, "my_ipv4.json"));
        IP = Utils.getIpV4(this, "my_ipv4.json");
        ConnectTask connectTask = new ConnectTask(this);
        connectTask.execute();
        apiUrl = String.format("http://%s:8080/api/", IP);
    }

//    Hàm xử lý response từ server
    public void registerResponse(String jsonString) {
        try {
            currentAccount = gson.fromJson(jsonString, Account.class);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swapFragment(R.id.fragmentContainerViewFullContent, SubRegisterFragment.class);
                }
            });
        } catch (JsonSyntaxException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), jsonString, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void loginResponse(String jsonString) {
        try {
            currentAccount = gson.fromJson(jsonString, Account.class);
            try {
                String checkName = currentAccount.getUser().getFirstName();
                Log.d("debugCheckName", checkName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swapFragment(R.id.fragmentContainerViewFullContent, MainMenuFragment.class);
                    }
                });
            } catch (NullPointerException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swapFragment(R.id.fragmentContainerViewFullContent, SubRegisterFragment.class);
                    }
                });
            }
        } catch (JsonSyntaxException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), jsonString, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void chattingResponse(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                appendOtherMsg(message);
            }
        });
    }
//

//    Tách jsonString response từ server
    public void handleResponse(String jsonResponse) {
        Response response = gson.fromJson(jsonResponse, Response.class);
        try {
            Class<?> c = Class.forName(getPackageName() + ".MainActivity");
            Method method = c.getDeclaredMethod(response.getResponseFunction(), String.class);
            method.invoke(this, response.getResponseParam());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

//    Xử lý giao diện tin nhắn
    public void appendOtherMsg(String message) {
        try {
            LinearLayout linearLayout = findViewById(R.id.layoutReceive);
            TextView otherMsg = new TextView(this);
            otherMsg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            otherMsg.setText(message);
            otherMsg.setBackgroundColor(Color.parseColor("#808080"));
            otherMsg.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
            linearLayout.addView(otherMsg);
        } catch (NullPointerException e) {

        }
    }

//    Xử lý giao diện submit subregister
    public void handleResponseSubRegister(int responseCode) {
        if (responseCode == 200) {
            currentAccount.setUser(gson.fromJson(httpResponse, User.class));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swapFragment(R.id.fragmentContainerViewFullContent, MainMenuFragment.class);
                }
            });
        } else {
            ExceptionError exceptionError = gson.fromJson(httpResponse, ExceptionError.class);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), exceptionError.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

//    Đăng ký khi bấm vào btnRegister
    public void register(View v) {
        EditText editTextEmail = findViewById(R.id.editTxtEmail);
        EditText editTextPassword = findViewById(R.id.editTxtPassword);
        String jsonString;
        try {
            jsonString = new JSONObject()
                    .put("email", editTextEmail.getText())
                    .put("password", editTextPassword.getText())
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        SendTask sendTask = new SendTask();
        sendTask.execute("registerRequest", jsonString);
    }

//    Đăng nhập khi click vào btnLogin
    public void login(View v) {
        EditText editTextEmail = findViewById(R.id.editTxtEmail);
        EditText editTextPassword = findViewById(R.id.editTxtPassword);
        String jsonString;
        try {
            jsonString = new JSONObject()
                    .put("email", editTextEmail.getText())
                    .put("password", editTextPassword.getText())
                    .toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        SendTask sendTask = new SendTask();
        sendTask.execute("loginRequest", jsonString);
    }

//    Gửi message
    public void sendMessage(View view) {
        EditText editTxtMessage = findViewById(R.id.editTxtMessage);
        String message = editTxtMessage.getText().toString();
        editTxtMessage.setText("");
        SendTask sendTask = new SendTask();
        sendTask.execute("chattingRequest", message);
        LinearLayout linearLayout = findViewById(R.id.layoutReceive);
        TextView myMsg = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.RIGHT;
        layoutParams.setMargins(10, 10, 10, 10); // (left, top, right, bottom)
        myMsg.setLayoutParams(layoutParams);
        myMsg.setText(message);
        myMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        linearLayout.addView(myMsg);
    }

//    Xử lý khi bấm vào nút submit trong giao diện subregister
    public void subRegisterSubmit(View v) {
        EditText editTxtFirstName = findViewById(R.id.editTxtFirstName);
        EditText editTxtLastName = findViewById(R.id.editTxtLastName);
        EditText editTxtGender = findViewById(R.id.editTxtGender);

        PutRequestTask putRequestTask = new PutRequestTask(this);
        String path = String.format("user/%s", currentAccount.getUser().getId());

        try {
            String jsonString = new JSONObject()
                    .put("firstName", editTxtFirstName.getText())
                    .put("lastName", editTxtLastName.getText())
                    .put("gender", editTxtGender.getText())
                    .toString();
            putRequestTask.execute(path, jsonString, "handleResponseSubRegister");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

//    Switch fragment
    public void swapFragment(int fragmentContainerId, Class targetFragment) {
        fragmentManager.beginTransaction()
                .replace(fragmentContainerId, targetFragment, null)
                .setReorderingAllowed(true)
                .addToBackStack("name")
                .commit();
    }

//    Hàm handle switch fragment
    public void handleSwapFragment(View v) {
        if (v.getId() == R.id.txtInformRegister) {
            swapFragment(R.id.fragmentContainerViewFullContent, RegisterFragment.class);
        } else if (v.getId() == R.id.txtInformLogin) {
            swapFragment(R.id.fragmentContainerViewFullContent, LoginFragment.class);
        } else if (v.getId() == R.id.linearLayoutChats) {
            swapFragment(R.id.fragmentContainerViewMainMenu, ChatRoomFragment.class);
        } else if (v.getId() == R.id.linearLayoutPeople) {
            swapFragment(R.id.fragmentContainerViewMainMenu, PeopleFragment.class);
        }
    }

//    Read response body
    public void readResponseBody(BufferedReader in) throws IOException {
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        httpResponse = response.toString();
    }
}