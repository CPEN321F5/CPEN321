package com.cpen321.f5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatlistsActivity extends AppCompatActivity {

    private String current_userID;
    private String coresChats;
    private JSONArray coresChatsJson;
    //private JSONArray chatListJson = new JSONArray();
    private RecyclerView chatList;
    private ImageView homeBtn, profileBtn;
    private ChatListAdapter chatListAdapter;
    //get from server
    private String wholeConversationList;
    private JSONArray JsonConversationList;
    private String myID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlists);
        wholeConversationList = getIntent().getStringExtra("conversationsList");
        myID = getIntent().getStringExtra("myID");
        //parse data
        if( wholeConversationList!= null){
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

        chatList = findViewById(R.id.chatLists);
        homeBtn = findViewById(R.id.home_chat_button);
        profileBtn = findViewById(R.id.profile_chat_button);

        chatListAdapter = new ChatListAdapter(getLayoutInflater(), myID);
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