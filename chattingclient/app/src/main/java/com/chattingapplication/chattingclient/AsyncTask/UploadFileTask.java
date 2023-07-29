package com.chattingapplication.chattingclient.AsyncTask;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.chattingapplication.chattingclient.AuthenticationActivity;
import com.chattingapplication.chattingclient.LoadActivity;
import com.chattingapplication.chattingclient.R;
import com.chattingapplication.chattingclient.Utils.HttpResponse;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UploadFileTask extends AsyncTask<String, Void, String> {

    private static final String BOUNDARY = "======";
    private static final String LINEBREAK = "\r\n";

    private Uri uri;

    private final Context context;

    public UploadFileTask(Context context, Uri uri){
        this.uri = uri;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            if(uri == null){
                return null;
            }
            URL apiUrl = new URL(LoadActivity.apiUrl + params[0]);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            Log.d("debugPostURL", LoadActivity.apiUrl + params[0]);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+BOUNDARY);
            connection.setDoOutput(true);

            DataOutputStream output = new DataOutputStream (connection.getOutputStream());

            //add file part
            output.writeBytes("--" + BOUNDARY + LINEBREAK);
            output.writeBytes(String.format(
                    "Content-Disposition: form-data; name=\"uploadAvatar\"; filename=\"%s\"%s",
                    params[1], LINEBREAK)
            );
            output.writeBytes("Content-Type " +
                    URLConnection.guessContentTypeFromName(params[1]) +
                    LINEBREAK);
            output.writeBytes(LINEBREAK);
            DataInputStream dataInputStream = new
                    DataInputStream(context.getContentResolver().openInputStream(uri));
            byte[] buffer = new byte[4096];
            int bytesRead;
            while((bytesRead = dataInputStream.read(buffer)) != -1){
                output.write(buffer, 0, bytesRead);
            }
            dataInputStream.close();
            output.writeBytes(LINEBREAK);

            // End part
            output.writeBytes("--" + BOUNDARY + "--" + LINEBREAK);

            output.flush();
            output.close();

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();

            Log.d("UploadFileResponseCode", String.valueOf(responseCode));
            Log.d("UploadFileresponseMessage", String.valueOf(responseMessage));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
