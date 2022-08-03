package com.cpen321.f5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class ItemActivity extends AppCompatActivity implements LocationListener
{
    private static final String TAG = "ItemActivity";

    public static String stepPrice;
    public static String itemPrice;
    public static String highestPriceHolder;
    public static String expireTime;
    public static String sellerID;

    ViewAdapterItem viewAdapterItem;
    DotsIndicator dotsIndicator;
    ViewPager viewPager;

    RequestQueue requestQueue;

    TextView _itemName;
    TextView _itemPrice;
    TextView _itemCategory;
    TextView _itemDescription;
    TextView _itemLocation;
    TextView _itemNumber;

    String itemName;
    //String itemPrice;
    String itemCategory;
    String itemDescription;
    String itemLocationLong;
    String itemLocationLat;
    String itemNumber;

    String img0;
    String img1;
    String img2;

    public static Bitmap bitmap0;
    public static Bitmap bitmap1;
    public static Bitmap bitmap2;

    public static Drawable drawable0;
    public static Drawable drawable1;
    public static Drawable drawable2;

    public static String itemID;
    String tmpID = ItemListActivity.ItemID;

    String GETITEMURL = "http://20.106.78.177:8081/item/getbyid/" + tmpID + "/";;

    LocationManager locationManager;
    private double lat;
    private double lon;

    private double lat_item;
    private double lon_item;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        //viewAdapterItem = new ViewAdapterItem(this);

        Button contactsellerButton;
        Button bidButton;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, (LocationListener) this);

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
                    itemLocationLong = response.getString("location_lon");
                    itemLocationLat = response.getString("location_lat");
                    itemNumber = response.getString("ItemID");
                    itemDescription = response.getString("description");
                    sellerID = response.getString("sellerID");
                    highestPriceHolder = response.getString("currentPriceHolder");
                    stepPrice = response.getString("stepPrice");
                    expireTime = response.getString("timeExpire");
//                            "1320105600";

                    img0 = response.getString("image_0");
                    img1 = response.getString("image_1");
                    img2 = response.getString("image_2");

                    Log.d(TAG, "Base 64: " + img0);

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

                    viewAdapterItem = new ViewAdapterItem(ItemActivity.this);
                    viewPager = findViewById(R.id.view_pager);
                    dotsIndicator = findViewById(R.id.dots_indicator);

                    viewPager.setAdapter(viewAdapterItem);
                    dotsIndicator.setViewPager(viewPager);

                    lon_item = Double.parseDouble(itemLocationLong);
                    lat_item = Double.parseDouble(itemLocationLat);

                    Log.d(TAG, "lat = " + lat);
                    Log.d(TAG, "lon = " + lon);
                    Log.d(TAG, "lat_item = " + lat_item);
                    Log.d(TAG, "lon_item = " + lon_item);


                    //int distance = (int) distance(lat, lon, lat_item, lon_item, 'K');



                    //Log.d(TAG, "distance = " + distance);

                    _itemName = findViewById(R.id.item_name_caption);
                    _itemName.setText(itemName);

                    _itemPrice = findViewById(R.id.item_price_caption);
                    _itemPrice.setText("$ " + itemPrice);

                    _itemCategory = findViewById(R.id.item_category_caption);
                    _itemCategory.setText(itemCategory);

//                    _itemLocation = findViewById(R.id.item_location_caption);
//                    _itemLocation.setText("Distance to you: " + Integer.toString(distance) + " km");

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

    private static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d(TAG, "Lat: " + location.getLatitude() + " | Long: " + location.getLongitude());
        lat = location.getLatitude();
        lon = location.getLongitude();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            Log.d(TAG, "debug sign: " + addresses.size() + "---" + lat + "---" + lon);
            String cityName = addresses.get(0).getLocality();
            Log.d(TAG, "city name: " + cityName);

            Formatter formatter = new Formatter();
            formatter.format("%.5f", lat);
            formatter.format("%.5f", lon);


        } catch (IOException e) {
            e.printStackTrace();
        }

        int distance = (int) distance(lat, lon, lat_item, lon_item, 'K');

        Log.d(TAG, "distance = " + distance);

        _itemLocation = findViewById(R.id.item_location_caption);
        _itemLocation.setText("Distance to you: " + Integer.toString(distance) + " km");
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    private Bitmap base64ToBitmap (String img)
    {
        byte[] bytes = Base64.decode(img, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
