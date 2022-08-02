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

import java.util.ArrayList;
import java.util.List;


public class ItemListActivity extends AppCompatActivity {
    public static String ItemID;
    ListView listView;
    String[] IDsArray;
    List<String> cache;


    private final String TAG = "ItemListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        int size;
        try{
            cache = new ArrayList<>(MainUI.getItemList());
            size = cache.size();
            if (size == 0){
                Toast.makeText(ItemListActivity.this, "No result found",Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e_item_search){
            try{
                cache = new ArrayList<>(CategoryActivity.getItemList());
                size = cache.size();
                if (size == 0){
                    Toast.makeText(ItemListActivity.this, "No result found",Toast.LENGTH_SHORT).show();
                }
            }catch (NullPointerException e_category_search){
                cache = new ArrayList<>();
                size = 0;
            }
        }



        Log.d(TAG, "size = " + size);
        //Log.d(TAG, "attribute = " + cache.get(0));


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
                ItemID = IDsArray[position];
                Intent tmp = new Intent(ItemListActivity.this, ItemActivity.class);
                startActivity(tmp);
            }
        });

    }
}