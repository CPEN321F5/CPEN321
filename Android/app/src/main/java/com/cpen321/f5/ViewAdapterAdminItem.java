package com.cpen321.f5;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewAdapterAdminItem extends PagerAdapter
{
    private Context context;
    //private Integer[] images = {R.drawable.one, R.drawable.two, R.drawable.three};
    //private Bitmap[] images = {ItemActivity.bitmap0, ItemActivity.bitmap1, ItemActivity.bitmap2};
    private Drawable[] images = {AdminItemActivity.drawable0, AdminItemActivity.drawable1, AdminItemActivity.drawable2};

    ViewAdapterAdminItem(Context context)
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
        LayoutInflater layoutInflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflate.inflate(R.layout.activity_viewadapter, null);
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
