package com.example.artcitytourapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
    static FragmentContainerView listContainer;
    static FragmentActivity activity;
    static SwitchMaterial listSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sub_planning, container, false);

        loadData();
        return view;
    }

    public static FragmentContainerView getContainer(){
        if (listContainer == null){
            return null;
        }
        return listContainer;
    }

    public static FragmentActivity getActivityContainer(){
        return activity;
    }

    public static SwitchMaterial getSwitch(){
        return listSwitch;
    }

    public void loadData(){
        RutaPersonalizada ruta = RutaPersonalizada.getInstance();

        final EditText editPlanTitle = view.findViewById(R.id.editPlanTitle);
        final TextView lblNumSites = view.findViewById(R.id.lblNumSites);
        final SwitchMaterial orderSwitch = view.findViewById(R.id.orderSwitch);
        listSwitch = view.findViewById(R.id.listSwitch);
        final TextView listDes = view.findViewById(R.id.listDes);
        listContainer = view.findViewById(R.id.listContainer);
        activity = getActivity();

        editPlanTitle.setText(ruta.getName());
        changeNumberSites(lblNumSites, true);
        orderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listContainer.removeAllViewsInLayout();
                if (b){
                    SubPlanningOrderSitesListFragment subPlanningOrder = new SubPlanningOrderSitesListFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.listContainer, subPlanningOrder, "subPlanningOrderListFragment")
                            .addToBackStack(null)
                            .commit();
                }
                else{
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
                    changeNumberSites(lblNumSites, false);
                    SubPlanningSharedRouteListFragment subPlanningSharedRoute = new SubPlanningSharedRouteListFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.listContainer, subPlanningSharedRoute, "SubPlanningSharedRouteListFragment")
                            .addToBackStack(null)
                            .commit();
                }
                else{
                    listDes.setText(R.string.propia);
                    changeNumberSites(lblNumSites, true);
                    SubPlanningMyRouteListFragment subPlanningMyRoute = new SubPlanningMyRouteListFragment();
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.listContainer, subPlanningMyRoute, "subPlanningMyRouteListFragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
    }

    void changeNumberSites(TextView lblNumSites, Boolean personal){
        int i = 0;
        String cantidad = "";
        if (personal){
            i = RutaPersonalizada.getInstance().getCantSitios();
        }else{
            i = RutaPersonalizada.getInstance().getSharedRoute().size();
        }

        cantidad = String.valueOf(i);
        if (i > 1){
            cantidad += " sitios";
        }
        else{
            cantidad += " sitio";
        }
        lblNumSites.setText(cantidad);
    }
}