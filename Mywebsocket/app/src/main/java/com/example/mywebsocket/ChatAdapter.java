package com.example.mywebsocket;
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

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {

    private static final int TEXT_SENT = 0;
    private static final int TEXT_RECEIVE = 1;
    private static final int IMAGE_SENT = 2;
    private static final int IMAGE_RECEIVE = 3;

    private LayoutInflater chatInflate; // instantiate the contents of layout XML files into View objects
    private List<JSONObject> messages = new ArrayList<>();

    public ChatAdapter (LayoutInflater chatInflate) {

        this.chatInflate = chatInflate;

    }

    private class SentTextHolder extends RecyclerView.ViewHolder {
        TextView messageText, sendDate, sendTime;;
        public SentTextHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.sentText);
            sendDate = itemView.findViewById(R.id.sendDate);
            sendTime = itemView.findViewById(R.id.sendTime);
        }
    }

    private class ReceiveTextHolder extends RecyclerView.ViewHolder {
        TextView nameText, messageText, receiveDate, receiveTime;
        public ReceiveTextHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.NameText);
            messageText = itemView.findViewById(R.id.receiveText);
            receiveDate = itemView.findViewById(R.id.receiveDate);
            receiveTime = itemView.findViewById(R.id.receiveTime);
        }
    }

    private class SentImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView sendDate, sendTime;
        public SentImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.sendImage);
            sendDate = itemView.findViewById(R.id.sendDate2);
            sendTime = itemView.findViewById(R.id.sendTime2);
        }
    }

    private class ReceiveImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameText, receiveDate, receiveTime;
        public ReceiveImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.receiveImage);
            nameText = itemView.findViewById(R.id.NameText);
            receiveDate = itemView.findViewById(R.id.receiveDate2);
            receiveTime = itemView.findViewById(R.id.receiveTime2);
        }
    }

    @Override //identify sender and receiver; text and image
    public int getItemViewType(int position) {
        JSONObject message = messages.get(position);
        try {
            if (message.getBoolean("isSent")) {
                if (message.has("message")){
                    return TEXT_SENT;
                }else{
                    return IMAGE_SENT;
                }
            } else {
                if (message.has("message")){
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
        }
        return null;
    }

    @Override //get data
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        JSONObject message = messages.get(position);

        try {
            if (message.getBoolean("isSent")) {
                if (message.has("message")) {
                    SentTextHolder textHolder = (SentTextHolder) holder;
                    textHolder.messageText.setText(message.getString("message"));
                    textHolder.sendDate.setText("Aug 18");
                    textHolder.sendTime.setText("12:00");
                } else {
                    SentImageHolder imageHolder = (SentImageHolder) holder;
                    Bitmap bitmap = getBitmapFromString(message.getString("image")); //base64 to bitmap
                    imageHolder.imageView.setImageBitmap(bitmap);
                    imageHolder.sendDate.setText("Dec 13");
                    imageHolder.sendTime.setText("01:00");
                }
            } else {
                if (message.has("message")) {
                    ReceiveTextHolder textHolder = (ReceiveTextHolder) holder;
                    textHolder.nameText.setText(message.getString("name"));
                    textHolder.messageText.setText(message.getString("message"));
                    //textHolder.receiveDate.setText();
                    //textHolder.receiveTime.setText();
                } else {
                    ReceiveImageHolder imageHolder = (ReceiveImageHolder) holder;
                    imageHolder.nameText.setText(message.getString("name"));
                    Bitmap bitmap = getBitmapFromString(message.getString("image"));
                    imageHolder.imageView.setImageBitmap(bitmap);
                    //imageHolder.receiveDate.setText();
                    //imageHolder.receiveTime.setText();
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
