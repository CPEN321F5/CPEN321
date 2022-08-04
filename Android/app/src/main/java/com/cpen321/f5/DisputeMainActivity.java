package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class DisputeMainActivity extends AppCompatActivity
{
    private static final String TAG = "DisputeMainActivity";

    RequestQueue requestQueue;

    Button fetchButton;

    String orderItemID;
    TextView _orderItemID;

    public static String buyerName;
    public static String sellerName;
    public static String itemName;
    public static String itemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispute_main);

        requestQueue = Volley.newRequestQueue(this);

        fetchButton = findViewById(R.id.fetch_button);
        fetchButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                _orderItemID = findViewById(R.id.order_caption);
                orderItemID = _orderItemID.getText().toString();
                Log.d(TAG, "ID = " + orderItemID);

                if (validCheck1())
                {
                    GETDISPUTEITEM();
                }
            }
        });
    }

    private boolean validCheck1()
    {
        if (orderItemID.equals(""))
        {
            Toast.makeText(DisputeMainActivity.this, "ID Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void GETDISPUTEITEM ()
    {
        String GETDISPUTEITEMURL = "http://20.106.78.177:8081/item/getbyid/" + orderItemID + "/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETDISPUTEITEMURL, null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "attribute = " + response.toString());

                try
                {
                    buyerName = response.getString("currentPriceHolder");
                    sellerName = response.getString("sellerID");
                    itemName = response.getString("name");
                    itemImage = response.getString("image_0");

                    Toast.makeText(DisputeMainActivity.this, "CREDENTIALS RETRIEVED", Toast.LENGTH_LONG).show();

                    Intent disputeIntent = new Intent(DisputeMainActivity.this, DisputeActivity.class);
                    startActivity(disputeIntent);
                }
                catch (Exception w)
                {
                    Toast.makeText(DisputeMainActivity.this, "Incorrect Order/Item ID",Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(DisputeMainActivity.this, "Incorrect Order/Item ID",Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}