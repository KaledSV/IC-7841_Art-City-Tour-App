package com.example.artcitytourapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.artcitytourapp.R;
import com.example.artcitytourapp.activities.Adapters.GalleryImagesAdapter;


public class FullscreenImageFragment extends Fragment {
    View view;
    ImageView imageViewDetalle;
    GalleryImagesAdapter galleryImagesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fullscreen_image, container, false);
        final View descripcionWindow = getLayoutInflater().inflate(R.layout.fragment_resena, null);
        LinearLayout layoutDescripcionImagen = view.findViewById(R.id.layoutDescripcionImagen);
        layoutDescripcionImagen.addView(descripcionWindow);
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