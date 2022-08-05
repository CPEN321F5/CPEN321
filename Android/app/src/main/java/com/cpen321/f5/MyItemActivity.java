package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONObject;

public class MyItemActivity extends AppCompatActivity
{
    private static final String TAG = "MyItemActivity";

    TextView removeButton;
    TextView homeButton;
    TextView editButton;
    RequestQueue requestQueue;

    ViewAdapterMyItem viewAdapterItem;
    DotsIndicator dotsIndicator;
    ViewPager viewPager;

    TextView _itemName;
    TextView _itemPrice;
    TextView _itemCategory;
    TextView _itemDescription;
    TextView _itemNumber;

    String itemName;
    String itemPrice;
    String itemCategory;
    String itemDescription;
    String itemNumber;

    String highestPriceHolder;
    String sellerID;

    TextView _ownerName;
    TextView _highestPriceHolderName;
    String ownerName;
    String highestPriceHolderName;

    String img0;
    String img1;
    String img2;

    public static Bitmap bitmap0;
    public static Bitmap bitmap1;
    public static Bitmap bitmap2;

    public static Drawable drawable0;
    public static Drawable drawable1;
    public static Drawable drawable2;

    String itemID;
    String status;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item);

        itemID = getIntent().getStringExtra("myItemID");
        status = getIntent().getStringExtra("status");
        Log.d("TAG", itemID);

        requestQueue = Volley.newRequestQueue(this);

        GETITEM();

        removeButton = findViewById(R.id.remove_button);
        removeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                REMOVEITEM();

                Intent removeIntent = new Intent(MyItemActivity.this, MainUI.class);
                startActivity(removeIntent);
            }
        });

        editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(MyItemActivity.this, EditActivity.class);
                editIntent.putExtra("itemID",itemID);
                if(status.equals("active")){
                    startActivity(editIntent);
                }else{
                    editButton.setText("Edit Button is No Longer Available");
                }
            }
        });

        homeButton = findViewById(R.id.home_myItem_button);
        homeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                REMOVEITEM();

                Intent homeIntent = new Intent(MyItemActivity.this, MainUI.class);
                startActivity(homeIntent);
            }
        });
    }

    private void GETITEM ()
    {
        String GETITEMURL = "http://20.106.78.177:8081/item/getbyid/" + itemID + "/";
        Log.d(TAG, GETITEMURL);

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
                    itemCategory = response.getString("catagory");
                    itemNumber = response.getString("ItemID");
                    itemDescription = response.getString("description");
                    sellerID = response.getString("sellerID");
                    highestPriceHolder = response.getString("currentPriceHolder");

                    img0 = response.getString("image_0");
                    img1 = response.getString("image_1");
                    img2 = response.getString("image_2");

                    //Log.d(TAG, "Base 64: " + img0);

                    bitmap0 = base64ToBitmap(img0);
                    drawable0 = new BitmapDrawable(getResources(), bitmap0);

                    if (img1.equals(""))
                    {
                        bitmap1 = base64ToBitmap(img0);
                        drawable1 = new BitmapDrawable(getResources(), bitmap1);
                    }

                    else
                    {
                        bitmap1 = base64ToBitmap(img1);
                        drawable1 = new BitmapDrawable(getResources(), bitmap1);
                    }

                    if (img2.equals(""))
                    {
                        bitmap2 = base64ToBitmap(img0);
                        drawable2 = new BitmapDrawable(getResources(), bitmap2);
                    }

                    else
                    {
                        bitmap2 = base64ToBitmap(img2);
                        drawable2 = new BitmapDrawable(getResources(), bitmap2);
                    }

                    viewAdapterItem = new ViewAdapterMyItem(MyItemActivity.this);
                    viewPager = findViewById(R.id.view_pager);
                    dotsIndicator = findViewById(R.id.dots_indicator);

                    viewPager.setAdapter(viewAdapterItem);
                    dotsIndicator.setViewPager(viewPager);

                    _itemName = findViewById(R.id.item_name_caption);
                    _itemName.setText(itemName);

                    _itemPrice = findViewById(R.id.item_price_caption);
                    _itemPrice.setText("$ " + itemPrice);

                    _itemCategory = findViewById(R.id.item_category_caption);
                    _itemCategory.setText("Category: " + itemCategory);

                    _itemNumber = findViewById(R.id.item_id_caption);
                    _itemNumber.setText("Item ID: " + itemNumber);

                    _itemDescription = findViewById(R.id.item_description_caption);
                    _itemDescription.setText(itemDescription);

                    if (highestPriceHolder.equals("no one bid yet"))
                    {
                        _highestPriceHolderName = findViewById(R.id.highest_name);
                        _highestPriceHolderName.setText("Price Holder: No Bidders");
                    }

                    else
                    {
                        GETHIGHEST(highestPriceHolder);
                    }

                    GETOWNER(sellerID);

                }
                catch (Exception w)
                {
                    Toast.makeText(MyItemActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MyItemActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void REMOVEITEM()
    {
        String REMOVEITEMURL = "http://20.106.78.177:8081/item/removeitem/" + itemID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, REMOVEITEMURL, null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "attribute = " + response.toString());

                try
                {
                    Toast.makeText(MyItemActivity.this, "ITEM REMOVED", Toast.LENGTH_LONG).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(MyItemActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MyItemActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void GETOWNER (String ID)
    {
        String GETPROFURL = "http://20.106.78.177:8081/user/getprofile/";
        Log.d(TAG, GETPROFURL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETPROFURL+ ID + "/", null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "attribute = " + response.toString());

                try
                {
                    ownerName = response.getString("FirstName") + " " + response.getString("LastName");

                    _ownerName = findViewById(R.id.owner_name);
                    _ownerName.setText("Owner: " + ownerName);

                    //Toast.makeText(MyItemActivity.this, "CREDENTIALS RETRIEVED", Toast.LENGTH_LONG).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(MyItemActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MyItemActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void GETHIGHEST (String ID)
    {
        String GETPROFURL = "http://20.106.78.177:8081/user/getprofile/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETPROFURL+ ID + "/", null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "attribute = " + response.toString());

                try
                {
                    highestPriceHolderName = response.getString("FirstName") + " " + response.getString("LastName");

                    _highestPriceHolderName = findViewById(R.id.highest_name);
                    _highestPriceHolderName.setText("Price Holder: " + highestPriceHolderName);

                    //Toast.makeText(MyItemActivity.this, "CREDENTIALS RETRIEVED", Toast.LENGTH_LONG).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(MyItemActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MyItemActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private Bitmap base64ToBitmap (String img)
    {
        byte[] bytes = Base64.decode(img, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}