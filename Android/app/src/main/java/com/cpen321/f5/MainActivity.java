package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button searchButton;
    private Button postButton;
    private Button checkoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initial commit
        searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchItem = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchItem);
            }
        });

        postButton = findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postItem = new Intent(MainActivity.this, PostActivity.class);
                startActivity(postItem);
            }
        });

        checkoutButton = findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkoutActivity = new Intent(MainActivity.this, CheckoutActivity.class);
                startActivity(checkoutActivity);
            }
        });
    }
}