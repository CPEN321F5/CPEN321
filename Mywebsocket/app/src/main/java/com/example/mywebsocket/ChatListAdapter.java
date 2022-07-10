package com.example.mywebsocket;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.singleListHolder> {

    private LayoutInflater chatListInflate;
    private String userID;
    private JSONArray coresChats= new JSONArray();

    public ChatListAdapter(LayoutInflater chatListInflate){

        this.chatListInflate = chatListInflate;

    }

    @NonNull
    @Override
    public singleListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = chatListInflate.inflate(R.layout.single_chat_layout, parent,false);
        return new singleListHolder(view);
    }

    @Override //get data
    public void onBindViewHolder(@NonNull singleListHolder holder, int position) {
        /* TO DO
         * username
         * last message (check if last message is an image then show [click to see image...])
         * last message time
         * TO DO              */

        try {
            holder.userName.setText(coresChats.getJSONObject(position).getString("name"));
            holder.lastMessage.setText(coresChats.getJSONObject(position).getString("Last message"));
            holder.lastTime.setText(coresChats.getJSONObject(position).getString("Time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatAcitivity.class);
                intent.putExtra("userID", userID);
                v.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return coresChats.length();
    }


    public void addList (JSONObject jsonObject) {
        coresChats.put(jsonObject);
        Log.d("TEST", "Adding an item");
        notifyDataSetChanged();
    }

    public static class singleListHolder extends RecyclerView.ViewHolder{
        TextView userName, lastMessage, lastTime;
        Button deleteBtn;

        public singleListHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.Username);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            lastTime = itemView.findViewById(R.id.lastTime);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

        }
    }
}
