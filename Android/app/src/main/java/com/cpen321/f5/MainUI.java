package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainUI extends AppCompatActivity
{

    private static final String TAG = "MainUI";

    private Button profileButton;
    private Button itemButton;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);

        profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent profileIntent = new Intent(MainUI.this, Profile.class);
                startActivity(profileIntent);
            }
        });

        itemButton = findViewById(R.id.item_button);
        itemButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent itemIntent = new Intent(MainUI.this, Item.class);
                startActivity(itemIntent);
            }
        });
    }
}