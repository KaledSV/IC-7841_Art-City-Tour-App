package com.example.artcitytourapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.artcitytourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import Ruta.Ruta;
import Sitio.Sitio;
import Usuario.VisitanteSingleton;

public class RoutesFragment extends Fragment {
    View view;
    TableLayout table;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_routes, container, false);

        ImageView appLogo = (ImageView) view.findViewById(R.id.appLogo);
        appLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect();
            }
        });

        // data of fragment
        // todo remove
        VisitanteSingleton user = VisitanteSingleton.AlterSingleton("1", "Kaled", 86254968, "kaledsv@gmail.com", "password");
        prepareTable();
        loadData();
        return view;
    }

    private void redirect() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://artcitytour.gamcultural.com/"));
        startActivity(browserIntent);
    }

    protected void loadData(){
        final ImageView appLogo = (ImageView) view.findViewById(R.id.appLogo);
        loadImage(appLogo, "Imagenes Interfaz/InfoLogo.png");
        bdGetRoutes();
    }

    protected void prepareTable(){
        table = view.findViewById(R.id.routesTable);
        table.setStretchAllColumns(true);
        table.setWeightSum(2); //numero de columnas
    }

    protected void loadImage(ImageView iv, String imgPath){
        if (imgPath == null){
            imgPath = "Imagenes Interfaz/notFoundImage.png";
        }
        StorageReference pathReference  = FirebaseStorage.getInstance().getReference(imgPath);

        try {
            File localFile = File.createTempFile("tempFile", ".png");
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 1000, 1000, false));
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    protected void bdGetRoutes(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        db.collection("Rutas")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Ruta> routes = new ArrayList<Ruta>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Ruta route = document.toObject(Ruta.class);
                                route.setIdRoute((String) document.getId());
                                routes.add(route);
                            }
                            for(int i = 0; i < routes.size()/2; i++){
                                addTwoRoute(routes.get(i*2), routes.get(i*2+1));
                            }
                            if (routes.size()%2 == 1){
                                addOneRoute(routes.get(routes.size()-1));
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    protected void addTwoRoute(Ruta route, Ruta route2){
        TableRow row = (TableRow)LayoutInflater.from(getContext()).inflate(R.layout.routes_row, null);
        ImageView iv = (ImageView) row.findViewById(R.id.imageView);
        TextView tiv = (TextView) row.findViewById(R.id.imageViewText);

        ImageView iv2 = (ImageView) row.findViewById(R.id.imageView2);
        TextView tiv2 = (TextView) row.findViewById(R.id.imageViewText2);

        tiv.setText(route.getNombre());
        tiv2.setText(route2.getNombre());
        loadImage(iv, route.getFotoPredeterminada());
        loadImage(iv2, route2.getFotoPredeterminada());

        setRedirect(iv, route);
        setRedirect(iv2, route2);

        table.addView(row);
    }

    protected void addOneRoute(Ruta route){
        Log.d(route.getIdRoute(), route.getFieldValues());
        TableRow row = (TableRow)LayoutInflater.from(getContext()).inflate(R.layout.routes_row, null);
        ImageView iv = (ImageView) row.findViewById(R.id.imageView);
        TextView tiv = (TextView) row.findViewById(R.id.imageViewText);

        tiv.setText(route.getNombre());
        loadImage(iv, route.getFotoPredeterminada());
        setRedirect(iv, route);
        table.addView(row);
    }

    protected void setRedirect(ImageView iv, Ruta route){
        iv.setClickable(true);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable("idRuta", route.getIdRoute());
                Navigation.findNavController(view).navigate(R.id.routeSitesFragment, b);
                /*
                Bundle b = new Bundle();
                b.putSerializable("idRuta", route.getIdRoute());

                SitesFragment nextFrag = new SitesFragment();
                nextFrag.setArguments(b);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentContainer, nextFrag, "RouteFragment")
                        .addToBackStack(null)
                        .commit();*/
            }
        });
    }


}