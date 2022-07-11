package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private Button postButton;
    private Button itemsButton;
    private Button disputeButton;
    private Button updateButton;

    RequestQueue requestQueue;

    String GETPROFILEURL = "http://20.106.78.177:8081/user/getprofile/" + MainActivity.idOfUser + "/";
    String POSTPROFILEURL = "http://20.106.78.177:8081/user/updateprofile/";

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

    TextView name;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        requestQueue = Volley.newRequestQueue(this);

        Log.d(TAG, GETPROFILEURL);
        GETUSERPROFILE();

        postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent postIntent = new Intent(ProfileActivity.this, PostActivity.class);
                startActivity(postIntent);
            }
        });

        itemsButton = findViewById(R.id.items_button);
        itemsButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent itemsIntent = new Intent(ProfileActivity.this, MyItemListActivity.class);
                startActivity(itemsIntent);
            }
        });

        disputeButton = findViewById(R.id.dispute_button);
        disputeButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent disputeIntent = new Intent(ProfileActivity.this, DisputeActivity.class);
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
                }
            }
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
                }
                catch (Exception w)
                {
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
        if (email.equals(""))
        {
            Toast.makeText(ProfileActivity.this, "Email Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (firstName.equals(""))
        {
            Toast.makeText(ProfileActivity.this, "First Name Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (lastName.equals(""))
        {
            Toast.makeText(ProfileActivity.this, "Last Name Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (address1.equals(""))
        {
            Toast.makeText(ProfileActivity.this, "Address Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (city.equals(""))
        {
            Toast.makeText(ProfileActivity.this, "City Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (country.equals(""))
        {
            Toast.makeText(ProfileActivity.this, "Country Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (zip.equals(""))
        {
            Toast.makeText(ProfileActivity.this, "ZIP Code Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (phone.equals(""))
        {
            Toast.makeText(ProfileActivity.this, "Phone Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}