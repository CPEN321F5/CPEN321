package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ItemActivity extends AppCompatActivity {

    private static final String TAG = "ItemActivity";

    TextView productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        productName = findViewById(R.id.product_name);
        productName.setText(SearchActivity.itemSelected);
    }
}