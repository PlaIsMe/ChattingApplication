package com.chattingapplication.chattingclient.AsyncTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.chattingapplication.chattingclient.Model.User;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadFromURLTask extends AsyncTask<String, Void, Bitmap> {
    private User user;
    private ImageView imageView;

    public DownloadFromURLTask(User user, ImageView imageView){
        this.user = user;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap = null;
        try {
            if(params[0] != null){
                URL cloudinaryURL = new URL(params[0]);
                bitmap = BitmapFactory.decodeStream(
                        cloudinaryURL.openConnection().getInputStream()
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(bitmap != null && user != null){
            user.setDownloadAvatar(bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }
}
