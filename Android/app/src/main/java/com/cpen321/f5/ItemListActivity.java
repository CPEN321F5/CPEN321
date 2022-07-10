package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

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
    ListView listView;
    String[] IDsArray;
    List<Item> cache = SearchActivity.getItemList();
    private final String TAG = "ItemListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);



        int size = cache.size();
        IDsArray = new String[size];
        for (int i = 0; i < size; i++){
            IDsArray[i] = cache.get(i).getID();
            Log.d(TAG, i + " => " + IDsArray[i]);
        }



        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, IDsArray);

        ListView listView = (ListView) findViewById(R.id.item_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
//                        Intent donut = new Intent(MainActivity.this, Donut.class);
//                        startActivity(donut);
                        Toast.makeText(ItemListActivity.this, "YA MA DEI 0", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(ItemListActivity.this, "YA MA DEI 1", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(ItemListActivity.this, "YA MA DEI 2", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(ItemListActivity.this, "YA MA DEI 3", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });

    }
}