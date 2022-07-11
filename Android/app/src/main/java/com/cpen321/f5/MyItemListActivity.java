package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class MyItemListActivity extends AppCompatActivity
{
    private static final String TAG = "MyItemListActivity";

    public static String MyItemID;

    String searchKey = MainActivity.idOfUser;
    String GETMYITEMSURL = "http://20.106.78.177:8081/item/getbycond/seller/" + searchKey;

    RequestQueue requestQueue;
    private static List<String> itemIDList;

    String[] IDsArray;
    List<String> cache;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item_list);

        requestQueue = Volley.newRequestQueue(this);

        itemIDList = new ArrayList<>();

        Log.d(TAG, "search key = " + searchKey);

        GETMYITEMS(searchKey);

    }

    private void GETMYITEMS(String searchKey)
    {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GETMYITEMSURL, null, new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                JSONArray jsonArray = response;
                try
                {
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String itemID = jsonObject.getString("ItemID");

                        itemIDList.add(itemID);
                    }

                    DISPLAYMYITEMS();
                }
                catch (Exception w)
                {
                    Toast.makeText(MyItemListActivity.this,w.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(MyItemListActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public static List<String> GETMYITEMSLIST()
    {
        return itemIDList;
    }

    private void DISPLAYMYITEMS()
    {
        cache = MyItemListActivity.GETMYITEMSLIST();
        int size = cache.size();
        Log.d(TAG, "size = " + size);

        if (size == 0)
        {
            Toast.makeText(MyItemListActivity.this, "No result found",Toast.LENGTH_SHORT).show();
        }

        IDsArray = new String[size];
        for (int i = 0; i < size; i++)
        {
            IDsArray[i] = cache.get(i);
            Log.d(TAG, i + " => " + IDsArray[i]);
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, IDsArray);

        ListView listView = findViewById(R.id.item_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                MyItemID = IDsArray[position];
                Intent tmp = new Intent(MyItemListActivity.this, MyItemActivity.class);
                startActivity(tmp);
            }
        });
    }
}