package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.util.TimeUnit;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    final String TAG = "SearchActivity";
    String searchKey;
    private Button searchButton;

    RequestQueue requestQueue;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        itemList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchKey = ((EditText)findViewById(R.id.search_bar)).getText().toString().trim();
                if( validCheck() ){

                    Log.d(TAG, "search key = " + searchKey);


                    getdata(searchKey);
                    //TODO: change MainUI to the page after search
                    //TODO: wait to connect with Domi
//                    Intent MainUI = new Intent(SearchActivity.this, MainUI.class);
//                    startActivity(MainUI);
                }

            }
        });

    }



    private boolean validCheck(){
        if (searchKey.equals("")){
            Toast.makeText(SearchActivity.this, "Fail, please type in something", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void getdata(String searchKey)
    {
        String url = getString(R.string.url_searchResult) + searchKey;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONArray jsonArray = response;
                try {
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String ttl = jsonObject.getString("name");
                        String dsrp = jsonObject.getString("description");
                        String slID = jsonObject.getString("sellerID");
                        String lct = jsonObject.getString("location");
                        String sttPrc = jsonObject.getString("startPrice");
                        String dpst = jsonObject.getString("deposit");
                        String stpPrc = jsonObject.getString("stepPrice");
                        String pstTm = jsonObject.getString("postTime");
                        String tmLst = jsonObject.getString("timeLast");
                        String tmExpr = jsonObject.getString("timeExpire");


                        Log.d(TAG, "name = " + ttl);
                        Log.d(TAG, "description = " + dsrp);
                        Log.d(TAG, "sellerID = " + slID);
                        Log.d(TAG, "location = " + lct);
                        Log.d(TAG, "startPrice = " + sttPrc);
                        Log.d(TAG, "deposit = " + dpst);
                        Log.d(TAG, "stepPrice = " + stpPrc);
                        Log.d(TAG, "postTime = " + pstTm);
                        Log.d(TAG, "timeLast = " + tmLst);
                        Log.d(TAG, "timeExpire = " + tmExpr);


                        itemList.add(new Item(ttl, dsrp, slID, lct, sttPrc, dpst, stpPrc, pstTm, tmLst, tmExpr));
                    }
                    Toast.makeText(SearchActivity.this, "Successfully",Toast.LENGTH_LONG).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(SearchActivity.this,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }


}