package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BidActivity extends AppCompatActivity {
    private final String TAG = "BidActivity";

    private TextView toAddPriceText;
    private TextView currentPriceText;
    private TextView priceHolderText;
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

        FloatingActionButton addButton = findViewById(R.id.add_button);
        FloatingActionButton subButton = findViewById(R.id.sub_button);
        Button bidButton = findViewById(R.id.inner_bid_button);

        toAddPriceText = findViewById(R.id.to_add_price);
        currentPriceText = findViewById(R.id.currentPrice);
        priceHolderText = findViewById(R.id.highestBidder);
        TextView expireTimeText = findViewById(R.id.end_time);

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
                Intent ItemUI = new Intent(BidActivity.this, ItemActivity.class);
                startActivity(ItemUI);
            }
        });
    }

    private void updPrice() {
        RequestQueue queue = Volley.newRequestQueue(BidActivity.this);
        String url = getString(R.string.url_item_bid);
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