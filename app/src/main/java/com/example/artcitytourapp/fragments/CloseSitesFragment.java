package com.example.artcitytourapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.artcitytourapp.R;

public class CloseSitesFragment extends Fragment {
    public static int argumento1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle b = getArguments();
        if (b != null) {
            argumento1 = (int) b.get("tipoFiltro");
        }
        return inflater.inflate(R.layout.fragment_close_sites, container, false);
    }
    public static int getArgumento1(){
        return argumento1;
    }

    public static void setArgumento1(int argumento1) {
        CloseSitesFragment.argumento1 = argumento1;
    }
}