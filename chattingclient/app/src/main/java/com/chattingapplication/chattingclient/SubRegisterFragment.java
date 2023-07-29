package com.chattingapplication.chattingclient;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.chattingapplication.chattingclient.AsyncTask.PatchRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.PostRequestTask;
import com.chattingapplication.chattingclient.AsyncTask.UploadFileTask;
import com.chattingapplication.chattingclient.Utils.HttpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubRegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubRegisterFragment extends Fragment {
    private AuthenticationActivity authenticationActivity;
    private static final int REQUEST_IMAGE_SELECT = 100;
    private Uri uploadAvatar = null;
    private ImageView userAvatar;
    public SubRegisterFragment() {
        // Required empty public constructor
    }

    public static SubRegisterFragment newInstance() {
        SubRegisterFragment fragment = new SubRegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authenticationActivity = (AuthenticationActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_register, container, false);
        Button btnSubmitSubRegister = (Button) view.findViewById(R.id.btnSubmitSubRegister);
        Button btnAvatar = view.findViewById(R.id.btnChangeAvatar);
        userAvatar = view.findViewById(R.id.uploadAvatar);

        btnSubmitSubRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTxtFirstName = (EditText) view.findViewById(R.id.editTxtFirstName);
                EditText editTxtLastName = (EditText) view.findViewById(R.id.editTxtLastName);
                EditText editTxtGender = (EditText) view.findViewById(R.id.editTxtGender);

                PatchRequestTask patchRequestTask = new PatchRequestTask(new HttpResponse(authenticationActivity));
                String path = String.format("user/%s", LoadActivity.currentAccount.getUser().getId());
                String uploadAvatarPath = "user/upload_avatar/" + LoadActivity.currentAccount.getUser().getId();
                UploadFileTask uploadFileTask = new UploadFileTask(getContext(), uploadAvatar);

                if(uploadAvatar != null){
                    Log.d("AvatarURI", uploadAvatar.getPath());
                    File file = new File(uploadAvatar.getPath());
                    Log.d("AvatarURI", file.getName());
                    try {
                        uploadFileTask.execute(uploadAvatarPath, file.getName());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                try {
                    String jsonString = new JSONObject()
                            .put("firstName", editTxtFirstName.getText())
                            .put("lastName", editTxtLastName.getText())
                            .put("gender", editTxtGender.getText())
                            .toString();
                    patchRequestTask.execute(path, jsonString, "handleResponseSubRegister");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Upload avatar here
        btnAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(intent, REQUEST_IMAGE_SELECT);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uploadAvatar = data.getData();
                userAvatar.setImageURI(uploadAvatar);
            }
        }
    }

}