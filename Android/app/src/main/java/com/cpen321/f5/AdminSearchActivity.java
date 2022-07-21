package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminSearchActivity extends AppCompatActivity
{
    private static final String TAG = "AdminSearchActivity";

    String searchKey;
    RequestQueue requestQueue;
    private static List<String> adminItemIDList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_search);

        Button searchButton;

        requestQueue = Volley.newRequestQueue(this);

        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                adminItemIDList = new ArrayList<>();
                searchKey = ((EditText)findViewById(R.id.search_bar)).getText().toString().trim();

                if (validCheck())
                {
                    Log.d(TAG, "search key = " + searchKey);

                    getdata(searchKey);
                }
            }
        });
    }

    private boolean validCheck()
    {
        if (searchKey.equals(""))
        {
            Toast.makeText(AdminSearchActivity.this, "Fail, please type in something", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void getdata(String searchKey)
    {
        String url = "http://20.106.78.177:8081/item/search/" + searchKey;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                JSONArray jsonArray = response;
                try
                {
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String itemID = jsonObject.getString("ItemID");

                        adminItemIDList.add(itemID);
                        //Log.d(TAG, "ATTRIBUTE = " + itemIDList.get(0));
                    }
                    Intent ListUI = new Intent(AdminSearchActivity.this, AdminListActivity.class);
                    startActivity(ListUI);
                    //Toast.makeText(SearchActivity.this, "Successfully",Toast.LENGTH_SHORT).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(AdminSearchActivity.this,w.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(AdminSearchActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public static List<String> getAdminItemList()
    {
        return adminItemIDList;
    }
}