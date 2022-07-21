package com.cpen321.f5;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter {

    private static final int TEXT_SENT = 0;
    private static final int TEXT_RECEIVE = 1;
    private static final int IMAGE_SENT = 2;
    private static final int IMAGE_RECEIVE = 3;

    private final LayoutInflater chatInflate; // instantiate the contents of layout XML files into View objects
    private final String myID;
    private final String othername;
    private final List<JSONObject> messages = new ArrayList<>();

    public ChatAdapter (LayoutInflater chatInflate, String myID, String userID1, String user1name, String user2name) {

        this.chatInflate = chatInflate;
        this.myID = myID;
        this.othername = Objects.equals(userID1, myID) ? user2name : user1name;

    }

    private class SentTextHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView sendTime;
        public SentTextHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.sentText);
            sendTime = itemView.findViewById(R.id.sendTime);
        }
    }

    private class ReceiveTextHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView messageText;
        TextView receiveTime;
        public ReceiveTextHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.NameText);
            messageText = itemView.findViewById(R.id.receiveText);
            receiveTime = itemView.findViewById(R.id.receiveTime);
        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView sendTime;
        public SentImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.sendImage);
            sendTime = itemView.findViewById(R.id.sendTime2);
        }
    }

    private class ReceiveImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameText;
        TextView receiveTime;
        public ReceiveImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.receiveImage);
            nameText = itemView.findViewById(R.id.NameText);
            receiveTime = itemView.findViewById(R.id.receiveTime2);
        }
    }

    @Override //identify sender and receiver; text and image
    public int getItemViewType(int position) {
        JSONObject message = messages.get(position);
        try {
            if (message.getString("userID").equals(myID)) {
                if (!message.has("image")){
                    return TEXT_SENT;
                }else{
                    return IMAGE_SENT;
                }
            } else {
                if (!message.has("image")){
                    return TEXT_RECEIVE;
                }else{
                    return IMAGE_RECEIVE;
                }
            }
        } catch (JSONException error) {
            error.printStackTrace();
        }
        return -1;
    }

    @NonNull
    @Override //inflate layout
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch(viewType){
            case TEXT_SENT:
                view = chatInflate.inflate(R.layout.item_send_text, parent,false);
                return new SentTextHolder(view);
            case TEXT_RECEIVE:
                view = chatInflate.inflate(R.layout.item_receive_text, parent, false);
                return new ReceiveTextHolder(view);
            case IMAGE_SENT:
                view = chatInflate.inflate(R.layout.item_send_image, parent, false);
                return new SentImageHolder(view);
            case IMAGE_RECEIVE:
                view = chatInflate.inflate(R.layout.item_receive_image, parent, false);
                return new ReceiveImageHolder(view);
            default:
                Log.d("ChatAdapter", "invalid view type");
                return null;
        }
    }

    @Override //get data
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        JSONObject message = messages.get(position);
        long epoch_time = 0;
        try {
            epoch_time = Long.parseLong(message.has("time") ? message.getString("time") : String.valueOf(System.currentTimeMillis()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String date = new SimpleDateFormat("MM/dd/ HH:mm:ss").format(epoch_time);

        try {
            if (message.getString("userID").equals(myID)) {
                if (!message.has("image")) {
                    SentTextHolder textHolder = (SentTextHolder) holder;
                    textHolder.messageText.setText(message.getString("message"));
                    textHolder.sendTime.setText(date);
                } else {
                    SentImageHolder imageHolder = (SentImageHolder) holder;
                    Bitmap bitmap = getBitmapFromString(message.getString("image")); //base64 to bitmap
                    imageHolder.imageView.setImageBitmap(bitmap);
                    imageHolder.sendTime.setText(date);
                }
            } else {
                if (!message.has("image")) {
                    ReceiveTextHolder textHolder = (ReceiveTextHolder) holder;
                    textHolder.nameText.setText(othername);
                    textHolder.messageText.setText(message.getString("message"));
                    textHolder.receiveTime.setText(date);
                } else {
                    ReceiveImageHolder imageHolder = (ReceiveImageHolder) holder;
                    imageHolder.nameText.setText(othername);
                    Bitmap bitmap = getBitmapFromString(message.getString("image"));
                    imageHolder.imageView.setImageBitmap(bitmap);
                    imageHolder.receiveTime.setText(date);
                }

            }
        } catch (JSONException error) {
            error.printStackTrace();
        }
    }

    private Bitmap getBitmapFromString(String image) {
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addItem (JSONObject jsonObject) {
        messages.add(jsonObject);
        notifyDataSetChanged(); //refresh when list change
    }
}
