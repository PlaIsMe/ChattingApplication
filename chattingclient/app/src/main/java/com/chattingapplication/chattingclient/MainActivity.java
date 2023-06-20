package com.chattingapplication.chattingclient;

import android.graphics.Color;
import android.os.AsyncTask;
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

import com.chattingapplication.chattingclient.Model.Account;
import com.chattingapplication.chattingclient.Model.ExceptionError;
import com.chattingapplication.chattingclient.Model.Response;
import com.chattingapplication.chattingclient.Model.User;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String SERVER_IP = "192.168.1.245";
    String apiUrl = String.format("http://%s:8080/api/", SERVER_IP);
    int SERVER_PORT = 8081;
    Socket clientFd;
    static DataOutputStream dOut;
    DataInputStream dIn;
    String socketResponse;
    String httpResponse;
    Account currentAccount;
    boolean isHttpSuccess;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectTask connectTask = new ConnectTask();
        connectTask.execute();
//        HandlePattern("REGISTER|{\"username\":\"test19\",\"email\":\"test19@gmail.com\",\"password\":\"IamPhong\"}");
    }

//    Kết nối socket
    class ConnectTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                clientFd = new Socket(SERVER_IP, SERVER_PORT);
                dOut = new DataOutputStream(clientFd.getOutputStream());
                dIn = new DataInputStream(clientFd.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (clientFd.isConnected()) {
                        try {
                            socketResponse = dIn.readUTF();
                            Log.d("debugReceived", socketResponse);
                            handleResponse(socketResponse);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }).start();
        }
    }

//    Gửi tin nhắn cho server
    public class SendTask extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String request;
            try {
                request = new JSONObject()
                        .put("requestFunction", params[0])
                        .put("requestParam", params[1])
                        .toString();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                Log.d("debugSent", request);
                dOut.writeUTF(request);
                dOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

//    Hàm xử lý response từ server
    public void registerResponse(String jsonString) {
        try {
            currentAccount = gson.fromJson(jsonString, Account.class);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.fragment_sub_register);
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
                        setContentView(R.layout.fragment_chatting);
                    }
                });
            } catch (NullPointerException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setContentView(R.layout.fragment_sub_register);
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

        PutRequestTask pushRequestTask = new PutRequestTask();
        String path = String.format("user/%s", currentAccount.getUser().getId());

        try {
            String jsonString = new JSONObject()
                    .put("firstName", editTxtFirstName.getText())
                    .put("lastName", editTxtLastName.getText())
                    .put("gender", editTxtGender.getText())
                    .toString();
            pushRequestTask.execute(path, jsonString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Log.d("debugIsHttpSuccessMainThread", String.valueOf(isHttpSuccess));
        if (isHttpSuccess) {
            currentAccount.setUser(gson.fromJson(httpResponse, User.class));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.fragment_chatting);
                }
            });
        }
    }

//    Xử lý khi bấm vào dòng chữ phụ khi login/register
    public void swapRegister(View v) {
        setContentView(R.layout.fragment_register);
    }

    public void swapLogin(View v) {
        setContentView(R.layout.fragment_login);
    }

//    Đóng client
    public void closeClient() {
        try {
            if (clientFd != null) {
                clientFd.close();
            }
            if (dIn != null) {
                dIn.close();
            }
            if (dOut != null) {
                dOut.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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

//    Get request
    public class GetRequestTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(apiUrl + params[0]);
                Log.d("debugGetURL", apiUrl + params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    readResponseBody(in);
                    Log.d("debugGetResponse", httpResponse);
                } else {
                }
            } catch (IOException e) {
            }
            return null;
        }
    }

//    Put request
    public class PutRequestTask extends AsyncTask<String, String, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(apiUrl + params[0]);
                Log.d("debugPutURL", apiUrl + params[0]);
                Log.d("debugPutContent", params[1]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                osw.write(params[1]);
                osw.flush();
                osw.close();

                BufferedReader in;
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    readResponseBody(in);
                    isHttpSuccess = true;
                } else {
                    in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    readResponseBody(in);
                    isHttpSuccess = false;
                    ExceptionError exceptionError = gson.fromJson(httpResponse, ExceptionError.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), exceptionError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                Log.d("debugPutResponse", httpResponse);
                Log.d("debugIsHttpSuccessPutTask", String.valueOf(isHttpSuccess));
            } catch (IOException e) {
            }
            return null;
        }
    }
}