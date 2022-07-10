package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {
    final String TAG = "SearchActivity";
    ListView listView;
    String[] name;
    ArrayAdapter<String> arrayAdapter;
    public static String itemSelected;

    public static String itemSeller;
    public static String itemDescription;
    public static String itemCategory;
    public static String itemLocation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        name = getResources().getStringArray(R.array.countries_array);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = findViewById(R.id.listview);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name);
        listView.setAdapter(arrayAdapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                itemSelected = arrayAdapter.getItem(position);
                Toast.makeText(SearchActivity.this, itemSelected + " Selected", Toast.LENGTH_SHORT).show();
                Intent itemIntent = new Intent(SearchActivity.this, ItemActivity.class);
                startActivity(itemIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}