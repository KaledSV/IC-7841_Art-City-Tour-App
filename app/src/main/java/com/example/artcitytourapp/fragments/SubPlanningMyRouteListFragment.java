package com.example.artcitytourapp.fragments;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import Ruta.RutaPersonalizada;
import Sitio.Sitio;
import Sitio.SitioPersonalizado;
import Usuario.VisitanteSingleton;

public class SubPlanningMyRouteListFragment extends Fragment {
    View view;
    TableLayout table;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_sub_planning_my_route_list, container, false);
        loadData();
        return view;
    }

    public void loadData(){
        table = view.findViewById(R.id.tableSitesList);
        table.setStretchAllColumns(true);
        table.setWeightSum(1);
        if (RutaPersonalizada.getInstance().getMyRoute().size()>0){
            // remove other buttons and labels
            TextView addSitesTextViews = view.findViewById(R.id.addSitesTextViews);
            TextView personalizeTextView = view.findViewById(R.id.personalizeTextView);
            Button exploreBtn = view.findViewById(R.id.exploreBtn);
            addSitesTextViews.setVisibility(View.GONE);
            personalizeTextView.setVisibility(View.GONE);
            exploreBtn.setVisibility(View.GONE);

            // set table and plus button
            ImageView optionsBtn = view.findViewById(R.id.optionsBtn);
            optionsBtn.setClickable(true);
            optionsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo boton de explorar
                }
            });
            for (SitioPersonalizado site : RutaPersonalizada.getInstance().getMyRoute()){
                bdGetSiteFoto(site);
            }
        }
        else{
            table.setVisibility(View.GONE);
            Button exploreBtn = view.findViewById(R.id.exploreBtn);
            exploreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //todo boton de explorar
                }
            });
        }
    }

    protected void addTableRow(SitioPersonalizado site, String imgPath) {
        TableRow siteRow = (TableRow)LayoutInflater.from(getContext()).inflate(R.layout.planning_row, null);

        ImageView siteImageView = (ImageView) siteRow.findViewById(R.id.siteImageView);
        TextView siteScheduleTextView = (TextView) siteRow.findViewById(R.id.siteScheduleTextView);
        TextView siteTextView = (TextView) siteRow.findViewById(R.id.siteTextView);
        TextView siteTypeTextView = (TextView) siteRow.findViewById(R.id.siteTypeTextView);
        EditText editTextComment = (EditText) siteRow.findViewById(R.id.editTextComment);
        ImageView removeBtn = (ImageView) siteRow.findViewById(R.id.removeBtn);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        imageRow(siteImageView, imgPath);
        siteTextView.setText(site.getNombre());
        siteTypeTextView.setText(site.getTipoSitio());
        editTextComment.setText(site.getComentario());

        if (site.getHoraVisita().compareTo(new Date()) < 0)
            siteScheduleTextView.setText(R.string.add_schedule);
        else
            siteScheduleTextView.setText(formatter.format(site.getHoraVisita()));

        siteScheduleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // todo change schedule
            }
        });

        // todo change comentary

        removeBtn.setClickable(true);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DetachAndAttachSameFragment")
            @Override
            public void onClick(View view) {
                RutaPersonalizada.getInstance().removeSiteMyRouteList(site, view);
            }
        });

        table.addView(siteRow);
    }

    protected void bdGetSiteFoto(SitioPersonalizado site){
        if (site.getIdFotoPredeterminada() == null){
            addTableRow(site, "Imagenes Interfaz/notFoundImage.png");
        }
        else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Fotografia").document(site.getIdFotoPredeterminada());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            if (document.get("foto") == null) {
                                addTableRow(site, "Imagenes Interfaz/notFoundImage.png");
                            } else {
                                addTableRow(site, (String) Objects.requireNonNull(document.get("foto")));
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

    protected void imageRow(ImageView iv, String imgPath){
        StorageReference pathReference  = FirebaseStorage.getInstance().getReference(imgPath);
        try {
            File localFile = File.createTempFile("tempFile", imgPath.substring(imgPath.lastIndexOf(".")));
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 180, 180, false));
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}