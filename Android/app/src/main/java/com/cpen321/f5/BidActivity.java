package com.cpen321.f5;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class BidActivity extends AppCompatActivity {
    private final String TAG = "BidActivity";

    private Button bidButton;
    private FloatingActionButton addButton, subButton;
    private TextView toAddPriceText, currentPriceText, priceHolderText, expireTimeText;
    int currentPrice = Integer.parseInt(ItemActivity.itemPrice);
    int stepPrice = Integer.parseInt(ItemActivity.stepPrice);
    String currentPriceHolder = ItemActivity.highestPriceHolder;
    String itemID = ItemActivity.itemID;
    String formattedExpireTime;

    int addPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid);

        long seconds = Long.parseLong(ItemActivity.expireTime);
        long millis = seconds * 1000;

        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy h:mm,a", Locale.ENGLISH);
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        formattedExpireTime = sdf.format(date);

        //formattedExpireTime = ItemActivity.expireTime;
        Log.d(TAG, "time = " + formattedExpireTime);

        addButton = findViewById(R.id.add_button);
        subButton = findViewById(R.id.sub_button);
        bidButton = findViewById(R.id.inner_bid_button);

        toAddPriceText = findViewById(R.id.to_add_price);
        currentPriceText = findViewById(R.id.currentPrice);
        priceHolderText = findViewById(R.id.highestBidder);
        expireTimeText = findViewById(R.id.end_time);

        currentPriceText.setText(Integer.toString(currentPrice));
        priceHolderText.setText(currentPriceHolder);
        expireTimeText.setText(formattedExpireTime);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPrice += stepPrice;
                toAddPriceText.setText(Integer.toString(addPrice));
            }
        });

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPrice -= stepPrice;
                if (addPrice < 0){
                    addPrice = 0;
                }
                toAddPriceText.setText(Integer.toString(addPrice));
            }
        });

        bidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updPrice();
            }
        });
    }

    private void updPrice() {
        RequestQueue queue = Volley.newRequestQueue(BidActivity.this);
        String url = getString(R.string.url_post);
        currentPrice += addPrice;
        currentPriceHolder = MainActivity.idOfUser;
        StringRequest postRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(BidActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(BidActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("ItemID", itemID);
                params.put("currentPrice", Integer.toString(currentPrice));
                params.put("currentPriceHolder", currentPriceHolder);

                return params;
            }
        };
        currentPriceText.setText(Integer.toString(currentPrice));
        priceHolderText.setText(currentPriceHolder);
        queue.add(postRequest);
    }
}