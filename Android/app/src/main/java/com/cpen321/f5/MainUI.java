package com.cpen321.f5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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

public class MainUI extends AppCompatActivity {
    private Button searchButton;
    private Button postButton;
    private Button checkoutButton;
    private Button profileButton;
    private Button chatButton;
    private Button logoutButton;
    private Button categoryButton;
    private Button walletButton;
    private ImageButton IB1;
    private ImageButton IB2;
    private ImageButton IB3;
    private ImageButton IB4;
    private ImageButton IB5;
    private ImageButton IB6;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        categoryButton = findViewById(R.id.main_ui_more);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryList = new Intent(MainUI.this, CategoryActivity.class);
                startActivity(categoryList);
            }
        });

        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchItem = new Intent(MainUI.this, SearchActivity.class);
                startActivity(searchItem);
            }
        });

        postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postItem = new Intent(MainUI.this, PostActivity.class);
                startActivity(postItem);
            }
        });

        checkoutButton = findViewById(R.id.checkout_button);
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

        chatButton = findViewById(R.id.chat_button);
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

        IB1 = findViewById(R.id.imageButton01);
        IB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IB1Activity = new Intent(MainUI.this, ItemActivity.class);
                startActivity(IB1Activity);
            }
        });

        IB2 = findViewById(R.id.imageButton02);
        IB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IB2Activity = new Intent(MainUI.this, ItemActivity.class);
                startActivity(IB2Activity);
            }
        });

        IB3 = findViewById(R.id.imageButton03);
        IB3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IB2Activity = new Intent(MainUI.this, ItemActivity.class);
                startActivity(IB2Activity);
            }
        });

        IB4 = findViewById(R.id.imageButton04);
        IB4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IB4Activity = new Intent(MainUI.this, ItemActivity.class);
                startActivity(IB4Activity);
            }
        });

        IB5 = findViewById(R.id.imageButton05);
        IB5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IB5Activity = new Intent(MainUI.this, ItemActivity.class);
                startActivity(IB5Activity);
            }
        });

        IB6 = findViewById(R.id.imageButton06);
        IB6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent IB6Activity = new Intent(MainUI.this, ItemActivity.class);
                startActivity(IB6Activity);
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

        walletButton = findViewById(R.id.post_wallet);
        walletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent walletActivity = new Intent(MainUI.this, WalletActivity.class);
                startActivity(walletActivity);
            }
        });
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
}
