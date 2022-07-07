package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class Profile extends AppCompatActivity {

    private static final String TAG = "Profile";
    private Button itemsButton;
    private Button disputeButton;

    TextView firstName;
    TextView lastName;
    TextView email;
    TextView phone;
    TextView address1;
    TextView address2;
    TextView city;
    TextView province;
    TextView country;
    TextView zip;


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

        String PROFILEURL = "http://20.106.78.177:8081/user/getprofile/" + MainActivity.idOfUser + "/";
        GETUSERPROFILE(PROFILEURL);

    }

    private void GETUSERPROFILE (final String URL)
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.d(TAG, response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        // error
                        Log.d("Error.Response", String.valueOf(error));
                    }
                });

        queue.add(stringRequest);
    }
}