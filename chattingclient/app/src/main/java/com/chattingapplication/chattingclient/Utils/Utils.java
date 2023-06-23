package com.chattingapplication.chattingclient.Utils;

import android.content.Context;
import android.util.Log;

import com.chattingapplication.chattingclient.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

public class Utils {

    public static String getJsonString(Context mainActivity, String fileName){
        String jsonString;
        try {
            InputStream input = null;
            input = mainActivity.getAssets().open(fileName);
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            jsonString = new String(buffer, "UTF-8");
            Log.d("JSON", jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return jsonString;
    }

    public static String getIpV4(Context mainActivity, String fileName){
        String jsonString = getJsonString(mainActivity, fileName);
        Type typeToken = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> data = new Gson().fromJson(jsonString, typeToken);
        Log.d("IPV4", data.get("ipv4"));
        return data.get("ipv4");
    }
}
