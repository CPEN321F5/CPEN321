package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    private static final String TAG = "Profile";
    private Button itemsButton;
    private Button disputeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        itemsButton = findViewById(R.id.items_button);
        itemsButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent itemsIntent = new Intent(Profile.this, ItemsList.class);
                startActivity(itemsIntent);
            }
        });

        disputeButton = findViewById(R.id.dispute_button);
        disputeButton.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                Intent disputeIntent = new Intent(Profile.this, Dispute.class);
                startActivity(disputeIntent);
            }
        });

    }
}