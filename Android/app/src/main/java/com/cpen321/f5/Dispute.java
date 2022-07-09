package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class Dispute extends AppCompatActivity
{
    private static final String TAG = "Dispute";

    private Button submitButton;

    EditText _reason;
    EditText _refund;
    EditText _admin;

    String reason;
    String refund;
    String admin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispute);

        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                _reason = findViewById(R.id.reason_caption);
                _refund = findViewById(R.id.refund_caption);
                _admin = findViewById(R.id.admin_caption);

                reason = _reason.getText().toString();
                refund = _refund.getText().toString();
                admin = _admin.getText().toString();


                Log.d(TAG, "REASON = " + reason);
                Log.d(TAG, "REFUND = " + refund);
                Log.d(TAG, "ADMIN = " + admin);

                //missing error checking pop out error box

                //NEED UPDATED URL
                String PROFILEURL = "http://20.106.78.177:8081/user/getprofile/" + MainActivity.idOfUser + "/";
                GETDISPUTEUPDATE(PROFILEURL);
            }
        });
    }

    private void GETDISPUTEUPDATE (final String URL)
    {
        JSONObject jsonObject = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(Dispute.this);

        StringRequest postRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(Dispute.this, "DATA SEND TO DB", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(Dispute.this, "FAILED TO SEND DATA: " + error, Toast.LENGTH_SHORT).show();
                    }
                })

        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                // FIELD UNDETERMINED, WAITING FOR ORDERS DB
                params.put("OrderID", MainActivity.idOfUser);
                params.put("Reason", reason);
                params.put("Refund", refund);
                params.put("Admin", admin);

                return params;
            }
        };
        queue.add(postRequest);
    }

}