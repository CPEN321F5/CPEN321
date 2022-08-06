package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class DisputeActivity extends AppCompatActivity
{
    private static final String TAG = "DisputeActivity";

    RequestQueue requestQueue;

    EditText _orderItemID;
    EditText _reason;

    String orderItemID;
    String reason;
    String refund = "";
    String admin = "";

    TextView _buyerName;
    TextView _sellerName;
    TextView _itemName;
    Bitmap _itemBitmap;
    Drawable _itemDrawable;
    ImageView _disputeImage;

    String itemBuyerName;
    String itemSellerName;

    public static String adminConclusion;
    public static String ifDisputed;

    String GETPROFURL = "http://20.106.78.177:8081/user/getprofile/";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispute);

        Button checkButton;
        Button submitButton;

        AutoCompleteTextView dropdown1 = findViewById(R.id.tf_spinner_1);
        String[] items1 = getResources().getStringArray(R.array.trueFalse);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        dropdown1.setAdapter(adapter1);
        dropdown1.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                refund = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "REFUND = " + refund);
            }
        });

        AutoCompleteTextView dropdown2 = findViewById(R.id.tf_spinner_2);
        String[] items2 = getResources().getStringArray(R.array.trueFalse);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        dropdown2.setAdapter(adapter2);
        dropdown2.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                admin = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "ADMIN = " + admin);
            }
        });

        requestQueue = Volley.newRequestQueue(this);

        _itemName = findViewById(R.id.item_name);
        _itemName.setText(DisputeMainActivity.itemName);

        GETDISPUTEBUYER(DisputeMainActivity.buyerName);
        GETDISPUTESELLER(DisputeMainActivity.sellerName);

        _itemBitmap = base64ToBitmap(DisputeMainActivity.itemImage);
        _itemDrawable = new BitmapDrawable(getResources(), _itemBitmap);
        _disputeImage = findViewById(R.id.dispute_image);
        _disputeImage.setImageDrawable(_itemDrawable);

        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                _reason = findViewById(R.id.reason_caption);
                reason = _reason.getText().toString();

                Log.d(TAG, "REASON = " + reason);
                Log.d(TAG, "REFUND = " + refund);
                Log.d(TAG, "ADMIN = " + admin);

                //missing error checking pop out error box
                if (validCheck())
                {
                    GETDISPUTEUPDATE();
                }
            }
        });

        checkButton = findViewById(R.id.check_button);
        checkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GETDISPUTEADMIN();
            }
        });
    }

    private void GETDISPUTEUPDATE ()
    {
        String DISPUTEUPDATEURL = "http://20.106.78.177:8081/item/updateitem/";

        //JSONObject jsonObject = new JSONObject();
        RequestQueue queue = Volley.newRequestQueue(DisputeActivity.this);

        StringRequest postRequest = new StringRequest(Request.Method.PUT, DISPUTEUPDATEURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(DisputeActivity.this, "DATA SEND TO DB", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(DisputeActivity.this, "FAILED TO SEND DATA: " + error, Toast.LENGTH_SHORT).show();
                    }
                })

        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("ItemID", DisputeMainActivity.orderItemID);
                params.put("refundDescription", reason);
                params.put("refund", refund);
                params.put("needAdmin", admin);
                params.put("adminResponse", "Waiting For Admin To Resolve Dispute!");

                return params;
            }
        };
        queue.add(postRequest);
    }

    private boolean validCheck()
    {
        if (reason.equals(""))
        {
            Toast.makeText(DisputeActivity.this, "Reason Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (refund.equals(""))
        {
            Toast.makeText(DisputeActivity.this, "Refund Needed Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (admin.equals(""))
        {
            Toast.makeText(DisputeActivity.this, "Admin Needed Cannot Be Empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void GETDISPUTEADMIN ()
    {
        String GETDISPUTEADMINURL = "http://20.106.78.177:8081/item/getbyid/" + DisputeMainActivity.orderItemID + "/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETDISPUTEADMINURL, null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "attribute = " + response.toString());

                try
                {
                    adminConclusion = response.getString("adminResponse");
                    ifDisputed = response.getString("needAdmin");

                    if (ifDisputed.equals("false"))
                    {
                        Toast.makeText(DisputeActivity.this, "DISPUTE NOT YET FILED FOR THIS ITEM", Toast.LENGTH_LONG).show();
                    }

                    else
                    {
                        Toast.makeText(DisputeActivity.this, "CREDENTIALS RETRIEVED", Toast.LENGTH_LONG).show();

                        Intent disputeCheckIntent = new Intent(DisputeActivity.this, DisputeCheckActivity.class);
                        startActivity(disputeCheckIntent);
                    }
                }
                catch (Exception w)
                {
                    Toast.makeText(DisputeActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(DisputeActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void GETDISPUTEBUYER (String ID)
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETPROFURL+ ID + "/", null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "attribute = " + response.toString());

                try
                {
                    itemBuyerName = response.getString("FirstName") + " " + response.getString("LastName");

                    _buyerName = findViewById(R.id.buyer_caption);
                    _buyerName.setText(itemBuyerName);

                    Toast.makeText(DisputeActivity.this, "CREDENTIALS RETRIEVED", Toast.LENGTH_LONG).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(DisputeActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(DisputeActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void GETDISPUTESELLER (String ID)
    {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GETPROFURL+ ID + "/", null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                Log.d(TAG, "attribute = " + response.toString());

                try
                {
                    itemSellerName = response.getString("FirstName") + " " + response.getString("LastName");

                    _sellerName = findViewById(R.id.seller_caption);
                    _sellerName.setText(itemSellerName);

                    Toast.makeText(DisputeActivity.this, "CREDENTIALS RETRIEVED", Toast.LENGTH_LONG).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(DisputeActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(DisputeActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private Bitmap base64ToBitmap (String img)
    {
        byte[] bytes = Base64.decode(img, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}