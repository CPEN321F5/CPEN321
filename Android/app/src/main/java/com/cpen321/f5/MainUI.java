package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainUI extends AppCompatActivity {
    private Button searchButton;
    private Button postButton;
    private Button checkoutButton;
    private Button profileButton;
    private ImageButton IB1;
    private ImageButton IB2;
    private ImageButton IB3;
    private ImageButton IB4;
    private ImageButton IB5;
    private ImageButton IB6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);
        //initial commit
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
    }
}
