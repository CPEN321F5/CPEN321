package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.util.List;


public class AdminListActivity extends AppCompatActivity
{
    private static final String TAG = "AdminListActivity";

    String[] IDsArray;
    List<String> cache = AdminSearchActivity.getAdminItemList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        int size = cache.size();
        Log.d(TAG, "size = " + size);

        if (size == 0){
            Toast.makeText(AdminListActivity.this, "No result found",Toast.LENGTH_SHORT).show();
        }

        IDsArray = new String[size];
        for (int i = 0; i < size; i++){
            IDsArray[i] = cache.get(i);
            Log.d(TAG, i + " => " + IDsArray[i]);
        }

        viewAdminItems();

    }

    private void viewAdminItems() {

        RecyclerView admin_item_recyclerview = findViewById(R.id.admin_item_recyclerview);
        AdminListAdapter admin_itemListAdapter = new AdminListAdapter(getLayoutInflater());
        admin_item_recyclerview.setAdapter(admin_itemListAdapter);
        admin_item_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        for(int i = 0; i <= IDsArray.length - 1; i++){
            admin_itemListAdapter.addList(IDsArray[i]);
        }
    }

}