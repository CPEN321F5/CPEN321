package com.cpen321.f5;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    RequestQueue requestQueue;

    String GETPROFILEURL = "http://20.106.78.177:8081/user/getprofile/" + MainActivity.idOfUser + "/";
    String POSTPROFILEURL = "http://20.106.78.177:8081/user/updateprofile/";

    private EditText _firstName;
    private EditText _lastName;
    private EditText _email;
    private EditText _phone;
    private EditText _unit;
    private EditText _address1;
    private EditText _address2;
    private EditText _city;
    private EditText _province;
    private EditText _country;
    private EditText _zip;

    private ImageView profileImgVw;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String unit;
    private String address1;
    private String address2;
    private String city;
    private String province;
    private String country;
    private String zip;
    private String profileImg;

    TextView name;
    private static List<String> itemIDList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        CardView itemsButton;
        TextView postButton;
        TextView disputeButton;
        Button updateButton;

        requestQueue = Volley.newRequestQueue(this);

        Log.d(TAG, GETPROFILEURL);
        profileImgVw = findViewById(R.id.profile_image);
        GETUSERPROFILE();


        postButton = findViewById(R.id.profile_post_button);
        postButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent postIntent = new Intent(ProfileActivity.this, PostActivity.class);
                startActivity(postIntent);
            }
        });

        itemsButton = findViewById(R.id.profile_item_post);
        itemsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent itemsIntent = new Intent(ProfileActivity.this, MyItemListActivity.class);
                startActivity(itemsIntent);
            }
        });

        disputeButton = findViewById(R.id.profile_dispute_button);
        disputeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent disputeIntent = new Intent(ProfileActivity.this, DisputeMainActivity.class);
                startActivity(disputeIntent);
            }
        });

        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _firstName = findViewById(R.id.first_name_caption);
                _lastName = findViewById(R.id.last_name_caption);
                _email = findViewById(R.id.email_caption);
                _phone = findViewById(R.id.phone_caption);
                _unit = findViewById(R.id.unit_caption);
                _address1 = findViewById(R.id.address1_caption);
                _address2 = findViewById(R.id.address2_caption);
                _city = findViewById(R.id.city_caption);
                _province = findViewById(R.id.province_caption);
                _country = findViewById(R.id.country_caption);
                _zip = findViewById(R.id.zip_caption);


                firstName = _firstName.getText().toString();
                lastName = _lastName.getText().toString();
                email = _email.getText().toString();
                phone = _phone.getText().toString();
                unit = _unit.getText().toString();
                address1 = _address1.getText().toString();
                address2 = _address2.getText().toString();
                city = _city.getText().toString();
                province = _province.getText().toString();
                country = _country.getText().toString();
                zip = _zip.getText().toString();

                Log.d(TAG, "FIRSTNAME = " + firstName);
                Log.d(TAG, "LASTNAME = " + lastName);
                Log.d(TAG, "EMAIL = " + email);
                Log.d(TAG, "PHONE = " + phone);
                Log.d(TAG, "UNIT = " + unit);
                Log.d(TAG, "ADDRESS1 = " + address1);
                Log.d(TAG, "ADDRESS2 = " + address2);
                Log.d(TAG, "CITY = " + city);
                Log.d(TAG, "PROVINCE = " + province);
                Log.d(TAG, "COUNTRY = " + country);
                Log.d(TAG, "ZIP = " + zip);

                if (validCheck())
                {
                    UPDATEUSERPROFILE();
                    Log.d(TAG, "line186" + profileImg);
                }
            }
        });

        CardView highestBiddingButton = findViewById(R.id.profile_item_bid_highest);
        highestBiddingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIDList = new ArrayList<>();
                getDataForBid(MainActivity.idOfUser);
            }
        });

        CardView itemPurchasedButton = findViewById(R.id.profile_item_purchased);
        itemPurchasedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIDList = new ArrayList<>();
                getDataForPurchased(MainActivity.idOfUser);
            }
        });



        profileImgVw.setOnClickListener(v ->{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            requestForAlbum.launch(intent);
            Toast.makeText(ProfileActivity.this, "Trying to open album", Toast.LENGTH_SHORT).show();
            UPDATEUSERPROFILE();
            Log.d(TAG, "NEW IMAGE" + profileImg);
        });
    }

    private void GETUSERPROFILE ()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETPROFILEURL, null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, response.toString());

                try
                {

                    try{
                        profileImg = response.getString("ProfileImage");
                    }catch (Exception e){
                        profileImg = "";
                    }

                    firstName = response.getString("FirstName");
                    lastName = response.getString("LastName");
                    email = response.getString("Email");
                    phone = response.getString("Phone");
                    unit = response.getString("Unit");
                    address1 = response.getString("Address_line_1");
                    address2 = response.getString("Address_line_2");
                    city = response.getString("City");
                    province = response.getString("Province");
                    country = response.getString("Country");
                    zip = response.getString("ZIP_code");

                    _firstName = findViewById(R.id.first_name_caption);
                    _firstName.setText(firstName);

                    _lastName = findViewById(R.id.last_name_caption);
                    _lastName.setText(lastName);

                    _email = findViewById(R.id.email_caption);
                    _email.setText(email);

                    _phone = findViewById(R.id.phone_caption);
                    _phone.setText(phone);

                    _unit = findViewById(R.id.unit_caption);
                    _unit.setText(unit);

                    _address1 = findViewById(R.id.address1_caption);
                    _address1.setText(address1);

                    _address2 = findViewById(R.id.address2_caption);
                    _address2.setText(address2);

                    _city = findViewById(R.id.city_caption);
                    _city.setText(city);

                    _province = findViewById(R.id.province_caption);
                    _province.setText(province);

                    _country = findViewById(R.id.country_caption);
                    _country.setText(country);

                    _zip = findViewById(R.id.zip_caption);
                    _zip.setText(zip);

                    Toast.makeText(ProfileActivity.this, "CREDENTIALS RETRIEVED", Toast.LENGTH_LONG).show();

                    name = findViewById(R.id.name);
                    name.setText(firstName + " " + lastName);

                    Log.d(TAG, "line 277" + profileImg);
                    refreshImg();
                }
                catch (Exception w)
                {
                    Log.d(TAG, "line 282");
                    Toast.makeText(ProfileActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(ProfileActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void UPDATEUSERPROFILE ()
    {
        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.PUT, POSTPROFILEURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(ProfileActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(ProfileActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("UserID", MainActivity.idOfUser);
                params.put("ProfileImage", profileImg);
                params.put("Email", email);
                params.put("FirstName", firstName);
                params.put("LastName", lastName);
                params.put("Unit", unit);
                params.put("Address_line_1", address1);
                params.put("Address_line_2", address2);
                params.put("City", city);
                params.put("Province", province);
                params.put("Country", country);
                params.put("ZIP_code", zip);
                params.put("Phone", phone);

                return params;
            }
        };
        queue.add(postRequest);
    }



    private boolean validCheck()
    {
        if (!isValidEmailAddress(email) && email != ""){
            Toast.makeText(ProfileActivity.this, "Email Is Not Valid", Toast.LENGTH_SHORT).show();
            return false;
        }else if (email.equals("") && phone.equals("")){
            Toast.makeText(ProfileActivity.this, "Email Or Phone Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if (firstName.equals("") || lastName.equals("")){
            Toast.makeText(ProfileActivity.this, "Name Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if (address1.equals("")){
            Toast.makeText(ProfileActivity.this, "Address Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if (city.equals("") || country.equals("")){
            Toast.makeText(ProfileActivity.this, "City Or Country Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if (zip.equals("")){
            Toast.makeText(ProfileActivity.this, "ZIP Code Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void getDataForBid(String buyerID)
    {
        String url = "http://20.106.78.177:8081/item/getbycond/bidder/" + buyerID;

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
                        //Log.d("SearchActivity", "ATTRIBUTE = " + itemIDList.get(0));
                    }
                    Intent ListUI = new Intent(ProfileActivity.this, ItemListActivity.class);
                    ListUI.putExtra("search_interface","6");
                    startActivity(ListUI);
                    //Toast.makeText(SearchActivity.this, "Successfully",Toast.LENGTH_SHORT).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(ProfileActivity.this,w.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void getDataForPurchased(String buyerID)
    {
        String url = "http://20.106.78.177:8081/item/getbycond/buyer/" + buyerID;

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
                        //Log.d("SearchActivity", "ATTRIBUTE = " + itemIDList.get(0));
                    }
                    Log.d(TAG, "Error1");
                    Intent ListUI = new Intent(ProfileActivity.this, ItemListActivity.class);
                    ListUI.putExtra("search_interface","5");
                    startActivity(ListUI);
                    //Toast.makeText(SearchActivity.this, "Successfully",Toast.LENGTH_SHORT).show();
                }
                catch (Exception w)
                {
                    Log.d(TAG, "Error0");
                    Toast.makeText(ProfileActivity.this,w.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProfileActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private final ActivityResultLauncher<Intent> requestForAlbum =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    if(result.getData() != null && result.getResultCode() == RESULT_OK){
                        //Decode image size
                        BitmapFactory.Options o = new BitmapFactory.Options();
                        o.inJustDecodeBounds = true;
                        try{
                            InputStream Is = getContentResolver().openInputStream(result.getData().getData());
                            BitmapFactory.decodeStream(Is, null, o);
                            Is.close();
                        }catch (FileNotFoundException error){
                            Toast.makeText(ProfileActivity.this, "error", Toast.LENGTH_SHORT).show();
                            error.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int IMAGE_MAX_SIZE = 700;
                        int scale = 1;
                        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
                        }

                        //Decode with inSampleSize
                        BitmapFactory.Options o2 = new BitmapFactory.Options();
                        o2.inSampleSize = scale;

                        try {
                            InputStream Is2 = getContentResolver().openInputStream(result.getData().getData());
                            Bitmap image = BitmapFactory.decodeStream(Is2, null, o2);
                            Is2.close();
                            profileImg = bitmapToBase64(image);

                            refreshImg();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private String bitmapToBase64(Bitmap image) {
        ByteArrayOutputStream Os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,50, Os);
        String b64String = Base64.encodeToString(Os.toByteArray(), Base64.DEFAULT);

        return b64String;
    }

    private Bitmap base64ToBitmap (String img)
    {
        byte[] bytes = Base64.decode(img, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void refreshImg(){
        ImageView img = findViewById(R.id.profile_image);
        if (profileImg.equals("")){

        }else{
            img.setImageBitmap(base64ToBitmap(profileImg));
        }
    }

    private boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static List<String> getItemList(){
        return itemIDList;
    }
}
