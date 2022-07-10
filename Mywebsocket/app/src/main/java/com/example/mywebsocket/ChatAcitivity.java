package com.example.mywebsocket;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatAcitivity extends AppCompatActivity implements TextWatcher {

    private String username;
    private WebSocket webSocket;
    private EditText messageText;
    private View sendButton;
    private View imageButton;
    private View cameraButton;
    private View chatListButton;
    private View homeButton;
    private RecyclerView recycleContent;
    private String PATHofSERVER ="ws://20.106.78.177:8080";
    private File photo;
    private Uri imageUri;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acitivity);

        username = getIntent().getStringExtra("name");
        new SocketListener().run();
        initView();
    }

    //call when client successfully connect to the server
    // call when receive any message as formal string; receive except sender
    private class SocketListener extends WebSocketListener{

        private void run(){
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(PATHofSERVER)
                    .build();

            webSocket = client.newWebSocket(request, this);

            client.dispatcher().executorService().shutdown();
        }

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            super.onOpen(webSocket, response);
            runOnUiThread(()->{
                Toast.makeText(ChatAcitivity.this, "Socket Connection Successful", Toast.LENGTH_SHORT).show();
                initView();
            });
        }


        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);
            runOnUiThread(() ->{
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    jsonObject.put("isSent", false); //is when receive
                    chatAdapter.addItem(jsonObject);
                    recycleContent.smoothScrollToPosition(chatAdapter.getItemCount()-1);
                } catch (JSONException error) {
                    error.printStackTrace();
                }
            });
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
            Log.d("CHAT", t.getMessage());
        }

        @Override
        public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            super.onClosing(webSocket, code, reason);
            Log.d("CHAT", "Closing" + code + " / reason " + reason);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String content = s.toString().trim(); // delete space
        if (content.isEmpty()) {
            resetMessageContent();
        }
    }

    private void resetMessageContent() {
        messageText.removeTextChangedListener(this);
        messageText.setText("");
        messageText.addTextChangedListener(this);
    }

    private void initView() {
        messageText = findViewById(R.id.editMessage);
        sendButton = findViewById(R.id.Send_button);
        imageButton = findViewById(R.id.image_button);
        cameraButton = findViewById(R.id.camera_button);
        chatListButton = findViewById(R.id.chatList_chatroom_button);
        homeButton = findViewById(R.id.home_chatroom_button);
        recycleContent = findViewById(R.id.recyclerView);

        /*initialize adapter */

        chatAdapter = new ChatAdapter(getLayoutInflater());
        recycleContent.setAdapter(chatAdapter);
        recycleContent.setLayoutManager(new LinearLayoutManager(this));

        /*initialize adapter */

        messageText.addTextChangedListener(this);
        sendButton.setOnClickListener(v->{ //transfer to Json
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("name",username);
                jsonObject.put("message",messageText.getText().toString());
                webSocket.send(jsonObject.toString());
                jsonObject.put("isSent", true);
                chatAdapter.addItem(jsonObject);
                recycleContent.smoothScrollToPosition(chatAdapter.getItemCount()-1);
                resetMessageContent();
            }catch (JSONException error){
                error.printStackTrace();
            }
        });

        imageButton.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            requestForAlbum.launch(intent);
            Toast.makeText(ChatAcitivity.this, "Trying to open album", Toast.LENGTH_SHORT).show();
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                photo = new File(getExternalCacheDir(), "output_photo.jpg");

                //if the same photo exist, directly use the existing one
                try {
                    if (photo.exists()) {
                        photo.delete();
                    }
                    photo.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //if system version is higher than 7.0, use Uri is unsafe
                if(Build.VERSION.SDK_INT < 24){
                    imageUri = Uri.fromFile(photo);
                }else{
                    // use file provider to protect data
                    imageUri = FileProvider.getUriForFile(ChatAcitivity.this, "com.f5.camera.fileprovider", photo);
                }

                //open camera
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //specify the saving path
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //save photo into output_photo.jpg
                requestForCamera.launch(intent);

            }

        });

        chatListButton.setOnClickListener(v ->{
            Intent intent = new Intent(this, ChatlistsActivity.class);
            startActivity(intent);
        });

        homeButton.setOnClickListener(v ->{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

    }

    private ActivityResultLauncher requestForCamera =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getData() != null && result.getResultCode() == RESULT_OK){
                        try{
                            InputStream Is = getContentResolver().openInputStream(imageUri);
                            Bitmap image = BitmapFactory.decodeStream(Is);
                            sendImage(image);
                        }catch (FileNotFoundException error){
                            error.printStackTrace();
                        }
                    }
                }
            });

    private ActivityResultLauncher<Intent> requestForAlbum =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getData() != null && result.getResultCode() == RESULT_OK){
                        try{
                            InputStream Is = getContentResolver().openInputStream(result.getData().getData());
                            Bitmap image = BitmapFactory.decodeStream(Is);
                            Toast.makeText(ChatAcitivity.this, "work", Toast.LENGTH_SHORT).show();
                            sendImage(image);
                        }catch (FileNotFoundException error){
                            Toast.makeText(ChatAcitivity.this, "error", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    }
                }
            });

    private void sendImage(Bitmap image) {
        ByteArrayOutputStream Os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,50, Os);
        String b64String = Base64.encodeToString(Os.toByteArray(), Base64.DEFAULT);

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put("name",username);
            jsonObject.put("image",b64String);
            webSocket.send(jsonObject.toString());
            jsonObject.put("isSent", true);
            chatAdapter.addItem(jsonObject);
            recycleContent.smoothScrollToPosition(chatAdapter.getItemCount()-1);
        }catch (JSONException error){
            error.printStackTrace();
        }
    }

}