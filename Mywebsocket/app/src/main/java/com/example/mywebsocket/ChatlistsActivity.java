package com.example.mywebsocket;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlists);
        initView();
    }

    private void initView() {

        chatList = findViewById(R.id.chatLists);
        homeBtn = findViewById(R.id.home_chat_button);
        profileBtn = findViewById(R.id.profile_chat_button);

        chatListAdapter = new ChatListAdapter(getLayoutInflater());
        chatList.setAdapter(chatListAdapter);
        chatList.setLayoutManager(new LinearLayoutManager(this));

        current_userID = "122132142342";
        coresChats = "[{\"conversationID\":\"0001\",\"name\":\"Andrew\",\"Last message\":\"Andrew\",\"Time\":\"June 11 8:00\"}," +
                "{\"conversationID\":\"0001\",\"name\":\"Peter\", \"Last message\":\"HHHgfdgdfgfdgdfffffffffffffffffffffffH\",\"Time\":\"June 11 9:00\"}," +
                "{\"conversationID\":\"0001\",\"name\":\"Peteeeer\", \"Last message\":\"HHHddfgfdgdfgfdgdfgfdH\",\"Time\":\"June 11 9:00\"}," +
                "{\"conversationID\":\"0001\",\"name\":\"Peeeeter\", \"Last message\":\"HH fdgfdg rtert betreg HH\",\"Time\":\"June 11 9:00\"}," +
                "{\"conversationID\":\"0001\",\"name\":\"Peteeeer\", \"Last message\":\"HHdfdsfffffffcvxxxxxxxxxxfdddddwesfddddddddddxw3ersfdHH\",\"Time\":\"June 11 9:00\"},]";

        if( coresChats!= null){
            try {
                coresChatsJson = new JSONArray(coresChats);
            } catch (JSONException e) {
                e.printStackTrace();
                coresChatsJson = new JSONArray();
            }

            for(int i = 0; i <= coresChatsJson.length() - 1; i++){


                try {
                    chatListAdapter.addList(coresChatsJson.getJSONObject(i));
                    //chatList.smoothScrollToPosition(chatListAdapter.getItemCount()-1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }


        //Btns to do list
        homeBtn.setOnClickListener(v ->{
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        profileBtn.setOnClickListener(v ->{

        });

    }

}