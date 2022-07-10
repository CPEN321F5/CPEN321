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

public class SearchActivity extends AppCompatActivity {
    final String TAG = "SearchActivity";
    String searchKey;
    private Button searchButton;
    RequestQueue requestQueue;
    private static List<String> itemIDList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        requestQueue = Volley.newRequestQueue(this);

        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIDList = new ArrayList<>();
                searchKey = ((EditText)findViewById(R.id.search_bar)).getText().toString().trim();
                if( validCheck() ){
                    Log.d(TAG, "search key = " + searchKey);

                    getdata(searchKey);


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
                        String itemID = jsonObject.getString("ItemID");

                        itemIDList.add(itemID);
                        //Log.d(TAG, "ATTRIBUTE = " + itemIDList.get(0));
                    }
                    Intent ListUI = new Intent(SearchActivity.this, ItemListActivity.class);
                    startActivity(ListUI);
                    //Toast.makeText(SearchActivity.this, "Successfully",Toast.LENGTH_SHORT).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(SearchActivity.this,w.getMessage(),Toast.LENGTH_SHORT).show();
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

    public static List<String> getItemList(){
        return itemIDList;
    }

}
