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

import org.json.JSONException;
import org.json.JSONObject;

public class ItemActivity extends AppCompatActivity {

    private static final String TAG = "ItemActivity";

    public static String stepPrice;
    public static String itemPrice;
    public static String highestPriceHolder;
    public static String expireTime;
    public static String sellerID;

    RequestQueue requestQueue;

    TextView _itemName;
    TextView _itemPrice;
    TextView _itemCategory;
    TextView _itemDescription;
    TextView _itemLocation;
    TextView _itemNumber;

    private Button bidButton;
    private Button contactsellerButton;


    String itemName;
    //String itemPrice;
    String itemCategory;
    String itemDescription;
    String itemLocation;
    String itemNumber;

    public static String itemID;
    String tmpID = ItemListActivity.ItemID;

    String GETITEMURL = "http://20.106.78.177:8081/item/getbyid/" + tmpID + "/";;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);



        requestQueue = Volley.newRequestQueue(this);

        Log.d(TAG, GETITEMURL);
        GETITEM();

        String chat_init_url = "http://20.106.78.177:8081/chat/initconversation/";
        String myID = MainActivity.idOfUser;


        bidButton = findViewById(R.id.bid_button);
        bidButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                itemID = tmpID;
                Intent bidIntent = new Intent(ItemActivity.this, BidActivity.class);
                startActivity(bidIntent);
            }
        });

        contactsellerButton = findViewById(R.id.contact_seller_button);
        contactsellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //init userid2 string here to wait for GETITEM response
                String userID2 = ItemActivity.sellerID;
                String chat_url = chat_init_url + myID + "/" + userID2;

                RequestQueue queue = Volley.newRequestQueue(ItemActivity.this);
                Intent intent = new Intent(ItemActivity.this, ChatAcitivity.class);
                Log.d("CHAT", "starting chat with url" + chat_url);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, chat_url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            response.put("myID", myID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("CHAT", response.toString());
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
            }
        });
    }

    private void GETITEM ()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETITEMURL, null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "attribute = " + response.toString());

                try
                {
                    itemName = response.getString("name");
                    itemPrice = response.getString("currentPrice");
                    itemCategory = response.getString("name");
                    itemLocation = response.getString("location");
                    itemNumber = response.getString("ItemID");
                    itemDescription = response.getString("description");
                    sellerID = response.getString("sellerID");
                    highestPriceHolder = response.getString("currentPriceHolder");
                    stepPrice = response.getString("stepPrice");
                    expireTime = response.getString("timeExpire");
//                            "1320105600";

                    _itemName = findViewById(R.id.item_name_caption);
                    _itemName.setText(itemName);

                    _itemPrice = findViewById(R.id.item_price_caption);
                    _itemPrice.setText("$ " + itemPrice);

                    _itemCategory = findViewById(R.id.item_category_caption);
                    _itemCategory.setText(itemCategory);

                    _itemLocation = findViewById(R.id.item_location_caption);
                    _itemLocation.setText("Item Location: " + itemLocation);

                    _itemNumber = findViewById(R.id.item_id_caption);
                    _itemNumber.setText("Item ID: " + itemNumber);

                    _itemDescription = findViewById(R.id.item_description_caption);
                    _itemDescription.setText(itemDescription);

                    Toast.makeText(ItemActivity.this, "CREDENTIALS RETRIEVED", Toast.LENGTH_LONG).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(ItemActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(ItemActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

}
