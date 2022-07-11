package com.example.mywebsocket;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    String userID1 ="1";
    String userID2 ="2";
    String myID = "1";
    String chat_init_url = "http://20.106.78.177:8081/chat/initconversation/";
    String chat_url = chat_init_url + myID + "/" + userID2;
    String chatList_init_url = "http://20.106.78.177:8081/chat/getconversationlist/";
    String chatList_url = chatList_init_url + myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);

        findViewById(R.id.enter_button)
                .setOnClickListener(v ->{
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    Intent intent = new Intent(this, ChatAcitivity.class);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, chat_url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                response.put("myID", "1");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("TEST1", response.toString());
                            intent.putExtra("conversations", response.toString());
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("TEST1", error.getMessage());
                        }
                    });
                    queue.add(jsonObjectRequest);

                });

        findViewById(R.id.chatList_button)
                .setOnClickListener(v -> {
                    RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                    Intent intent = new Intent(this, ChatlistsActivity.class);
                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, chatList_url, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("TEST1", response.toString());
                            intent.putExtra("conversationsList", response.toString());
                            intent.putExtra("myID", myID);
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("TEST1", chatList_url);
                        }
                    });
                    queue.add(jsonArrayRequest);
                });


    }
}

