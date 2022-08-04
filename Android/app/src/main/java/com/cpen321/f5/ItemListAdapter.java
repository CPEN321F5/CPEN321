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
import android.widget.Toast;

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

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.SingleItemHolder> {

    private LayoutInflater itemListInflate;
    private JSONArray itemLists= new JSONArray();
    RequestQueue requestQueueForItem;
    String item_get_url = "http://20.106.78.177:8081/item/getbyid/";


    public ItemListAdapter(LayoutInflater itemListInflate){

        this.itemListInflate = itemListInflate;

    }

    @NonNull
    @Override
    public SingleItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = itemListInflate.inflate(R.layout.item_list_layout, parent,false);
        return new ItemListAdapter.SingleItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleItemHolder holder, int position) {
        String itemID = "1";
        try {
            itemID = itemLists.get(position).toString();
            Log.d("ItemListAdapter1", itemID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestQueueForItem = Volley.newRequestQueue(this.itemListInflate.getContext());
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
                    String getSellerID = response.getString("sellerID");
                    holder.item_name.setText(response.getString("name"));
                    holder.item_price.setText(response.getString("currentPrice"));
                    holder.item_username.setText(getSellerID); //change later
                    holder.item_image.setImageBitmap(bitmap);
                    holder.itemView.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), ItemActivity.class);
                            Intent my_intent = new Intent(v.getContext(), MyItemActivity.class);
                            intent.putExtra("itemID", itemID);
                            my_intent.putExtra("myItemID", itemID);
                            Log.d("ItemListAdapter2", itemID);
                            if(getSellerID.equals(MainActivity.idOfUser)){
                                v.getContext().startActivity(my_intent);
                            }else{
                                v.getContext().startActivity(intent);
                            }
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
        return itemLists.length();
    }

    public void addList (String single_itemID) {
        itemLists.put(single_itemID);
        Log.d("TEST", "Adding an item");
        notifyDataSetChanged();
    }


    public static class SingleItemHolder extends RecyclerView.ViewHolder{
        ImageView item_image;
        TextView item_name;
        TextView item_price;
        TextView item_username;

        public SingleItemHolder(@NonNull View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            item_name = itemView.findViewById(R.id.item_name);
            item_price = itemView.findViewById(R.id.item_price);
            item_username = itemView.findViewById(R.id.item_username);
        }
    }

    private Bitmap getBitmapFromString(String image) {
        byte[] bytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
