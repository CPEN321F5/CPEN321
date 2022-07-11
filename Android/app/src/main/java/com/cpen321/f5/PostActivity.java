package com.cpen321.f5;

import static com.android.volley.VolleyLog.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class PostActivity extends AppCompatActivity {
    String title;
    String description;
    String location;
    String startPrice;
    String deposit;
    String stepPrice;
    String timeLast;
    String postTime;

    String timeExpire;

    private final String TAG = "PostActivity";

    private Button postButton;
    private Button cancelButton;

    JSONObject jsonObject = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_items);

        postButton = findViewById(R.id.post_post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                title = ((EditText)findViewById(R.id.post_title)).getText().toString().trim();
                description = ((EditText)findViewById(R.id.post_description)).getText().toString().trim();
                location = ((TextView)findViewById(R.id.post_location)).getText().toString().trim();
                startPrice = ((EditText)findViewById(R.id.post_start_price)).getText().toString().trim();
                deposit = ((EditText)findViewById(R.id.post_deposit)).getText().toString().trim();
                stepPrice = ((EditText)findViewById(R.id.post_step_price)).getText().toString().trim();
                timeLast = ((EditText)findViewById(R.id.post_time_last)).getText().toString().trim();
                postTime = getTime();



                Log.d(TAG, "title = " + title);
                Log.d(TAG, "description = " + description);
                Log.d(TAG, "startP = " + startPrice);
                Log.d(TAG, "stepP = " + stepPrice);
                Log.d(TAG, "deposit = " + deposit);
                Log.d(TAG, "how lone = " + timeLast);
                Log.d(TAG, "post time = " + postTime);

                if (validCheck()){
                    long currentTime = Instant.now().toEpochMilli() / 1000;
                    long expireTime = currentTime + Integer.parseInt(timeLast) * 3600;
                    timeExpire = Long.toString(expireTime);
                    Log.d(TAG, "expire time = " + timeExpire);
                    postDataToServer();
                    Intent MainUI = new Intent(PostActivity.this, MainUI.class);
                    startActivity(MainUI);
                }
            }
        });

        cancelButton = findViewById(R.id.post_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainUI = new Intent(PostActivity.this, MainUI.class);
                startActivity(MainUI);
            }
        });

    }


    private void postDataToServer(){
        String url = getString(R.string.url_post);
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

                params.put("sellerID", MainActivity.idOfUser);
                params.put("name", title);
                params.put("description", description);
                params.put("location", location);
                params.put("startPrice", startPrice);
                params.put("deposit", deposit);
                params.put("stepPrice", stepPrice);
                params.put("postTime", postTime);
                params.put("timeLast", timeLast);
                params.put("timeExpire", timeExpire);

                params.put("expired", "false");
                //const
                params.put("refund", "false");
                params.put("needAdmin", "false");
                params.put("refundDescription", "");


                return params;
            }
        };
        queue.add(postRequest);
    }

    private boolean validCheck(){
        if (startPrice.equals("")){
            Toast.makeText(PostActivity.this, "Fail, Start Price is not set yet", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (deposit.equals("")){
            Toast.makeText(PostActivity.this, "Fail, Deposit is not set yet", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (stepPrice.equals("")){
            Toast.makeText(PostActivity.this, "Fail, Step Price is not set yet", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (timeLast.equals("")){
            Toast.makeText(PostActivity.this, "Fail, the post last hour is not set yet", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Integer.parseInt(timeLast) > 168){
            Toast.makeText(PostActivity.this, "Fail, the post should last less than 7 days", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String getTime(){
        String time;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }


}

