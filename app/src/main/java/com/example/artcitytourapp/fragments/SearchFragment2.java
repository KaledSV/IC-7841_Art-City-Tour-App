package com.example.artcitytourapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.artcitytourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import Sitio.Sitio;
import Usuario.VisitanteSingleton;

public class SearchFragment2 extends Fragment {
    View view;
    TableLayout table;
    String palabraBuscada1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_search2, container, false);
        final SearchView barraDeBusqueda1 = view.findViewById(R.id.searchViewSearch2);
        final Button bf1 = view.findViewById(R.id.botonf1);
        final Button bf2 = view.findViewById(R.id.botonf2);
        final Button bf3 = view.findViewById(R.id.botonf3);
        final Button bf4 = view.findViewById(R.id.botonf4);
        bf1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putSerializable("tipoFiltro", 1);
                Navigation.findNavController(view).navigate(R.id.closeFragment,b);
            }
        });
        bf2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putSerializable("tipoFiltro", 2);
                Navigation.findNavController(view).navigate(R.id.closeFragment,b);
            }
        });
        bf3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putSerializable("tipoFiltro", 3);
                Navigation.findNavController(view).navigate(R.id.closeFragment,b);
            }
        });
        bf4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putSerializable("tipoFiltro", 4);
                Navigation.findNavController(view).navigate(R.id.closeFragment,b);
            }
        });
        barraDeBusqueda1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                palabraBuscada1 = s;
                table.removeAllViews();
                prepareTable();
                bdGetSites(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length()==0){
                    table.removeAllViews();
                    prepareTable();
                    bdGetSites(false);
                }
                return false;
            }
        });
        prepareTable();
        bdGetSites(false);
        return view;
    }



    protected void bdGetSites(boolean busquedaXNomb){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Sitios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nombre = document.get("nombre").toString();
                                String nombre2 = nombre.toLowerCase();
                                if(busquedaXNomb){
                                    if(nombre.startsWith(palabraBuscada1)||nombre2.startsWith(palabraBuscada1)){
                                        Sitio site = document.toObject(Sitio.class);
                                        assert site != null;
                                        site.setCoordenadas((GeoPoint) Objects.requireNonNull(document.get("coordenadas")));
                                        site.setIdSite(document.getId());
                                        bdGetSiteFoto(site);
                                        //Log.d("TAG", document.getId() + " => " + document.getData());
                                    }
                                }else{
                                    Sitio site = document.toObject(Sitio.class);
                                    assert site != null;
                                    site.setCoordenadas((GeoPoint) Objects.requireNonNull(document.get("coordenadas")));
                                    site.setIdSite(document.getId());
                                    bdGetSiteFoto(site);
                                    //Log.d("TAG", document.getId() + " => " + document.getData());
                                }
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    protected void bdGetSiteFoto(Sitio espSite){
        if (espSite.getIdFotoPredeterminada() == null){
            addTableRow(espSite, "Imagenes Interfaz/notFoundImage.png");
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
                                addTableRow(espSite, "Imagenes Interfaz/notFoundImage.png");
                            } else {
                                addTableRow(espSite, (String) Objects.requireNonNull(document.get("foto")));
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

    protected void prepareTable(){
        table = view.findViewById(R.id.tableSitesList);
        table.setStretchAllColumns(true);
        table.setWeightSum(1); //numero de columnas
    }

    protected void addTableRow(Sitio espSite, String imgPath) {
        TableRow siteRow = (TableRow)LayoutInflater.from(getContext()).inflate(R.layout.sites_row, null);
        VisitanteSingleton user = VisitanteSingleton.getInstance();

        ImageView siteImageView = (ImageView) siteRow.findViewById(R.id.siteImageView);
        ImageView heartImageView = (ImageView) siteRow.findViewById(R.id.heartImageView);
        TextView siteTextView = (TextView) siteRow.findViewById(R.id.siteTextView);
        TextView siteTypeTextView = (TextView) siteRow.findViewById(R.id.siteTypeTextView);
        RelativeLayout siteImageLayout = (RelativeLayout) siteRow.findViewById(R.id.siteImageLayout);

        //heartImageView.setImageResource(R.drawable.favorite_off);
        setFavoriteImage(user.siteFavoriteStatus(espSite), heartImageView);
        imageRow(siteImageView, imgPath);
        siteTextView.setText(espSite.getNombre());
        siteTypeTextView.setText(espSite.getTipoSitio());

        // click de la fila
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
        table.addView(siteRow);
    }
    protected void imageRow(ImageView iv, String imgPath){
        StorageReference pathReference  = FirebaseStorage.getInstance().getReference(imgPath);
        try {
            File localFile = File.createTempFile("tempFile", imgPath.substring(imgPath.lastIndexOf(".")));
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 250, 250, false));
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