package com.cpen321.f5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ItemActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = "ItemActivity";

    public static String stepPrice;
    public static String itemPrice;
    public static String highestPriceHolder;
    public static String expireTime;
    public static String sellerID;

    private static List<String> itemIDList;
    String searchKey;

    RequestQueue requestQueueForSearch;
    RequestQueue requestQueue;

    TextView _itemName;
    TextView _itemPrice;
    TextView _itemCategory;
    TextView _itemDescription;
    TextView _itemLocation;
    TextView _itemNumber;
    private ImageView addButton;
    private ImageView subButton;
    private TextView newPrice;

    String itemName;
    String itemCategory;
    String itemDescription;
    String itemLocationLong;
    String itemLocationLat;
    String itemNumber;

    public static String itemID;
    String tmpID;

    String GETITEMURL = "http://20.106.78.177:8081/item/getbyid/";;

    LocationManager locationManager;
    private double lat;
    private double lon;
    private double lat_item;
    private double lon_item;

    int tmpPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        View contact_seller_Button;
        View bidButton;

        tmpID = getIntent().getStringExtra("itemID");
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






        View home_button = findViewById(R.id.home_item_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemActivity.this, MainUI.class);
                startActivity(intent);
            }
        });
        View search_button = findViewById(R.id.search_item_button);
        requestQueueForSearch = Volley.newRequestQueue(this);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIDList = new ArrayList<>();
                EditText search_bar = findViewById(R.id.search_item_bar);
                searchKey = search_bar.getText().toString().trim();
                if( validCheck() ){
                    Log.d("SearchActivity", "search key = " + searchKey);
                    getDataForItemList(searchKey);
                }
            }
        });

        String chat_init_url = "http://20.106.78.177:8081/chat/initconversation/";
        String myID = MainActivity.idOfUser;

        bidButton = findViewById(R.id.bid_button);
        bidButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {

                //new code
                if (tmpPrice > Integer.parseInt(itemPrice)){
                    updPrice();
                }else{
                    Toast.makeText(ItemActivity.this, "bid price should be higher", Toast.LENGTH_SHORT).show();
                }
            }
        });

        contact_seller_Button = findViewById(R.id.contact_seller_button);
        contact_seller_Button.setOnClickListener(new View.OnClickListener() {
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

        addButton = findViewById(R.id.item_price_up_button);
        subButton = findViewById(R.id.item_price_down_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int priceChange = Integer.parseInt(stepPrice);
                tmpPrice += priceChange;
                newPrice.setText(Integer.toString(tmpPrice));
            }
        });

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int priceChange = Integer.parseInt(stepPrice);
                tmpPrice -= priceChange;
                if (tmpPrice < Integer.parseInt(itemPrice)){
                    tmpPrice = Integer.parseInt(itemPrice);
                }
                Toast.makeText(ItemActivity.this, "bid price should be higher", Toast.LENGTH_SHORT).show();
                newPrice.setText(Integer.toString(tmpPrice));
            }
        });
    }

    private void GETITEM ()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETITEMURL+ tmpID + "/", null, new Response.Listener<JSONObject>()
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
                    newPrice = findViewById(R.id.upcoming_price);
                    newPrice.setText(itemPrice);
                    tmpPrice = Integer.parseInt(itemPrice);


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


    //these functions are for old search activity
    private boolean validCheck(){
        if (searchKey.equals("")){
            Toast.makeText(ItemActivity.this, "Fail, please type in something", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getDataForItemList(String searchKey)
    {
        String url = getString(R.string.url_searchResult) + searchKey;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONArray jsonArray = response;
                try {
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String itemID = jsonObject.getString("ItemID");

                        itemIDList.add(itemID);
                        Log.d("SearchActivity", "ATTRIBUTE = " + itemIDList.get(0));
                    }
                    Intent ListUI = new Intent(ItemActivity.this, ItemListActivity.class);
                    ListUI.putExtra("search_interface","3");
                    startActivity(ListUI);
                    //Toast.makeText(SearchActivity.this, "Successfully",Toast.LENGTH_SHORT).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(ItemActivity.this,w.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ItemActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueueForSearch.add(jsonArrayRequest);
    }

    private void updPrice() {
        RequestQueue queue = Volley.newRequestQueue(ItemActivity.this);
        String url = getString(R.string.url_item_put);

        StringRequest postRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(ItemActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(ItemActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("ItemID", itemNumber);
                params.put("currentPrice", Integer.toString(tmpPrice));
                params.put("currentPriceHolder", MainActivity.idOfUser);

                return params;
            }
        };
        queue.add(postRequest);
    }


    public static List<String> getItemList(){
        return itemIDList;
    }

}
