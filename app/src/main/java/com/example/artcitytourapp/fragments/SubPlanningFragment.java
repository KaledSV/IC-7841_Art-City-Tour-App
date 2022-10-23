package com.example.artcitytourapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.artcitytourapp.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import Ruta.RutaPersonalizada;
import Sitio.SitioPersonalizado;

public class SubPlanningFragment extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sub_planning, container, false);

        for (SitioPersonalizado site : RutaPersonalizada.getInstance().getMyRoute()){
            Log.d("WEEEEEEEEEEEEEEEEEEEe", site.getFieldValues());
        }
        for (SitioPersonalizado site : RutaPersonalizada.getInstance().getSharedRoute()){
            Log.d("eEEEEEEEEEEEEEEEEEEEW", site.getFieldValues());
        }
        Log.d("WEEEEEEEEEEEE",  RutaPersonalizada.getInstance().getName() + " " +  RutaPersonalizada.getInstance().getCantSitios());
        Log.d("WEEEEEEEEEEEE",  RutaPersonalizada.getInstance().getMyRoutePersonalizedSitesIds().toString());
        Log.d("WEEEEEEEEEEEE",  RutaPersonalizada.getInstance().getMyRouteSitesIds().toString());
        loadData();
        return view;
    }

    public void loadData(){
        RutaPersonalizada ruta = RutaPersonalizada.getInstance();
        String cantidad = String.valueOf(ruta.getCantSitios());
        if (ruta.getCantSitios() > 1){
            cantidad += " sitios";
        }
        else{
            cantidad += " sitio";
        }

        final EditText editPlanTitle = view.findViewById(R.id.editPlanTitle);
        final TextView lblNumSites = view.findViewById(R.id.lblNumSites);
        final SwitchMaterial orderSwitch = view.findViewById(R.id.orderSwitch);
        final SwitchMaterial listSwitch = view.findViewById(R.id.listSwitch);
        final TextView listDes = view.findViewById(R.id.listDes);

        editPlanTitle.setText(ruta.getName());
        lblNumSites.setText(cantidad);
        orderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FragmentContainerView listContainer = view.findViewById(R.id.listContainer);
                listContainer.removeAllViewsInLayout();
                if (b){
                    SubPlanningOrderSitesListFragment subPlanningOrder = new SubPlanningOrderSitesListFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.listContainer, subPlanningOrder, "subPlanningOrderListFragment")
                            .addToBackStack(null)
                            .commit();
                }
                else{
                    // todo stop editing and save changes
                    SubPlanningMyRouteListFragment subPlanningMyRoute = new SubPlanningMyRouteListFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.listContainer, subPlanningMyRoute, "subPlanningMyRouteListFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        listSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                FragmentContainerView listContainer = view.findViewById(R.id.listContainer);
                listContainer.removeAllViewsInLayout();
                if (b){
                    listDes.setText(R.string.compartida);
                    SubPlanningSharedRouteListFragment subPlanningSharedRoute = new SubPlanningSharedRouteListFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.listContainer, subPlanningSharedRoute, "SubPlanningSharedRouteListFragment")
                            .addToBackStack(null)
                            .commit();
                }
                else{
                    listDes.setText(R.string.propia);
                    SubPlanningMyRouteListFragment subPlanningMyRoute = new SubPlanningMyRouteListFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.listContainer, subPlanningMyRoute, "subPlanningMyRouteListFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }
}