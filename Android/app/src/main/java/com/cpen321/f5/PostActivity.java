package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class PostActivity extends AppCompatActivity {
    String title;
    String description;
    String location;
    double startPrice = 0;
    double stepPrice = 0;

    private Button postButton;


    JSONObject jsonObject = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("post_try", "enter onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_items);


        title = ((EditText)findViewById(R.id.post_title)).getText().toString().trim();
        description = ((EditText)findViewById(R.id.post_description)).getText().toString().trim();
        location = ((TextView)findViewById(R.id.post_location)).getText().toString().trim();

        //TODO: fix the bug while user input is not a double
//        startPrice = Double.parseDouble(((EditText)findViewById(R.id.post_start_price)).getText().toString().trim());
//        stepPrice = Double.parseDouble(((EditText)findViewById(R.id.post_step_price)).getText().toString().trim());
        startPrice = 0;
        stepPrice = 0;

//        try {
//            jsonObject.put("UserID", "13427");
//            jsonObject.put("title", title);
//            jsonObject.put("description", description);
//            jsonObject.put("location", location);
//            jsonObject.put("startPrice", startPrice);
//            jsonObject.put("stepPrice", stepPrice);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        postButton = findViewById(R.id.post_post);
        postButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                postDataToServer();
            }
        });

    }


    private void postDataToServer(){
        String url = "http://20.106.78.177:8081/";
        RequestQueue queue = Volley.newRequestQueue(PostActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(PostActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(PostActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("UserID", "13427");
                params.put("title", title);
                params.put("description", description);
                params.put("location", location);
                params.put("startPrice", Double.toString(startPrice));
                params.put("stepPrice", Double.toString(stepPrice));
                return params;
            }
        };
        queue.add(postRequest);
    }


}

