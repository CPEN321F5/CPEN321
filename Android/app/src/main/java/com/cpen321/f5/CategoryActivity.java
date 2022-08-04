package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class CategoryActivity extends AppCompatActivity {
    private static ArrayList<String> itemIDList;

    RequestQueue requestQueue;
    String[] categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categories = getResources().getStringArray(R.array.categories);
        requestQueue = Volley.newRequestQueue(this);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, categories);

        ListView listView = (ListView) findViewById(R.id.category_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemIDList = new ArrayList<>();
                String category = categories[position];

                getDataForItemList(category);
//                Intent tmp = new Intent(ItemListActivity.this, ItemActivity.class);
//                startActivity(tmp);
            }
        });

        TextView chatList_button = findViewById(R.id.chat_category_button);
        String chatList_init_url = "http://20.106.78.177:8081/chat/getconversationlist/";
        chatList_button.setOnClickListener(v ->{
            String chatList_url = chatList_init_url + MainActivity.idOfUser;
            RequestQueue queue = Volley.newRequestQueue(v.getContext());
            Intent intent = new Intent(this, ChatlistsActivity.class);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, chatList_url, null, new com.android.volley.Response.Listener<JSONArray>() {
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
        TextView profile_button = findViewById(R.id.profile_category_button);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        TextView Home_button = findViewById(R.id.home_category_button);
        Home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryActivity.this, WalletActivity.class);
                startActivity(intent);
            }
        });


    }

    private void getDataForItemList(String searchKey)
    {
        String url = getString(R.string.url_item_get_by_categories) + searchKey;

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
                    Intent ListUI = new Intent(CategoryActivity.this, ItemListActivity.class);
                    startActivity(ListUI);
                    //Toast.makeText(SearchActivity.this, "Successfully",Toast.LENGTH_SHORT).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(CategoryActivity.this,w.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CategoryActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public static List<String> getItemList(){
        return itemIDList;
    }
}