package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private static final String TAG = "Profile";

    private Button itemsButton;
    private Button disputeButton;
    private Button updateButton;

    EditText _firstName;
    EditText _lastName;
    EditText _email;
    EditText _phone;
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
    String address1;
    String address2;
    String city;
    String province;
    String country;
    String zip;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
                Log.d(TAG, "ADDRESS1 = " + address1);
                Log.d(TAG, "ADDRESS2 = " + address2);
                Log.d(TAG, "CITY = " + city);
                Log.d(TAG, "PROVINCE = " + province);
                Log.d(TAG, "COUNTRY = " + country);
                Log.d(TAG, "ZIP = " + zip);

                //missing error checking pop out error box

                // NEED UPDATED URL
                String PROFILEURL = "http://20.106.78.177:8081/user/getprofile/" + MainActivity.idOfUser + "/";
                GETUSERPROFILE(PROFILEURL);
            }
        });

    }

    private void GETUSERPROFILE (final String URL)
    {
        JSONObject jsonObject = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(Profile.this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
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