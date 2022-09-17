package com.example.artcitytourapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.artcitytourapp.R;
import com.example.artcitytourapp.activities.Adapters.GalleryImagesAdapter;

public class GalleryFragment extends Fragment {
    View view;
    GridView gridViewImagenes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        // Inflate the layout for this fragment
        gridViewImagenes = view.findViewById(R.id.grid_view_imagenes);
        gridViewImagenes.setAdapter(new GalleryImagesAdapter(getContext()));
        gridViewImagenes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle b = new Bundle();
                b.putSerializable("idimagen", i);

                FullscreenImageFragment nextFrag= new FullscreenImageFragment();
                nextFrag.setArguments(b);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentContainer, nextFrag, "FullscreenFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;
    }
}