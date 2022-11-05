package com.example.artcitytourapp.activities.Adapters;/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.artcitytourapp.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import Ruta.RutaPersonalizada;
import Sitio.SitioPersonalizado;

public class RecyclerListAdapter extends RecyclerView.Adapter<ItemViewHolder> implements ItemTouchHelperAdapter{
    public final List<SitioPersonalizado> mItems = new ArrayList<>();
    public RecyclerListAdapter() {
        Log.d("Tag",String.valueOf(RutaPersonalizada.getInstance().getMyRoute().size()));
        mItems.addAll(RutaPersonalizada.getInstance().getMyRoute());
    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.planning_row_order, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        bdGetSiteFoto(holder,mItems.get(position));
    }
    protected void addTableRow(ItemViewHolder holder, SitioPersonalizado site, String imgPath) {
        ImageView siteImageView = holder.itemView.findViewById(R.id.imgSitioPlan);
        TextView siteTextView = holder.itemView.findViewById(R.id.txtTituloSitioPlan);
        TextView siteTypeTextView = holder.itemView.findViewById(R.id.txtDesSitioPlan);
        TextView dragAndDrop = holder.itemView.findViewById(R.id.btnSitioPlanDD);
        imageRow(siteImageView, imgPath);
        siteTextView.setText(site.getNombre());
        siteTypeTextView.setText(site.getTipoSitio());
    }

    protected void imageRow(ImageView iv, String imgPath){
        StorageReference pathReference  = FirebaseStorage.getInstance().getReference(imgPath);
        try {
            File localFile = File.createTempFile("tempFile", imgPath.substring(imgPath.lastIndexOf(".")));
            pathReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 180, 180, false));
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    protected void bdGetSiteFoto(ItemViewHolder holder,SitioPersonalizado site){
        if (site.getIdFotoPredeterminada() == null){
            addTableRow(holder, site, "Imagenes Interfaz/notFoundImage.png");
        }
        else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Fotografia").document(site.getIdFotoPredeterminada());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.get("foto") == null) {
                            addTableRow(holder,site, "Imagenes Interfaz/notFoundImage.png");
                        } else {
                            addTableRow(holder,site, (String) Objects.requireNonNull(document.get("foto")));
                        }
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return mItems.size();
    }
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }
    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }
}