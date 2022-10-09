package com.example.artcitytourapp.fragments;

import android.graphics.Typeface;
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
import android.widget.TextView;

import com.example.artcitytourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import Sitio.Sitio;


public class ScheduleFragment extends Fragment {
    private Sitio site;
    private View view;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_schedule, container, false);
        // load all data from database
        Bundle b = this.getArguments();
        if (b != null) {
            site = (Sitio) b.get("Sitio");
            bdGetHorarioIdSite(site.getIdSite(),site.getNombre());
        }

        ImageView backBtn = (ImageView) view.findViewById(R.id.backButton);
        backBtn.setClickable(true);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });
        return view;
    }
    protected void bdGetHorarioIdSite(String siteId,String nombre){
        LinearLayout layoutHorarios = view.findViewById(R.id.layoutHorarios);
        TextView titulo = view.findViewById(R.id.NombreSitio);
        titulo.setText(nombre);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        db.collection("Horario")
                .whereEqualTo("idSitio", siteId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String abierto = (String) document.getData().get("abierto");
                                String cerrado = (String) document.getData().get("cerrado");
                                String dia = (String) document.getData().get("dia");
                                LinearLayout ly = new LinearLayout(getContext());
                                ly.setOrientation(LinearLayout.VERTICAL);
                                TextView textview1 = new TextView(getContext());
                                textview1.setText(abierto);
                                TextView textview2 = new TextView(getContext());
                                textview2.setText(cerrado);
                                TextView textview3 = new TextView(getContext());
                                textview3.setText(dia);
                                textview3.setTypeface(null, Typeface.BOLD);
                                textview3.setTextSize(18);
                                ly.addView(textview3);
                                ly.addView(textview1);
                                ly.addView(textview2);
                                ly.setPadding(30,30,30,30);
                                layoutHorarios.addView(ly);
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}