package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

        viewButtons();

    }

    private void viewButtons() {
        TextView home_button = findViewById(R.id.home_myItemList_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyItemListActivity.this, MainUI.class);
                startActivity(intent);
            }
        });

        TextView chatList_button = findViewById(R.id.chatList_myItemList_button);
        String chatList_init_url = "http://20.106.78.177:8081/chat/getconversationlist/";
        chatList_button.setOnClickListener(v ->{
            String chatList_url = chatList_init_url + MainActivity.idOfUser;
            RequestQueue queue = Volley.newRequestQueue(v.getContext());
            Intent intent = new Intent(this, ChatlistsActivity.class);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, chatList_url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    Log.d("TEST1", response.toString());
                    intent.putExtra("conversationsList", response.toString());
                    intent.putExtra("myID", MainActivity.idOfUser);
                    startActivity(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TEST1", chatList_url);
                }
            });
            queue.add(jsonArrayRequest);
        });
        TextView profile_button = findViewById(R.id.profile_myItemList_button);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyItemListActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        TextView post_button = findViewById(R.id.post_myItemList_button);
        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyItemListActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });
    }

    private void GETMYITEMS(String searchKey)
    {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, GETMYITEMSURL, null, new Response.Listener<JSONArray>()
        {
            @Override
            public void onResponse(JSONArray response)
            {
                JSONArray jsonArray = response;
                Toast.makeText(MyItemListActivity.this,"CURRENT USER ID: " + searchKey,Toast.LENGTH_SHORT).show();
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


        RecyclerView item_recyclerview = findViewById(R.id.myitem_recyclerview);
        MyItemListAdapter myItemListAdapter = new MyItemListAdapter(getLayoutInflater());
        item_recyclerview.setAdapter(myItemListAdapter);
        item_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        for(int i = 0; i <= IDsArray.length - 1; i++){
            myItemListAdapter.addList(IDsArray[i]);
        }
    }
}