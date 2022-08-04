package com.cpen321.f5;



import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.util.Objects;


public class ItemListActivity extends AppCompatActivity {
    public static String ItemID;
    String[] IDsArray;
    List<String> cache;

    //parameters for searching button
    private static List<String> itemIDList;
    String searchKey;
    RequestQueue requestQueueForSearch;

    private final String TAG = "ItemListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        TextView search_hint = findViewById(R.id.search_hint);
        int size;
        String check_activity = getIntent().getStringExtra("search_interface");
        try{
            if(Objects.equals(check_activity, "1")){
                cache = new ArrayList<>(MainUI.getItemList());
                Log.d("check1",check_activity);
            }else if(Objects.equals(check_activity, "2")){
                cache = new ArrayList<>(ItemListActivity.getItemList());
                Log.d("check2",check_activity);
            }else if(Objects.equals(check_activity, "3")){
                cache = new ArrayList<>(ItemActivity.getItemList());
                Log.d("check3",check_activity);
            }else if(Objects.equals(check_activity, "5")) {
                cache = new ArrayList<>(ProfileActivity.getItemList());
                Log.d("check5purchased", check_activity);
            }else if(Objects.equals(check_activity, "6")) {
                cache = new ArrayList<>(ProfileActivity.getItemList());
                Log.d("check6biddingHighest", check_activity);
            }else{
                cache = new ArrayList<>(MainUI.getItemList());
                Log.d("check4",check_activity);
            }
            size = cache.size();
            if (size == 0){
                Toast.makeText(ItemListActivity.this, "No result found",Toast.LENGTH_SHORT).show();
                search_hint.setText("No result found, maybe you will be interested in:");
            }
        }catch (NullPointerException e_item_search){
            try{
                cache = new ArrayList<>(CategoryActivity.getItemList());
                size = cache.size();
                if (size == 0){
                    Toast.makeText(ItemListActivity.this, "No result found",Toast.LENGTH_SHORT).show();
                    search_hint.setText("No result found, maybe you will be interested in:");
                }
            }catch (NullPointerException e_category_search){
                cache = new ArrayList<>();
                search_hint.setText("No result found, maybe you will be interested in:");
                size = 0;
            }
        }



        Log.d(TAG, "size = " + size);
        //Log.d(TAG, "attribute = " + cache.get(0));


        IDsArray = new String[size];

        for (int i = 0; i < size; i++){
            IDsArray[i] = cache.get(i);
            Log.d(TAG, i + " => " + IDsArray[i]);
        }

//        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, IDsArray);
//
//        ListView listView = (ListView) findViewById(R.id.item_list);
//        listView.setAdapter(adapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ItemID = IDsArray[position];
//                Intent tmp = new Intent(ItemListActivity.this, ItemActivity.class);
//                startActivity(tmp);
//            }
//        });
        viewButtons();
        viewItems();

    }

    private void viewButtons() {
        TextView home_button = findViewById(R.id.home_itemList_button);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemListActivity.this, MainUI.class);
                startActivity(intent);
            }
        });
        TextView search_button = findViewById(R.id.search_itemList_button);
        requestQueueForSearch = Volley.newRequestQueue(this);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIDList = new ArrayList<>();
                EditText search_itemList_bar = findViewById(R.id.search_itemList_bar);
                searchKey = search_itemList_bar.getText().toString().trim();
                if( validCheck() ){
                    Log.d("SearchActivity", "search key = " + searchKey);
                    getDataForItemList(searchKey);
                }
            }
        });

        TextView chatList_button = findViewById(R.id.chatList_itemList_button);
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
        TextView profile_button = findViewById(R.id.profile_itemList_button);
        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemListActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        TextView wallet_button = findViewById(R.id.wallet_itemList_button);
        wallet_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemListActivity.this, WalletActivity.class);
                startActivity(intent);
            }
        });
    }

    private void viewItems() {

        RecyclerView item_recyclerview = findViewById(R.id.item_recyclerview);
        ItemListAdapter itemListAdapter = new ItemListAdapter(getLayoutInflater());
        item_recyclerview.setAdapter(itemListAdapter);
        item_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        for(int i = 0; i <= IDsArray.length - 1; i++){
            itemListAdapter.addList(IDsArray[i]);
        }
    }

    //these functions are for old search activity
    private boolean validCheck(){
        if (searchKey.equals("")){
            Toast.makeText(ItemListActivity.this, "Fail, please type in something", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void getDataForItemList(String searchKey)
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
                        //Log.d("SearchActivity", "ATTRIBUTE = " + itemIDList.get(0));
                    }
                    Intent ListUI = new Intent(ItemListActivity.this, ItemListActivity.class);
                    ListUI.putExtra("search_interface","2");
                    startActivity(ListUI);
                    //Toast.makeText(SearchActivity.this, "Successfully",Toast.LENGTH_SHORT).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(ItemListActivity.this,w.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ItemListActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueueForSearch.add(jsonArrayRequest);
    }

    public static List<String> getItemList(){
        return itemIDList;
    }


}