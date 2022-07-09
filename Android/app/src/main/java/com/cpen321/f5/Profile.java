package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private static final String TAG = "Profile";

    private Button itemsButton;
    private Button disputeButton;
    private Button updateButton;

    String PROFILEURL = "http://20.106.78.177:8081/user/getprofile/" + MainActivity.idOfUser + "/";

    EditText _firstName;
    EditText _lastName;
    EditText _email;
    EditText _phone;
    EditText _unit;
    EditText _address1;
    EditText _address2;
    EditText _city;
    EditText _province;
    EditText _country;
    EditText _zip;

    String firstName;
    String lastName;
    String email;
    String phone;
    String unit;
    String address1;
    String address2;
    String city;
    String province;
    String country;
    String zip;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        GETUSERPROFILE();

        itemsButton = findViewById(R.id.items_button);
        itemsButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent itemsIntent = new Intent(Profile.this, ItemsList.class);
                startActivity(itemsIntent);
            }
        });

        disputeButton = findViewById(R.id.dispute_button);
        disputeButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent disputeIntent = new Intent(Profile.this, Dispute.class);
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

                //missing error checking pop out error box

                // NEED UPDATED URL
                //UPDATEUSERPROFILE();
            }
        });

    }

    private void GETUSERPROFILE ()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PROFILEURL, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                String output = "";

                try
                {
                    // Process for parsing JSON Object
                    JSONObject jsonResponse = new JSONObject(response);

                    firstName = jsonResponse.getString("FirstName");
                    lastName = jsonResponse.getString("LastName");
                    email = jsonResponse.getString("Email");
                    phone = jsonResponse.getString("Phone");
                    unit = jsonResponse.getString("Unit");
                    address1 = jsonResponse.getString("Address_line_1");
                    address2 = jsonResponse.getString("Address_line_2");
                    city = jsonResponse.getString("City");
                    province = jsonResponse.getString("Province");
                    country = jsonResponse.getString("Country");
                    zip = jsonResponse.getString("ZIP_code");

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
                }

                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }

        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void UPDATEUSERPROFILE ()
    {
        JSONObject jsonObject = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(Profile.this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, PROFILEURL,
            new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    Toast.makeText(Profile.this, "DATA SEND TO DB", Toast.LENGTH_SHORT).show();
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Toast.makeText(Profile.this, "FAILED TO SEND DATA: " + error, Toast.LENGTH_SHORT).show();
                }
            })

        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("UserID", MainActivity.idOfUser);
                params.put("Email", email);
                params.put("FirstName", firstName);
                params.put("LastName", lastName);
                params.put("Address1", address1);
                params.put("Address2", address2);
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
}