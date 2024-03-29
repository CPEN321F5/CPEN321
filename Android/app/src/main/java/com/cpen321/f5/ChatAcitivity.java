package com.cpen321.f5;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatAcitivity extends AppCompatActivity implements TextWatcher {

    private WebSocket webSocket;
    private EditText messageText;
    private RecyclerView recycleContent;
    private String PATHofSERVER ="ws://20.106.78.177:8080";
    private Uri imageUri;
    private ChatAdapter chatAdapter;

    private JSONObject JsonConversation;
    private String myID = "1";
    private String userID1;
    private String user1name;
    private String user2name;
    private JSONArray messagesJson;
    private String conversationID;
    private String chatList_init_url = "http://20.106.78.177:8081/chat/getconversationlist/";
    private String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acitivity);
        myID = MainActivity.idOfUser;

        String wholeConversationUri = getIntent().getStringExtra("conversations");
        String call_method = getIntent().getStringExtra("GetOrPost");
        RequestQueue queue = Volley.newRequestQueue(this);
        int getMethod = Objects.equals(call_method, "GET") ? com.android.volley.Request.Method.GET : com.android.volley.Request.Method.POST;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(getMethod, wholeConversationUri, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    response.put("myID", myID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("TESTChat", response.toString());
                String wholeConversation = response.toString();
                getItemFromJson(wholeConversation);
                new SocketListener().run();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TESTChat", error.getMessage());
            }
        });
        queue.add(jsonObjectRequest);


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
        //required override method
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //required override method
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
        TextView chatRoomName = findViewById(R.id.chatRoomName);
        messageText = findViewById(R.id.editMessage);
        View sendButton = findViewById(R.id.Send_button);
        View imageButton = findViewById(R.id.image_button);
        View cameraButton = findViewById(R.id.camera_button);
        View chatListButton = findViewById(R.id.chatList_chatroom_button);
        View homeButton = findViewById(R.id.home_chatroom_button);
        recycleContent = findViewById(R.id.recyclerView);
        if(Objects.equals(myID, userID1)){
            chatRoomName.setText(user2name);
        }else{
            chatRoomName.setText(user1name);
        }


        /*initialize adapter */

        chatAdapter = new ChatAdapter(getLayoutInflater(), myID,  userID1, user1name, user2name);
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

                File photo = new File(getExternalCacheDir(), "output_photo.jpg");

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
                    if(result.getResultCode() == RESULT_OK){

                        //Decode image size
                        BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inJustDecodeBounds = true;
                        try{
                            InputStream Is = getContentResolver().openInputStream(imageUri);
                            BitmapFactory.decodeStream(Is, null, o);
                            Is.close();
                        }catch (FileNotFoundException error){
                            Toast.makeText(ChatAcitivity.this, "error", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int IMAGE_MAX_SIZE = 700;
                        int scale = 1;
                        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
                        }

                        //Decode with inSampleSize
                        BitmapFactory.Options o2 = new BitmapFactory.Options();
                        o2.inSampleSize = scale;

                        try {
                            InputStream Is2 = getContentResolver().openInputStream(imageUri);
                            Bitmap image = BitmapFactory.decodeStream(Is2, null, o2);
                            Is2.close();
                            sendImage(image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private final ActivityResultLauncher<Intent> requestForAlbum =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getData() != null && result.getResultCode() == RESULT_OK){
                        //Decode image size
                        BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inJustDecodeBounds = true;
                        try{
                            InputStream Is = getContentResolver().openInputStream(result.getData().getData());
                            BitmapFactory.decodeStream(Is, null, o);
                            Is.close();
                        }catch (FileNotFoundException error){
                            Toast.makeText(ChatAcitivity.this, "error", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int IMAGE_MAX_SIZE = 700;
                        int scale = 1;
                        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
                        }

                        //Decode with inSampleSize
                        BitmapFactory.Options o2 = new BitmapFactory.Options();
                        o2.inSampleSize = scale;

                        try {
                            InputStream Is2 = getContentResolver().openInputStream(result.getData().getData());
                            Bitmap image = BitmapFactory.decodeStream(Is2, null, o2);
                            Is2.close();
                            sendImage(image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void sendImage(Bitmap image) {
        ByteArrayOutputStream Os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,50, Os);
        String b64String = Base64.encodeToString(Os.toByteArray(), Base64.DEFAULT);

        Log.d("imageCompression", "size of b64string:" + b64String.length());

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
    private void getItemFromJson(String wholeConversation){
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
    }

    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//        }
    }

}