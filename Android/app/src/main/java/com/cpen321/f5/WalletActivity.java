package com.cpen321.f5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import android.annotation.SuppressLint;
import android.app.Application;
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
import com.cpen321.f5.util.PaymentsUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WalletActivity extends AppCompatActivity {

    RequestQueue requestQueue;

    final static String TAG = "WalletActivity";

    private Button loadButton;
    private EditText addedFundText;
    private TextView balance;
    private int balanceAmount = 0;
    private int addedFund = 0;

    String GETPROFILEURL = "http://20.106.78.177:8081/user/getprofile/" + MainActivity.idOfUser + "/";
    String POSTPROFILEURL = "http://20.106.78.177:8081/user/updateprofile/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        requestQueue = Volley.newRequestQueue(this);
        GetBalance();

        balance = findViewById(R.id.wallet_balance);
        addedFundText = findViewById(R.id.wallet_amount_to_load);
        loadButton = findViewById(R.id.load_wallet_button);


        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validCheck()){
                    Toast.makeText(WalletActivity.this, "add fund is not set yet",Toast.LENGTH_SHORT).show();
                } else{
                    addedFund = Integer.parseInt(addedFundText.getText().toString().trim());
                    ReloadBalance();
                    balance.setText(Integer.toString(balanceAmount + addedFund));
                    Toast.makeText(WalletActivity.this, "fund is added successfully",Toast.LENGTH_SHORT).show();
                    addedFundText.setText("");
                }
            }
        });

    }


    private void GetBalance ()
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
                    balanceAmount = Integer.parseInt(response.getString("balance"));
                    balance.setText(Integer.toString(balanceAmount));
                    Log.d(TAG, "balanceAmount = " + balanceAmount);
                }
                catch (Exception w)
                {
                    Toast.makeText(WalletActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(WalletActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void ReloadBalance ()
    {
        RequestQueue queue = Volley.newRequestQueue(WalletActivity.this);
        StringRequest postRequest = new StringRequest(Request.Method.PUT, POSTPROFILEURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Toast.makeText(WalletActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(WalletActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                int newBalance = balanceAmount + addedFund;
                Log.d(TAG, "new balanceAmount = " + balanceAmount);

                params.put("UserID", MainActivity.idOfUser);
                params.put("balance", Integer.toString(newBalance));
                return params;
            }
        };
        queue.add(postRequest);
    }

    private boolean validCheck(){
        if (addedFundText.equals("")){
            return false;
        }
        return true;
    }
}