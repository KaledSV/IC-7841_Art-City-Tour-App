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
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.artcitytourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import Sitio.Sitio;
import Usuario.VisitanteSingleton;

public class ReviewFragment extends Fragment {
    View view;
    TableLayout table;
    String palabraBuscada;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_review, container, false);
        final TextView fragmetTitle = view.findViewById(R.id.lblReviewTitle);
        final SearchView barraDeBusqueda = view.findViewById(R.id.searchView);
        barraDeBusqueda.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                palabraBuscada = s;
                table.removeAllViews();
                prepareTable();
                bdGetSitesVisited(true);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length()==0){
                    table.removeAllViews();
                    prepareTable();
                    bdGetSitesVisited(false);
                }
                return false;
            }
        });
        fragmetTitle.setText(getResources().getString(R.string.review));
        prepareTable();
        bdGetSitesVisited(false);
        return view;
    }


    protected void bdGetSitesVisited(boolean busquedaXNombre){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        VisitanteSingleton user = VisitanteSingleton.getInstance();
        // Create a new user with a first and last name
        db.collection("Visitados")
                .whereEqualTo("idUsuario", user.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> sitesIDs = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sitesIDs.add((String) document.getData().get("idSitio"));
                            }
                            //si el usuario coloca alguna palabra en el search view
                            if(busquedaXNombre){
                                if(palabraBuscada!=null){
                                    //Si el campo de texto es diferente a vacio
                                    if(palabraBuscada.length()!=0){
                                        bdGetSitesByLike(sitesIDs);
                                    }
                                }
                            }
                            else{
                                bdGetSites(sitesIDs);
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    //obtiene la lista de sitios visitados de un usuario
    //muestra sus sitios favoritos que contengan
    //un nombre parecido al buscado en el searchview
    protected void bdGetSitesByLike(ArrayList<String> sitesId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String x;
        final int[] i = {0};
        for(String siteId : sitesId){
            DocumentReference docRef = db.collection("Sitios").document(siteId);//.collection("nombre").startAt("museo del");
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String nombre = document.get("nombre").toString();
                            String nombre2 = nombre.toLowerCase();
                            if(nombre.startsWith(palabraBuscada)||nombre2.startsWith(palabraBuscada)){
                                Sitio site = document.toObject(Sitio.class);
                                assert site != null;
                                site.setCoordenadas((GeoPoint) Objects.requireNonNull(document.get("coordenadas")));
                                site.setIdSite(siteId);
                                bdGetSiteFoto(i, site);
                                i[0]++;
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

    //obtiene la lista de sitios visitados de un usuario
    //muestra sus sitios favoritos
    protected void bdGetSites(ArrayList<String> sitesId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final int[] i = {0};
        for(String siteId : sitesId){
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
                            bdGetSiteFoto(i, site);
                            i[0]++;
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

    protected void bdGetSiteFoto(int[] i, Sitio espSite){
        if (espSite.getIdFotoPredeterminada() == null){
            addTableRow(i[0], espSite, "Imagenes Interfaz/notFoundImage.png");
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
                                addTableRow(i[0], espSite, "Imagenes Interfaz/notFoundImage.png");
                            } else {
                                addTableRow(i[0], espSite, (String) Objects.requireNonNull(document.get("foto")));
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


    protected void addTableRow(int i, Sitio espSite, String imgPath) {
        TableRow siteRow = (TableRow)LayoutInflater.from(getContext()).inflate(R.layout.sites_row, null);

        ImageView siteImageView = (ImageView) siteRow.findViewById(R.id.siteImageView);
        TextView siteTextView = (TextView) siteRow.findViewById(R.id.siteTextView);
        TextView siteTypeTextView = (TextView) siteRow.findViewById(R.id.siteTypeTextView);

        imageRow(espSite, siteImageView, imgPath);
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
                Navigation.findNavController(view).navigate(R.id.siteFragmentReview, b);
            }
        });

        table.addView(siteRow);
    }

    protected void imageRow(Sitio espSite, ImageView iv, String imgPath){
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
}