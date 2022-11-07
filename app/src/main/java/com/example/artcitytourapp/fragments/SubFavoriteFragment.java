package com.example.artcitytourapp.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.artcitytourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import Ruta.RutaPersonalizada;
import Sitio.Sitio;
import Usuario.VisitanteSingleton;

public class SubFavoriteFragment extends Fragment {
    View view;
    TableLayout table;
    TableLayout table2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sub_favorite, container, false);
        table = view.findViewById(R.id.tableFavSitesList);
        table2 = view.findViewById(R.id.tableRecSitesList);
        bdGetSitesFavorite();
        bdGetSitesRecomendados();
        return view;
    }
    protected void bdGetSitesFavorite(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        VisitanteSingleton user = VisitanteSingleton.getInstance();
        for(String siteId : user.getSitiosFavoritos()){
            DocumentReference docRef = db.collection("Sitios").document(siteId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Sitio site = document.toObject(Sitio.class);
                            assert site != null;
                            site.setCoordenadas((GeoPoint) Objects.requireNonNull(document.get("coordenadas")));
                            site.setIdSite(siteId);
                            bdGetSiteFoto(site,false);
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    protected void bdGetSitesRecomendados(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Sitios")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Sitio site = document.toObject(Sitio.class);
                            site.setCoordenadas((GeoPoint) Objects.requireNonNull(document.get("coordenadas")));
                            site.setIdSite(document.getId());
                            final int random = new Random().nextInt(3);
                            if(random==0){
                                int band=0;
                                VisitanteSingleton user = VisitanteSingleton.getInstance();
                                for(String siteId : user.getSitiosFavoritos()){
                                    if(document.getId().equals(siteId)){
                                        band=1;
                                        break;
                                    }

                                }
                                if(band==0){
                                    bdGetSiteFoto(site,true);
                                }
                            }
                            //Log.d("TAG", document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                }
            });
    }

    protected void bdGetSiteFoto(Sitio espSite,boolean recomendados){
        if (espSite.getIdFotoPredeterminada() == null){
            addTableRowFav(espSite, "Imagenes Interfaz/notFoundImage.png",recomendados);
        }
        else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Fotografia").document(espSite.getIdFotoPredeterminada());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.get("foto") == null) {
                                addTableRowFav(espSite, "Imagenes Interfaz/notFoundImage.png",recomendados);
                            } else {
                                addTableRowFav(espSite, (String) Objects.requireNonNull(document.get("foto")),recomendados);
                            }
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                }
            });
        }
    }

    protected void addTableRowFav(Sitio espSite, String imgPath,boolean recomendados) {
        TableRow siteRow = (TableRow)LayoutInflater.from(getContext()).inflate(R.layout.fav_rows, null);
        VisitanteSingleton user = VisitanteSingleton.getInstance();

        ImageView siteImageView = (ImageView) siteRow.findViewById(R.id.siteImageView);
        ImageView heartImageView = (ImageView) siteRow.findViewById(R.id.heartImageView);
        TextView siteTextView = (TextView) siteRow.findViewById(R.id.siteTextView);
        TextView siteTypeTextView = (TextView) siteRow.findViewById(R.id.siteTypeTextView);
        ImageView addSiteImageView = (ImageView) siteRow.findViewById(R.id.addSiteImageView);
        RelativeLayout siteImageLayout = (RelativeLayout) siteRow.findViewById(R.id.siteImageLayout);

        //heartImageView.setImageResource(R.drawable.favorite_off);
        setFavoriteImage(user.siteFavoriteStatus(espSite), heartImageView);
        imageRow(siteImageView, imgPath);
        siteTextView.setText(espSite.getNombre());
        siteTypeTextView.setText(espSite.getTipoSitio());

        // clicks de la fila
        LinearLayout siteData = (LinearLayout) siteRow.findViewById(R.id.siteData);
        siteData.setClickable(true);
        siteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable("Sitio", espSite);
                //b.putSerializable("idRuta", idRoute);
                Navigation.findNavController(view).navigate(R.id.siteFragment, b);
            }
        });

        siteImageLayout.setClickable(true);
        siteImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.siteFavoriteStatus(espSite)){
                    user.bdRemoveFavorite(espSite.getIdSite(), view);
                    setFavoriteImage(false, heartImageView);
                }
                else{
                    user.bdAddFavorite(espSite.getIdSite(), view);
                    setFavoriteImage(true, heartImageView);
                }
            }
        });

        if (RutaPersonalizada.getInstance().getMyRouteSitesIds().contains(espSite.getIdSite())){
            addSiteImageView.setImageResource(R.drawable.ic_baseline_check_circle_24);
        }
        else{
            addSiteImageView.setImageResource(R.drawable.ic_baseline_add_google_24);
            addSiteImageView.setClickable(true);
            addSiteImageView.setOnClickListener(v -> {
                Snackbar mySnackbar = Snackbar.make(view, "Agregado al plan", Snackbar.LENGTH_LONG);
                mySnackbar.setAction("ver plan", new MyUndoListener(view));
                mySnackbar.show();

                RutaPersonalizada.getInstance().addSiteMyRoute(espSite, view);
                addSiteImageView.setImageResource(R.drawable.ic_baseline_check_circle_24);
            });
        }
        if(recomendados){
            table2.addView(siteRow);
        }
        else{
            table.addView(siteRow);
        }
    }

    protected void imageRow(ImageView iv, String imgPath){
        StorageReference pathReference  = FirebaseStorage.getInstance().getReference(imgPath);
        try {
            File localFile = File.createTempFile("tempFile", imgPath.substring(imgPath.lastIndexOf(".")));
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 125, 125, false));
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    protected void setFavoriteImage(Boolean status, ImageView iv){
        if (status){
            iv.setImageResource(R.drawable.favorite_on);
        }
        else{
            iv.setImageResource(R.drawable.favorite_off);
        }
    }
}