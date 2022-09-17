package com.example.artcitytourapp.activities.Adapters;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.artcitytourapp.R;

public class GalleryImagesAdapter extends BaseAdapter{
    private Context mContext;
    public int[] ImagesArray = {
            R.drawable.m1,
            R.drawable.m2,
            R.drawable.m3,
            R.drawable.m4,
            R.drawable.m5,
            R.drawable.m6
    };

    public GalleryImagesAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return ImagesArray.length;
    }

    @Override
    public Object getItem(int i) {
        return ImagesArray[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(ImagesArray[i]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(
                340,
                350
        ));

        return imageView;
    }
}
