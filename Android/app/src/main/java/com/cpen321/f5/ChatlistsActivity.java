package com.cpen321.f5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ChatlistsActivity extends AppCompatActivity {

    private JSONArray JsonConversationList;
    private String myID;
    private ChatListAdapter chatListAdapter;

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
        TextView homeBtn = findViewById(R.id.home_chat_button);
        TextView profileBtn = findViewById(R.id.profile_chat_button);
        TextView hintBtn = findViewById(R.id.hint_chat_button);

        chatListAdapter = new ChatListAdapter(getLayoutInflater());
        chatList.setAdapter(chatListAdapter);
        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(chatList);
        chatList.setLayoutManager(new LinearLayoutManager(this));

            for(int i = 0; i <= JsonConversationList.length() - 1; i++){
                try {
                    chatListAdapter.addList(JsonConversationList.getJSONObject(i));
                    //chatList.smoothScrollToPosition(chatListAdapter.getItemCount()-1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        SwipeRefreshLayout chatListRefresher = findViewById(R.id.chatListRefresh);
        String chatList_init_url = "http://20.106.78.177:8081/chat/getconversationlist/";
        String chatList_url = chatList_init_url + myID;
        chatListRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RequestQueue queue = Volley.newRequestQueue(ChatlistsActivity.this);
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, chatList_url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JsonConversationList = response;
                        initView();
                        Log.d("TESTChatList", response.toString());
                        chatListRefresher.setRefreshing(false);
                        Toast.makeText(ChatlistsActivity.this,"Refreshing completed", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TESTChatList", chatList_url);
                        chatListRefresher.setRefreshing(false);
                    }
                });
                queue.add(jsonArrayRequest);
            }
        });


        homeBtn.setOnClickListener(v ->{
            Intent intent = new Intent(this, MainUI.class);
            startActivity(intent);
        });

        profileBtn.setOnClickListener(v ->{
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            startActivity(profileIntent);
        });

        hintBtn.setOnClickListener(v ->{
            showAlertDialog("Swipe the single chat left to delete\n\n"
                    + "Swipe the list down to refresh");
        });
    }

    private void showAlertDialog(String s) {
        AlertDialog dialog = new AlertDialog.Builder(ChatlistsActivity.this)
                .setMessage(s)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        // input here is what kind of movement it wants to detect and swiped direction
        // there is to movement, so the first input is 0
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getBindingAdapterPosition();
            chatListAdapter.deleteList(position);
            Toast.makeText(ChatlistsActivity.this,"This chat is deleted...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(ChatlistsActivity.this, R.color.DarkSeaGreen))
                    .addActionIcon(R.drawable.ic_baseline_delete_forever_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}