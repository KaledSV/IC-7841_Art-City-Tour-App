package com.example.artcitytourapp.activities.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.artcitytourapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import Fotografia.Fotografia;

public class GalleryImagesAdapter extends BaseAdapter{
    private Context mContext;
    public Fotografia[] ImagesArray = {};

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
        bdGetPhoto(imageView, ImagesArray[i]);
        imageView.setPadding(10,10,10,10);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(
                340,
                350
        ));

        return imageView;
    }

    protected void bdGetPhoto(ImageView iv, Fotografia photo){
        StorageReference pathReference  = FirebaseStorage.getInstance().getReference(photo.getFoto());
        try {
            File localFile = File.createTempFile("tempFile", ".png");
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 400, 400, false));
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // setters and getters
    public Fotografia[] getImagesArray() {
        return ImagesArray;
    }

    public void setImagesArray(Fotografia[] imagesArray) {
        ImagesArray = imagesArray;
    }

    public void addImage(Fotografia image)
    {
        int size = ImagesArray.length;
        Fotografia[] newarr = new Fotografia[size + 1];

        for (int i = 0; i < size; i++)
            newarr[i] = ImagesArray[i];

        newarr[size] = image;
        ImagesArray = newarr;
    }
}
