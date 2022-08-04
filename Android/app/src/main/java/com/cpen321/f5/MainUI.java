package com.cpen321.f5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.List;

public class MainUI extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    String searchKey;
    RequestQueue requestQueueForSearch;
    private static List<String> itemIDList;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View logoutButton;
        View profileButton;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);
        //initial commit
        //url strings for chat list activity
        String chatList_init_url = "http://20.106.78.177:8081/chat/getconversationlist/";
        String myID = MainActivity.idOfUser;
        String chatList_url = chatList_init_url + myID;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        View categoryButton = findViewById(R.id.main_ui_more);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryList = new Intent(MainUI.this, CategoryActivity.class);
                startActivity(categoryList);
            }
        });

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

        View checkoutButton = findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkoutActivity = new Intent(MainUI.this, CheckoutActivity.class);
                startActivity(checkoutActivity);
            }
        });

        profileButton = findViewById(R.id.profile_button);
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



        logoutButton = findViewById(R.id.logout_button);
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
//                Intent walletActivity = new Intent(MainUI.this, WalletActivity.class);
//                startActivity(walletActivity);

                //new code
                Intent walletActivity = new Intent(MainUI.this, CheckoutActivity.class);
                startActivity(walletActivity);
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

        viewRecommendation();
    }



    private void viewRecommendation() {
        //use to test
        String[] IDs = new String[2];
        IDs[0] = "1659492974531";
        IDs[1] = "1659493108645";
        RecyclerView recommend_item_recyclerview = findViewById(R.id.recommend_itemList);
        MainUIAdapter recommendAdapter = new MainUIAdapter(getLayoutInflater());
        recommend_item_recyclerview.setAdapter(recommendAdapter);
        recommend_item_recyclerview.setLayoutManager(new GridLayoutManager(this,2));
        for(int i = 0; i <= IDs.length - 1; i++){
            recommendAdapter.addList(IDs[i]);
        }
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
