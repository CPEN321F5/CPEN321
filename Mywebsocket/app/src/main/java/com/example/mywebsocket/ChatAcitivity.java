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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatAcitivity extends AppCompatActivity implements TextWatcher {

    private WebSocket webSocket;
    private EditText messageText;
    private TextView chatRoomName;
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

    private String wholeConversation;
    private JSONObject JsonConversation;
    private String myID = "1";
    private String userID1;
    private String userID2;
    private String user1name;
    private String user2name;
    private String messagesString;
    private JSONArray messagesJson;
    private String conversationID;

    private String chatList_init_url = "http://20.106.78.177:8081/chat/getconversationlist/";

    private String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acitivity);

        wholeConversation = getIntent().getStringExtra("conversations");




        //get items from JSON
        if( wholeConversation!= null){
            try {
                JsonConversation = new JSONObject(wholeConversation);
            } catch (JSONException e) {
                e.printStackTrace();
                JsonConversation = new JSONObject();
            }

            try {
                userID1 = JsonConversation.getString("user1");
                userID2 = JsonConversation.getString("user2");
                user1name = JsonConversation.getString("user1name");
                user2name = JsonConversation.getString("user2name");
                messagesJson = JsonConversation.getJSONArray("messages");
                conversationID = JsonConversation.getString("conversationID");
                myID = JsonConversation.getString("myID");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Creating Chat Activity");
            Log.d(TAG, JsonConversation.toString());
        }

        //create socket
        new SocketListener().run();

        //initView(); //use to test without server
    }

    //call when client successfully connect to the server
    // call when receive any message as formal string; receive except sender
    private class SocketListener extends WebSocketListener{

        private void run(){
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(PATHofSERVER + "?ConversationID=" + conversationID)
                    .build();

            webSocket = client.newWebSocket(request, this);

            client.dispatcher().executorService().shutdown();
        }

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            super.onOpen(webSocket, response);
            runOnUiThread(()->{
                Toast.makeText(ChatAcitivity.this, "Socket Connection Successful", Toast.LENGTH_SHORT).show();
                try {
                    Log.d(TAG, "Opening Socket Connection");
                    initView();
                } catch (JSONException e) {
                    Log.d("TEST", "init error");
                    e.printStackTrace();
                }
            });
        }


        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);
            runOnUiThread(() ->{
                try {
                    JSONObject jsonObject = new JSONObject(text);
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

    private void initView() throws JSONException {
        chatRoomName = findViewById(R.id.chatRoomName);
        messageText = findViewById(R.id.editMessage);
        sendButton = findViewById(R.id.Send_button);
        imageButton = findViewById(R.id.image_button);
        cameraButton = findViewById(R.id.camera_button);
        chatListButton = findViewById(R.id.chatList_chatroom_button);
        homeButton = findViewById(R.id.home_chatroom_button);
        recycleContent = findViewById(R.id.recyclerView);
        if(Objects.equals(myID, userID1)){
            chatRoomName.setText(user2name);
        }else{
            chatRoomName.setText(user1name);
        }


        /*initialize adapter */

        chatAdapter = new ChatAdapter(getLayoutInflater(),myID,  userID1, userID2, user2name, conversationID);
        recycleContent.setAdapter(chatAdapter);
        recycleContent.setLayoutManager(new LinearLayoutManager(this));

        /*initialize adapter */

        //load history
        initHistory(messagesJson);

        messageText.addTextChangedListener(this);

        //code for sending text message
        sendButton.setOnClickListener(v->{ //transfer to Json
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("conversationID",conversationID);
                jsonObject.put("message",messageText.getText().toString());
                jsonObject.put("userID",myID);
                webSocket.send(jsonObject.toString());
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
            String chatList_url = chatList_init_url + myID;
            RequestQueue queue = Volley.newRequestQueue(v.getContext());
            Intent intent = new Intent(this, ChatlistsActivity.class);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(com.android.volley.Request.Method.GET, chatList_url, null, new com.android.volley.Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d("TEST1", response.toString());
                    intent.putExtra("conversationsList", response.toString());
                    intent.putExtra("myID", myID);
                    startActivity(intent);
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TEST1", chatList_url);
                }
            });
            queue.add(jsonArrayRequest);
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
            jsonObject.put("conversationID",conversationID);
            jsonObject.put("userID",myID);
            jsonObject.put("image",b64String);
            jsonObject.put("message","[Click to see this image...]");
            webSocket.send(jsonObject.toString());
            chatAdapter.addItem(jsonObject);
            recycleContent.smoothScrollToPosition(chatAdapter.getItemCount()-1);
        }catch (JSONException error){
            error.printStackTrace();
        }
    }

    private void initHistory(JSONArray messagesJson){
        for(int i = 0; i <= messagesJson.length() - 1; i++){
            try {
                chatAdapter.addItem(messagesJson.getJSONObject(i));
                recycleContent.smoothScrollToPosition(chatAdapter.getItemCount()-1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}