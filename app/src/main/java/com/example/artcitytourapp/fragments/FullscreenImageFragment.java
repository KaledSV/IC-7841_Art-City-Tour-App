package com.example.artcitytourapp.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.artcitytourapp.R;
import com.example.artcitytourapp.activities.Adapters.GalleryImagesAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import Fotografia.Fotografia;


public class FullscreenImageFragment extends Fragment {
    View view;
    Fotografia photo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fullscreen_image, container, false);

        Bundle b = this.getArguments();
        //recibir el id de la imagen mediante intent
        if (b != null) {
            photo = (Fotografia) b.get("photo");
            loadData();
        }

        return view;
    }

    protected void loadData(){
        LinearLayout layoutDescripcionImagen = view.findViewById(R.id.layoutDescripcionImagen);

        final View descripcionWindow = getLayoutInflater().inflate(R.layout.fragment_resena, null);
        layoutDescripcionImagen.addView(descripcionWindow);
        ImageView imageViewDetalle = view.findViewById(R.id.imagen_detalle);

        java.sql.Date timeD = new java.sql.Date(photo.getFechaSubida().getSeconds() * 1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(timeD);

        TextView resName = descripcionWindow.findViewById(R.id.resenaNombre);
        resName.setText(photo.getAutor());
        TextView resDate = descripcionWindow.findViewById(R.id.resenaFecha);
        resDate.setText(date);
        TextView resLikes = descripcionWindow.findViewById(R.id.countLikes);
        resLikes.setText(String.valueOf(photo.getLikes()));
        TextView resDislikes = descripcionWindow.findViewById(R.id.countDislikes);
        resDislikes.setText(String.valueOf(photo.getDislikes()));
        ExpandableTextView resComment = (ExpandableTextView) descripcionWindow.findViewById(R.id.expand_text_view);
        resComment.setText(photo.getDescripcion());

        bdGetPhoto(imageViewDetalle, photo);
    }

    protected void bdGetPhoto(ImageView iv, Fotografia photo){
        StorageReference pathReference  = FirebaseStorage.getInstance().getReference(photo.getFoto());
        try {
            File localFile = File.createTempFile("tempFile", photo.getFoto().substring(photo.getFoto().lastIndexOf(".")));
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
}