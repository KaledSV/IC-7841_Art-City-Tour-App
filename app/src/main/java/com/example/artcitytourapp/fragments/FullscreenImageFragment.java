package com.example.artcitytourapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.artcitytourapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import Fotografia.Fotografia;
import Usuario.VisitanteSingleton;


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

        Date timeD = photo.getFechaSubida();
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

        Button likeBtn = descripcionWindow.findViewById(R.id.likeBtn);
        Button dislikeBtn = descripcionWindow.findViewById(R.id.dislikeBtn);

        setLikeAndDislikeBtn(photo, likeBtn, dislikeBtn, resLikes, resDislikes);

        bdGetPhoto(imageViewDetalle, photo);

        setShareButton();
    }

    protected void setShareButton() {
        ImageView imageViewDetalle = view.findViewById(R.id.imagen_detalle);
        ImageView shrBtn = (ImageView) view.findViewById(R.id.shareImage_Fullscreen);

        shrBtn.setOnClickListener(view -> shareImage(imageViewDetalle));

    }

    @SuppressLint("RestrictedApi")
    private void shareImage(ImageView Image) {
        Bitmap ImageBitmap = ((BitmapDrawable)Image.getDrawable()).getBitmap(); // Gets the bitmap ( image ) associated with the imageview
        String base = "Visitá los sitios culturales del Art City Tour App!";
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), ImageBitmap, "Art City Tour", null);
        Uri imageUri =  Uri.parse(path);
        sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT,"Art City Tour App");
        sendIntent.putExtra(Intent.EXTRA_TEXT,base);
        startActivity(Intent.createChooser(sendIntent, "Enviar imagenes"));
    }

    protected void setLikeAndDislikeBtn(Fotografia photo, Button likeBtn, Button dislikeBtn, TextView resLikes, TextView resDislikes){
        VisitanteSingleton user = VisitanteSingleton.getInstance();
        setLikeImage(user.photoLikeStatus(photo.getIdFoto()), likeBtn);
        setDislikeImage(user.photoDislikeStatus(photo.getIdFoto()), dislikeBtn);

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.photoLikeStatus(photo.getIdFoto())){
                    user.removeLikePhoto(photo.getIdFoto(), view, photo);

                    // Deactivates like button and counter
                    setLikeImage(false, likeBtn);
                }
                else{
                    if (user.photoDislikeStatus(photo.getIdFoto())){
                        user.removeDislikePhoto(photo.getIdFoto(), view, photo);

                        // Update dislike button and counter if already used
                        setDislikeImage(false, dislikeBtn);
                        resDislikes.setText(String.valueOf(photo.getDislikes()));
                    }
                    user.addLikePhoto(photo.getIdFoto(), view, photo);

                    // Activates like button and counter
                    setLikeImage(true, likeBtn);
                }
                resLikes.setText(String.valueOf(photo.getLikes()));
            }
        });

        dislikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.photoDislikeStatus(photo.getIdFoto())){
                    user.removeDislikePhoto(photo.getIdFoto(), view, photo);

                    // Deactivates dislike button and counter
                    setDislikeImage(false, dislikeBtn);
                }
                else{
                    if (user.photoLikeStatus(photo.getIdFoto())){
                        user.removeLikePhoto(photo.getIdFoto(), view, photo);

                        // Update like button and counter if already used
                        setLikeImage(false, likeBtn);
                        resLikes.setText(String.valueOf(photo.getLikes()));
                    }
                    user.addDislikePhoto(photo.getIdFoto(), view, photo);

                    // Activates dislike button and counter
                    setDislikeImage(true, dislikeBtn);
                }
                resDislikes.setText(String.valueOf(photo.getDislikes()));
            }
        });
    }

    protected void setLikeImage(Boolean likeStatus, Button like){
        if (likeStatus){
            like.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24_positive);
        }
        else{
            like.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24);
        }
    }

    protected void setDislikeImage(Boolean dislikeStatus, Button dislike){
        if (dislikeStatus){
            dislike.setBackgroundResource(R.drawable.ic_baseline_thumb_down_24_positive);
        }
        else{
            dislike.setBackgroundResource(R.drawable.ic_baseline_thumb_down_24);
        }
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