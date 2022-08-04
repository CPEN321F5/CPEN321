package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
//import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

    RequestQueue requestQueue;

    String GETRESPONSEURL = "http://20.106.78.177:8081/item/getbyid/" + AdminDisputeActivity.AdminItemID + "/";
    String POSTRESPONSEURL = "http://20.106.78.177:8081/item/updateitem/";

    EditText _adminResponse;
    TextView _disputeReason;
    TextView _refundNeeded;

    String adminResponse;
    String disputeReason;
    String adminNeeded;
    String refundNeeded;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_response);

        Button updateButton;

        requestQueue = Volley.newRequestQueue(this);

        Log.d(TAG, GETRESPONSEURL);
        GETRESPSONSE();

        AutoCompleteTextView dropdownAdmin = findViewById(R.id.admin_spinner);
        String[] itemsAdmin = getResources().getStringArray(R.array.trueFalseAdmin);
        ArrayAdapter<String> adapterAdmin = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsAdmin);
        dropdownAdmin.setAdapter(adapterAdmin);
        dropdownAdmin.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                adminNeeded = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "ADMIN NEEDED = " + adminNeeded);
            }
        });

        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                _adminResponse = findViewById(R.id.response_caption);
                adminResponse = _adminResponse.getText().toString();

                Log.d(TAG, "RESPONSE = " + adminResponse);
                Log.d(TAG, "ADMIN NEEDED = " + adminNeeded);

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
                    disputeReason = response.getString("refundDescription");
                    adminNeeded = response.getString("needAdmin");
                    refundNeeded = response.getString("refund");

                    _adminResponse = findViewById(R.id.response_caption);
                    _adminResponse.setText(adminResponse);

                    _disputeReason = findViewById(R.id.reason_caption);
                    _disputeReason.setText("Reason For Dispute: " + disputeReason);

                    _refundNeeded = findViewById(R.id.refund_caption);
                    _refundNeeded.setText("Refund Required: " + refundNeeded);

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
                params.put("needAdmin", adminNeeded);

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