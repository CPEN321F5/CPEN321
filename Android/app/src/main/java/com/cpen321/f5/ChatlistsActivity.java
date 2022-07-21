package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;

public class ChatlistsActivity extends AppCompatActivity {

    private JSONArray JsonConversationList;
    private String myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlists);
        //get from server
        String wholeConversationList = getIntent().getStringExtra("conversationsList");
        myID = getIntent().getStringExtra("myID");
        //parse data
        if( wholeConversationList != null){
            try {
                JsonConversationList = new JSONArray(wholeConversationList);
            } catch (JSONException e) {
                e.printStackTrace();
                JsonConversationList = new JSONArray();
            }
        }
        initView();
    }

    private void initView() {

        //private JSONArray chatListJson = new JSONArray();
        RecyclerView chatList = findViewById(R.id.chatLists);
        ImageView homeBtn = findViewById(R.id.home_chat_button);
        ImageView profileBtn = findViewById(R.id.profile_chat_button);

        ChatListAdapter chatListAdapter = new ChatListAdapter(getLayoutInflater(), myID);
        chatList.setAdapter(chatListAdapter);
        chatList.setLayoutManager(new LinearLayoutManager(this));


            for(int i = 0; i <= JsonConversationList.length() - 1; i++){
                try {
                    chatListAdapter.addList(JsonConversationList.getJSONObject(i));
                    //chatList.smoothScrollToPosition(chatListAdapter.getItemCount()-1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }



        //Btns to do list
        homeBtn.setOnClickListener(v ->{
            Intent intent = new Intent(this, MainUI.class);
            startActivity(intent);
        });

        profileBtn.setOnClickListener(v ->{
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            startActivity(profileIntent);
        });

    }

}