package com.example.artcitytourapp.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.artcitytourapp.R;
import com.example.artcitytourapp.activities.Adapters.GalleryImagesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import Fotografia.Fotografia;
import Sitio.Sitio;
import Usuario.VisitanteSingleton;

public class GalleryFragment extends Fragment {
    View view;
    GalleryImagesAdapter imagenes;
    GridView gridViewImagenes;
    private Sitio site;
    private String idRoute;

    private AlertDialog photoDialog;
    private String description = "";
    private Uri photo = null;
    ActivityResultLauncher<String> mPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_gallery, container, false);
        imagenes = new GalleryImagesAdapter(getContext());
        Bundle b = this.getArguments();
        if (b != null) {
            site = (Sitio) b.get("Sitio");
            idRoute = (String) b.get("idRuta");
            loadData();
        }
        // Inflate the layout for this fragment
        gridViewImagenes = view.findViewById(R.id.grid_view_imagenes);
        gridViewImagenes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle b = new Bundle();
                b.putSerializable("photo", imagenes.ImagesArray[i]);
                b.putSerializable("Sitio", site);
                b.putSerializable("idRuta", idRoute);
                Navigation.findNavController(view).navigate(R.id.PhotoFragment, b);
            }
        });
        ImageView backBtn = (ImageView) view.findViewById(R.id.backImageGallery);
        backBtn.setClickable(true);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        mPhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        updatePhotoDialog(uri);
                    }
                });

        ImageView addPhoto = (ImageView) view.findViewById(R.id.addImageGallery);
        addPhoto.setClickable(true);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPhotoDialog();
            }
        });
        return view;
    }

    protected void loadData(){
        final TextView lblNameSite = view.findViewById(R.id.lblNameSite);
        lblNameSite.setText(site.getNombre());
        bdGetPhotosIdSite(site.getIdSite());
    }

    protected void cleanData(){
        description = "";
        photo = null;
    }

    protected void emptyValues(){
        new AlertDialog.Builder(requireContext())
                .setTitle("Debe rellenar todos los campos")
                .setMessage("La opinion o la calificacion del sitio estan vacias")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public void errorUploding(){
        new AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage("Ha ocurrido un error al subir la fotografía/reseña a la base de datos")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    protected void bdGetPhotosIdSite(String siteId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        db.collection("FotosXSitios")
                .whereEqualTo("idSitio", siteId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> fotosIDs = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                fotosIDs.add((String) document.getData().get("idFotografia"));
                            }
                            bdGetPhotosBySite(fotosIDs);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    protected void bdGetPhotosBySite( ArrayList<String> photosIDs){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for(String photoId : photosIDs){
            DocumentReference docRef = db.collection("Fotografia").document(photoId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Fotografia photo = document.toObject(Fotografia.class);
                            assert photo != null;
                            photo.setIdFoto(photoId);

                            imagenes.addImage(photo);
                            gridViewImagenes.setAdapter(imagenes);
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

    // photo selector dialog
    protected void createPhotoDialog(){
        AlertDialog.Builder photoDialogBuilder = new AlertDialog.Builder(requireContext());
        final View photoPopupWindow = getLayoutInflater().inflate(R.layout.photo_popup, null);
        TextView photoSite = (TextView) photoPopupWindow.findViewById(R.id.lblNameSite);

        EditText descriptionText = (EditText) photoPopupWindow.findViewById(R.id.descriptionEditText);
        Button photoSendBtn = (Button) photoPopupWindow.findViewById(R.id.sendBtn);
        LinearLayout photoCancelBtn = (LinearLayout) photoPopupWindow.findViewById(R.id.cancelLayout);
        LinearLayout addImage = (LinearLayout) photoPopupWindow.findViewById(R.id.addPhotosLinearLayout);

        photoSite.setText(site.getNombre());
        photoDialogBuilder.setView(photoPopupWindow);
        photoDialog = photoDialogBuilder.create();
        photoDialog.show();

        photoCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoDialog.hide();
            }
        });

        photoSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (descriptionText.getText().toString().equals("") || photo == null){
                    emptyValues();
                }
                else{
                    description = descriptionText.getText().toString();
                    uploadPhotoMedia();

                    photoDialog.hide();
                    cleanData();
                }
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhoto.launch("image/*");
            }
        });
    }

    protected void updatePhotoDialog(Uri uri){
        ImageView addImage = (ImageView) photoDialog.findViewById(R.id.imageView);
        assert addImage != null;
        addImage.setImageURI(uri);
        photo = uri;
    }

    // photo database methods
    protected void uploadPhotoMedia(){
        @SuppressLint("Recycle") Cursor returnCursor =
                getActivity().getApplicationContext().getContentResolver().query(photo, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String filename = formatter.format(now) + "_" + returnCursor.getString(nameIndex);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("FotografiasSitios/" + filename);
        storageReference.putFile(photo)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        /*storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                uploadPhoto(uri);
                            }
                        });*/
                        uploadPhoto("FotografiasSitios/" + filename);
                    }
                });
    }

    protected void uploadPhoto(String mediaUrl){
        VisitanteSingleton user = VisitanteSingleton.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("autor", user.getNombre());
        data.put("descripcion", description);
        data.put("fechaSubida", FieldValue.serverTimestamp());
        data.put("foto", mediaUrl);
        data.put("likes", 0);
        data.put("dislikes", 0);
        Task<DocumentReference> docRef = db.collection("Fotografia").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                        linkPhotoSite(documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUploding();
                        Log.w("Error", "Error adding document", e);
                    }
                });
    }

    protected void linkPhotoSite(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("idFotografia", id);
        data.put("idSitio", site.getIdSite());
        Task<DocumentReference> docRef = db.collection("FotosXSitios").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUploding();
                        Log.w("Error", "Error adding document", e);
                    }
                });
    }

}