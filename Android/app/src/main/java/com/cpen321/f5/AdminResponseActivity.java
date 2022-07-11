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

public class AdminResponseActivity extends AppCompatActivity
{
    private static final String TAG = "AdminResponseActivity";

    private Button updateButton;

    RequestQueue requestQueue;

    String GETRESPONSEURL = "http://20.106.78.177:8081/item/getbyid/" + AdminDisputeActivity.AdminItemID + "/";
    String POSTRESPONSEURL = "http://20.106.78.177:8081/item/updateitem/";

    EditText _adminResponse;

    String adminResponse;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_response);

        requestQueue = Volley.newRequestQueue(this);

        Log.d(TAG, GETRESPONSEURL);
        GETRESPSONSE();

        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                _adminResponse = findViewById(R.id.response_caption);

                adminResponse = _adminResponse.getText().toString();

                Log.d(TAG, "RESPONSE = " + adminResponse);

                if (validCheck())
                {
                    UPDATERESPONSE();
                }
            }
        });
    }

    private void GETRESPSONSE ()
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETRESPONSEURL, null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, response.toString());

                try
                {
                    adminResponse = response.getString("adminResponse");


                    _adminResponse = findViewById(R.id.response_caption);
                    _adminResponse.setText(adminResponse);

                    Toast.makeText(AdminResponseActivity.this, "CREDENTIALS RETRIEVED", Toast.LENGTH_LONG).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(AdminResponseActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(AdminResponseActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void UPDATERESPONSE ()
    {
        RequestQueue queue = Volley.newRequestQueue(AdminResponseActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.PUT, POSTRESPONSEURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(AdminResponseActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(AdminResponseActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("ItemID", AdminDisputeActivity.AdminItemID);

                params.put("adminResponse", adminResponse);

                return params;
            }
        };
        queue.add(postRequest);
    }

    private boolean validCheck()
    {
        if (adminResponse.equals(""))
        {
            Toast.makeText(AdminResponseActivity.this, "Response Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}