package com.cpen321.f5;

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

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.SingleListHolder> {

    private LayoutInflater chatListInflate;
    private JSONArray coresChats= new JSONArray();
    String chat_get_url = "http://20.106.78.177:8081/chat/getconversation/";

    public ChatListAdapter(LayoutInflater chatListInflate){

        this.chatListInflate = chatListInflate;

    }

    @NonNull
    @Override
    public SingleListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = chatListInflate.inflate(R.layout.single_chat_layout, parent,false);
        return new SingleListHolder(view);
    }

    @Override //get data
    public void onBindViewHolder(@NonNull SingleListHolder holder, int position) {
        /* TO DO
         * username
         * last message (check if last message is an image then show [click to see image...])
         * last message time
         * TO DO              */

        try {
            holder.userName.setText(coresChats.getJSONObject(position).getString("name"));
            holder.lastMessage.setText(coresChats.getJSONObject(position).getString("Last_message"));
            holder.lastTime.setText(coresChats.getJSONObject(position).getString("Time"));
            holder.conversationID = coresChats.getJSONObject(position).getString("conversationID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.chatGet_url = chat_get_url + holder.conversationID;
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatAcitivity.class);
                intent.putExtra("GetOrPost", "GET");
                intent.putExtra("conversations", holder.chatGet_url);
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

    // need to implement with backend
    public void deleteList(int position) {
        coresChats.remove(position);
        Log.d("TESTChatList", "removing an item");
        notifyDataSetChanged();
    }

    public static class SingleListHolder extends RecyclerView.ViewHolder{
        TextView userName;
        TextView lastMessage;
        TextView lastTime;
        Button deleteBtn;
        String conversationID;
        String chatGet_url;

        public SingleListHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.Username);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            lastTime = itemView.findViewById(R.id.lastTime);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

        }
    }
}
