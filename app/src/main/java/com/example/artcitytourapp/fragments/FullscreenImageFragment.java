package com.example.artcitytourapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.artcitytourapp.R;
import com.example.artcitytourapp.activities.Adapters.GalleryImagesAdapter;

import Sitio.Sitio;


public class FullscreenImageFragment extends Fragment {
    View view;
    ImageView imageViewDetalle;
    GalleryImagesAdapter galleryImagesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fullscreen_image, container, false);
        Bundle b = this.getArguments();
        //recibir el id de la imagen mediante intent
        if (b != null) {
            int posicion = (int) b.get("idimagen");
            imageViewDetalle = view.findViewById(R.id.imagen_detalle);
            galleryImagesAdapter = new GalleryImagesAdapter(getContext());
            imageViewDetalle.setImageResource(galleryImagesAdapter.ImagesArray[posicion]);
        }

        return view;
    }
}