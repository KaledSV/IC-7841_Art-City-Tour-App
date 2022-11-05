package com.example.artcitytourapp.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.artcitytourapp.R;
import com.example.artcitytourapp.activities.Adapters.RecyclerListAdapter;
import com.example.artcitytourapp.activities.Adapters.SimpleItemTouchHelperCallback;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import Ruta.RutaPersonalizada;
import Sitio.SitioPersonalizado;

public class SubPlanningOrderSitesListFragment extends Fragment {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sub_planning_order_sites_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerListAdapter adapter = new RecyclerListAdapter();
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        Button btnCancelar = view.findViewById(R.id.btn_planning_cancel);
        Button btnAceptar = view.findViewById(R.id.btn_planning_save);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.planningFragment);
            }
        });
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ordenarSitiosPlan(adapter);
                Navigation.findNavController(view).navigate(R.id.planningFragment);
            }
        });

        return view;
    }
    public void ordenarSitiosPlan(RecyclerListAdapter adapter){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("RutaPersonalizada").document(RutaPersonalizada.getInstance().getIdMyRoute());
        ArrayList<String> idsSitiosPlanes = new ArrayList<>();
        for (int i=0;i<adapter.mItems.size();i++){
            idsSitiosPlanes.add(adapter.mItems.get(i).getIdSitioPersonalizado());
            Log.d("tag",adapter.mItems.get(i).getIdSitioPersonalizado());
        }
        docRef.update("sitiosPersonalizado",idsSitiosPlanes);
        RutaPersonalizada.getInstance().setMyRoutePersonalizedSitesIds(idsSitiosPlanes);
        RutaPersonalizada.getInstance().setMyRoute(adapter.mItems);

        //RutaPersonalizada.alterRutaPersonalizada(RutaPersonalizada.getInstance().getIdMyRoute(),RutaPersonalizada.getInstance().getIdSharedRoute());
        //RutaPersonalizada.getInstance().setMyRoutePersonalizedSitesIds(idsSitiosPlanes);
    }

}