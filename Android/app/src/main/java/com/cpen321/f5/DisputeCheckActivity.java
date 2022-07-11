package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DisputeCheckActivity extends AppCompatActivity
{

    TextView adminAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispute_check);

        adminAnswer = findViewById(R.id.admin_answer_caption);
        adminAnswer.setText("Admin Response: " + DisputeActivity.adminConclusion);
    }
}