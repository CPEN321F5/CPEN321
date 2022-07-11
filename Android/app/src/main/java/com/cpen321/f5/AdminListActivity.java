package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class AdminListActivity extends AppCompatActivity
{
    private static final String TAG = "AdminListActivity";

    public static String adminItemID;
    ListView listView;
    String[] IDsArray;
    List<String> cache = AdminSearchActivity.getAdminItemList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        int size = cache.size();
        Log.d(TAG, "size = " + size);
        //Log.d(TAG, "attribute = " + cache.get(0));

        if (size == 0){
            Toast.makeText(AdminListActivity.this, "No result found",Toast.LENGTH_SHORT).show();
        }

        IDsArray = new String[size];
        for (int i = 0; i < size; i++){
            IDsArray[i] = cache.get(i);
            Log.d(TAG, i + " => " + IDsArray[i]);
        }



        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, IDsArray);

        ListView listView = (ListView) findViewById(R.id.item_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adminItemID = IDsArray[position];
                Intent tmp = new Intent(AdminListActivity.this, AdminItemActivity.class);
                startActivity(tmp);
            }
        });

    }
}