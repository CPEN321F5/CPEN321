package com.cpen321.f5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class ViewAdapterItem extends PagerAdapter
{
    private Context context;
    private LayoutInflater layoutInflator;
    //private Integer[] images = {R.drawable.one, R.drawable.two, R.drawable.three};
    //private Bitmap[] images = {ItemActivity.bitmap0, ItemActivity.bitmap1, ItemActivity.bitmap2};
    private Drawable[] images = {ItemActivity.drawable0, ItemActivity.drawable1, ItemActivity.drawable2};

    ViewAdapterItem(Context context)
    {
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
    {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        layoutInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflator.inflate(R.layout.activity_viewadapter, null);
        ImageView imageView = view.findViewById(R.id.image_view);
        //imageView.setImageBitmap(images[position]);
        imageView.setImageDrawable(images[position]);
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
