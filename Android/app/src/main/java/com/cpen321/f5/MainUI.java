package com.cpen321.f5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
//import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainUI extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    String searchKey;
    RequestQueue requestQueueForSearch;
    private static List<String> itemIDList;
    private final String TAG = "MainUI";
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);

        //TextView topBarText = findViewById(R.id.main_ui_top_bar);
        //topBarText.setText(getTimeFromAndroid() + ", " + MainActivity.nameOfUser.split(" ")[0]);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        setButton();
        setCategories();
        viewRecommendation();
    }

    private void setCategories() {

        View categoryButton = findViewById(R.id.main_ui_more);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryList = new Intent(MainUI.this, CategoryActivity.class);
                startActivity(categoryList);
            }
        });

        LinearLayout clothing_category = findViewById(R.id.clothes_button);
        clothing_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIDList = new ArrayList<>();
                getDataForCategory("Clothing");
            }
        });
        LinearLayout books_category = findViewById(R.id.book_button);
        books_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIDList = new ArrayList<>();
                getDataForCategory("Books");
            }
        });
        LinearLayout electronics_category = findViewById(R.id.electronis_button);
        electronics_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIDList = new ArrayList<>();
                getDataForCategory("Electronics");
            }
        });
        LinearLayout furniture_category = findViewById(R.id.furniture_button);
        furniture_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIDList = new ArrayList<>();
                getDataForCategory("Furniture");
            }
        });

    }

    private void setButton() {
        //initial commit
        //url strings for chat list activity
        String chatList_init_url = "http://20.106.78.177:8081/chat/getconversationlist/";
        String myID = MainActivity.idOfUser;
        String chatList_url = chatList_init_url + myID;
        requestQueueForSearch= Volley.newRequestQueue(this);


        View searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIDList = new ArrayList<>();
                EditText keywordSearch = findViewById(R.id.search_bar);
                searchKey = keywordSearch.getText().toString().trim();
                if( validCheck() ){
                    Log.d("SearchActivity", "search key = " + searchKey);
                    getDataForItemList(searchKey);
                }
            }
        });


        View postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postItem = new Intent(MainUI.this, PostActivity.class);
                startActivity(postItem);
            }
        });


        View profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileActivity = new Intent(MainUI.this, ProfileActivity.class);
                startActivity(profileActivity);
            }
        });

        View chatButton = findViewById(R.id.chat_button);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(MainUI.this);
                Intent intent = new Intent(MainUI.this, ChatlistsActivity.class);
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, chatList_url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("TEST1", response.toString());
                        intent.putExtra("conversationsList", response.toString());
                        intent.putExtra("myID", myID);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("CHAT", chatList_url);
                    }
                });
                queue.add(jsonArrayRequest);
            }
        });



        View logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                signOut();
            }
        });

        View walletButton = findViewById(R.id.post_wallet);
        walletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent walletActivity = new Intent(MainUI.this, CheckoutActivity.class);
                startActivity(walletActivity);
            }
        });
    }

    private String getTimeFromAndroid() {
        Date dt = new Date();
        int hours = dt.getHours();
        int min = dt.getMinutes();

        if(hours>=1 && hours<=12){
            Log.d(TAG, "time" + hours + " " + min);
            Log.d(TAG, "1");
            return "Good Morning";
        }else if(hours>=12 && hours<=16){
            Log.d(TAG, "time" + hours + " " + min);
            Log.d(TAG, "2");
            return "Good Afternoon";
        }else if(hours>=16 && hours<=21){
            Log.d(TAG, "time" + hours + " " + min);
            Log.d(TAG, "3");
            return "Good Evening";
        }else {
            Log.d(TAG, "time" + hours + " " + min);
            Log.d(TAG, "4");
            return "Good Night";
        }
    }

    private void viewRecommendation() {
        //use to test
        String[] IDs = new String[2];
        RecyclerView recommend_item_recyclerview = findViewById(R.id.recommend_itemList);
        MainUIAdapter recommendAdapter = new MainUIAdapter(getLayoutInflater());
        recommend_item_recyclerview.setAdapter(recommendAdapter);
        recommend_item_recyclerview.setLayoutManager(new GridLayoutManager(this,2));
        RequestQueue queue = Volley.newRequestQueue(MainUI.this);
        String getRecommendation_url = "http://20.106.78.177:8081/recommand/getrecommendation/";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, getRecommendation_url + MainActivity.idOfUser, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONArray jsonArray = response;
                try {
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String itemID = jsonObject.getString("ItemID");
                        recommendAdapter.addList(itemID);
                    }
                }
                catch (Exception w)
                {
                    Toast.makeText(MainUI.this,w.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("RECOMMEND", getRecommendation_url + MainActivity.idOfUser);
            }
        });
        queue.add(jsonArrayRequest);
    }

    private void signOut()
    {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Intent logoutActivity = new Intent(MainUI.this, MainActivity.class);
                        startActivity(logoutActivity);
                    }
                });
    }

    //these functions are for old search activity
    private boolean validCheck(){
        if (searchKey.equals("")){
            Toast.makeText(MainUI.this, "Fail, please type in something", Toast.LENGTH_SHORT).show();
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
                    Intent ListUI = new Intent(MainUI.this, ItemListActivity.class);
                    ListUI.putExtra("search_interface","1");
                    startActivity(ListUI);
                    //Toast.makeText(SearchActivity.this, "Successfully",Toast.LENGTH_SHORT).show();
                }
                catch (Exception w)
                {
                    Toast.makeText(MainUI.this,w.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainUI.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueueForSearch.add(jsonArrayRequest);
    }

    private void getDataForCategory(String searchKey)
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
                    Intent ListUI = new Intent(MainUI.this, ItemListActivity.class);
                    ListUI.putExtra("search_interface","1");
                    startActivity(ListUI);
                    //Log.d("MainUIcategory",itemIDList.get(0));
                }
                catch (Exception w)
                {
                    Toast.makeText(MainUI.this,w.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainUI.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueueForSearch.add(jsonArrayRequest);
    }
    public static List<String> getItemList(){
        return itemIDList;
    }
}
