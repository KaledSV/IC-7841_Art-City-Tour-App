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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.artcitytourapp.R;
import com.example.artcitytourapp.activities.Adapters.GalleryImagesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Fotografia.Fotografia;
import Sitio.Sitio;

public class GalleryFragment extends Fragment {
    View view;
    GalleryImagesAdapter imagenes;
    GridView gridViewImagenes;
    private Sitio site;
    private String idRoute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        imagenes = new GalleryImagesAdapter(getContext());
        Bundle b = this.getArguments();
        if (b != null) {
            site = (Sitio) b.get("Sitio");
            idRoute = (String) b.get("idRuta");
            loadData();
        }
        // Inflate the layout for this fragment
        gridViewImagenes = view.findViewById(R.id.grid_view_imagenes);
        gridViewImagenes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle b = new Bundle();
                b.putSerializable("photo", imagenes.ImagesArray[i]);
                Navigation.findNavController(view).navigate(R.id.PhotoFragment, b);
            }
        });
        return view;
    }

    protected void loadData(){
        bdGetPhotosIdSite(site.getIdSite());
    }

    protected void bdGetPhotosIdSite(String siteId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        db.collection("FotosXSitios")
                .whereEqualTo("idSitio", siteId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> fotosIDs = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                fotosIDs.add((String) document.getData().get("idFotografia"));
                            }
                            bdGetPhotosBySite(fotosIDs);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    protected void bdGetPhotosBySite( ArrayList<String> photosIDs){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for(String photoId : photosIDs){
            DocumentReference docRef = db.collection("Fotografia").document(photoId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Fotografia photo = document.toObject(Fotografia.class);
                            assert photo != null;
                            photo.setIdFoto(photoId);

                            imagenes.addImage(photo);
                            gridViewImagenes.setAdapter(imagenes);
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



}