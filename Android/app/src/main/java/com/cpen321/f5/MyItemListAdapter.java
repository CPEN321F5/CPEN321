package com.cpen321.f5;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyItemListAdapter extends RecyclerView.Adapter<MyItemListAdapter.SingleItemHolder> {
    private LayoutInflater MyItemListInflate;
    private JSONArray MyItemLists= new JSONArray();
    RequestQueue requestQueueForItem;
    String item_get_url = "http://20.106.78.177:8081/item/getbyid/";

    public MyItemListAdapter(LayoutInflater MyItemListInflate){

        this.MyItemListInflate = MyItemListInflate;

    }

    @NonNull
    @Override
    public MyItemListAdapter.SingleItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = MyItemListInflate.inflate(R.layout.my_item_list_layout, parent,false);
        return new MyItemListAdapter.SingleItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyItemListAdapter.SingleItemHolder holder, int position) {
        String itemID = "1";
        try {
            itemID = MyItemLists.get(position).toString();
            Log.d("ItemListAdapter1", itemID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestQueueForItem = Volley.newRequestQueue(this.MyItemListInflate.getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, item_get_url+itemID+"/", null, new Response.Listener<JSONObject>()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response)
            {
                //Log.d("ItemListAdapter", "attribute = " + response.toString());

                try
                {   String itemID = response.getString("ItemID");
                    Bitmap bitmap = getBitmapFromString(response.getString("image_0"));
                    String status = response.getString("status");
                    holder.item_name.setText(response.getString("name"));
                    holder.item_price.setText(response.getString("currentPrice"));
                    holder.item_image.setImageBitmap(bitmap);
                    holder.item_status.setText(status);
                    holder.itemView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), MyItemActivity.class);
                            intent.putExtra("myItemID", itemID);
                            intent.putExtra("status", status);
                            v.getContext().startActivity(intent);
                        }
                    });
                }
                catch (Exception w)
                {
                    Log.d("TESTItemListAdapter", item_get_url);
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("TESTItemListAdapter", error.getMessage());
            }
        });
        requestQueueForItem.add(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return MyItemLists.length();
    }

    public void addList (String single_itemID) {
        MyItemLists.put(single_itemID);
        Log.d("TEST", "Adding an item");
        notifyDataSetChanged();
    }

    public static class SingleItemHolder extends RecyclerView.ViewHolder{
        ImageView item_image;
        TextView item_name;
        TextView item_price;
        TextView item_status;

        public SingleItemHolder(@NonNull View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_name = itemView.findViewById(R.id.item_name);
            item_price = itemView.findViewById(R.id.item_price);
            item_status = itemView.findViewById(R.id.item_status);
        }
    }

    private Bitmap getBitmapFromString(String image) {
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}