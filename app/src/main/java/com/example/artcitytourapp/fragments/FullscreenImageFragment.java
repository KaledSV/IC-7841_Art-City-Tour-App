package com.example.artcitytourapp.fragments;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;

import Fotografia.Fotografia;
import Resenna.Resenna;
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

        Button likeBtn = descripcionWindow.findViewById(R.id.likeBtn);
        Button dislikeBtn = descripcionWindow.findViewById(R.id.dislikeBtn);
        Button shareBtn = descripcionWindow.findViewById(R.id.shareImage_Fullscreen);

        setLikeAndDislikeBtn(photo, likeBtn, dislikeBtn, resLikes, resDislikes);

        bdGetPhoto(imageViewDetalle, photo);

        setShareButton(shareBtn);
    }

    protected void setShareButton(Button shrBtn) {
        ImageView imageViewDetalle = view.findViewById(R.id.imagen_detalle);
        shrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage(imageViewDetalle.getDrawingCache());
            }
        });

    }

    @SuppressLint("RestrictedApi")
    private void shareImage(Bitmap image) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("image/jpeg");
        Uri bmpUri;
        String base = "Prueba envío imagenes";
        bmpUri = saveImage(image,getApplicationContext());
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sendIntent.putExtra(Intent.EXTRA_STREAM,bmpUri);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT,"Art City Tour App");
        sendIntent.putExtra(Intent.EXTRA_TEXT,base);
        startActivity(Intent.createChooser(sendIntent,"Compartir Imagen"));
    }

    private static Uri saveImage(Bitmap image, Context context){
        File imageFolder = new File(context.getCacheDir(),"images");
        Uri uri = null;
        try{
            imageFolder.mkdirs();
            File file = new File(imageFolder,"shared_images.jpg");
            FileOutputStream stream = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.JPEG,90,stream);

            stream.flush();
            stream.close();

            uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()),"com.example.artcitytourapp"+".provider", file);

        } catch (FileNotFoundException e) {
            Log.d("FNF","Exception"+e.getMessage());
        } catch (IOException e) {
            Log.d("Tag","Exception"+e.getMessage());
        }

        return uri;
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