package com.example.artcitytourapp.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.fragment.app.FragmentResultListener;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.artcitytourapp.R;
import com.example.artcitytourapp.activities.Adapters.GalleryImagesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import Sitio.Sitio;
import Usuario.VisitanteSingleton;

public class SiteFragment extends Fragment {
    View view;
    private AlertDialog resenaDialog, resenaAndPhotoDialog, resenaPhotosDialog, photoDialog;
    ActivityResultLauncher<String> mPhoto, mPhotos;
    private Sitio site;
    private String idRoute;

    private int calification = 0;
    private String opinion = "";
    private String description = "";
    private ArrayList<Uri> photos = new ArrayList<>();
    private Uri photo = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_site, container, false);

        // Data of fragment
        ExpandableTextView expTv = (ExpandableTextView) view.findViewById(R.id.expand_text_view);
        expTv.setText(getString(R.string.description_SiteInfo));

        Button addResena = (Button) view.findViewById(R.id.addReviews_SiteDes);
        addResena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createResenaDialog();
            }
        });

        Button addPhoto = (Button) view.findViewById(R.id.addPhoto_SiteDes);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPhotoDialog();
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

        mPhotos = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        updateResenaPhotosDialog(uri);
                    }
                });
        // load all data from database
        Bundle b = this.getArguments();
        if (b != null) {
            site = (Sitio) b.get("Sitio");
            idRoute = (String) b.get("idRuta");
            loadData();
        }

        return view;
    }


    // Activity methods
    @SuppressLint("SetTextI18n")
    protected void loadData(){
        Log.w("TAG", site.getFieldValues());
        final TextView lblNameRoute = view.findViewById(R.id.siteRoute_SiteInfo);
        final TextView lblNameSite = view.findViewById(R.id.siteName_SiteInfo);
        final ExpandableTextView lblDescription = (ExpandableTextView) view.findViewById(R.id.expand_text_view);
        final TextView lblexpectedTime = view.findViewById(R.id.timeExp_SiteInfo);
        final TextView lblcapacity = view.findViewById(R.id.capacity_SiteInfo);
        final TextView lblday = view.findViewById(R.id.day_SiteInfo); //todo para horarios
        final TextView lbltimeRange = view.findViewById(R.id.timeRange_SiteInfo); //todo para horarios;

        lblNameRoute.setText(site.getNombreRuta());
        lblNameSite.setText(site.getNombre());
        lblDescription.setText(site.getDescripcion());
        lblexpectedTime.setText(Integer.toString(site.getTiempoEspera()));
        lblcapacity.setText(Integer.toString(site.getCapacidad()));
        //lblday.setText(); todo
        //lbltimeRange.setText(); todo

        bdGetReviewsBySite("1");
        GalleryImagesAdapter imagenes = new GalleryImagesAdapter(getContext());
        for(int image: imagenes.ImagesArray){
            flipperImages(image);
        }
    }

    //Recibe el id del sitio y encuentra las reseñas de ese sitio
    public void flipperImages(int image){
        ViewFlipper vFlipper = view.findViewById(R.id.viewFlipper2);
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(image);
        vFlipper.addView(imageView);
        vFlipper.setFlipInterval(3500);
        vFlipper.setAutoStart(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Bundle b = new Bundle();
                b.putSerializable("Sitio", espSite);*/

                GalleryFragment nextFrag= new GalleryFragment();
                //nextFrag.setArguments(b);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.contentContainer, nextFrag, "GalerryFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    protected void bdGetReviewsBySite(String SiteId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ResenasXSitios")
                .whereEqualTo("idSitio", SiteId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> resenaIDs = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                resenaIDs.add((String) document.getData().get("idResena"));
                            }
                            bdGetReviewsBySiteAux(resenaIDs);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    protected void bdGetReviewsBySiteAux(ArrayList<String> resenaIDs){
        TableLayout varTbl = view.findViewById(R.id.tablaResenas);
        /*LinearLayout varL = view.findViewById(R.id.linearLayout3);
        ImageView imageView1 = new ImageView(getContext());
        Bitmap imagenOriginal = BitmapFactory.decodeResource(getResources(),R.drawable.m1);
        Bitmap imagenFinal = Bitmap.createScaledBitmap(imagenOriginal,100,100,false);
        imageView1.setImageBitmap(imagenFinal);
        varL.addView(imageView1);*/
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final int[] i = {0};
        for(String resenaId : resenaIDs){
            TableRow varTblR = new TableRow(getContext());
            TableRow varTblR2 = new TableRow(getContext());
            TableRow varTblR3 = new TableRow(getContext());
            TextView textView1 = new TextView(getContext());
            TextView textView2 = new TextView(getContext());
            DocumentReference docRef = db.collection("Resena").document(resenaId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            //Resenna resena = document.toObject(Resenna.class);
                            Log.d("PRUEBA1", (String) document.get("comentario"));
                            String perfil = (String) document.get("autor");
                            Timestamp fecha = (Timestamp) document.get("fechaSubida");

                            java.sql.Date timeD = new java.sql.Date(fecha.getSeconds() * 1000);
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            String fecha2 = sdf.format(timeD);
                            perfil = perfil + "\n" + fecha2;

                            textView1.setText(perfil);
                            textView2.setText((String) document.get("comentario"));

                            varTblR.addView(textView1);
                            varTblR2.addView(textView2);
                            varTbl.addView(varTblR);
                            varTbl.addView(varTblR2);
                            i[0]++;
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

    protected void cleanData(){
        opinion = "";
        calification = 0;
        photos.clear();
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

    // addReview dialog
    protected void createResenaDialog(){
        AlertDialog.Builder resenaDialogBuilder = new AlertDialog.Builder(requireContext());
        final View resenaPopupWindow = getLayoutInflater().inflate(R.layout.resena_popup, null);
        TextView resenaSite = (TextView) resenaPopupWindow.findViewById(R.id.lblNameSite);
        EditText opinionText = (EditText) resenaPopupWindow.findViewById(R.id.opinionText);
        Button resenaSendBtn = (Button) resenaPopupWindow.findViewById(R.id.sendBtn);
        LinearLayout resenaCancelBtn = (LinearLayout) resenaPopupWindow.findViewById(R.id.cancelLayout);

        Button resenaOneBtn = (Button) resenaPopupWindow.findViewById(R.id.resenaOneBtn);
        Button resenaTwoBtn = (Button) resenaPopupWindow.findViewById(R.id.resenaTwoBtn);
        Button resenaThreeBtn = (Button) resenaPopupWindow.findViewById(R.id.resenaThreeBtn);
        Button resenaFourBtn = (Button) resenaPopupWindow.findViewById(R.id.resenaFourBtn);
        Button resenaFiveBtn = (Button) resenaPopupWindow.findViewById(R.id.resenaFiveBtn);

        resenaSite.setText(site.getNombre());
        resenaDialogBuilder.setView(resenaPopupWindow);
        resenaDialog = resenaDialogBuilder.create();
        resenaDialog.show();

        resenaCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resenaDialog.hide();
                calification = 0;
                opinion = "";
            }
        });

        resenaSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (opinionText.getText().toString().equals("") || calification == 0){
                    emptyValues();
                }
                else{
                    opinion = opinionText.getText().toString();
                    createResenaAndPhotoDialog();
                    resenaDialog.hide();
                }
            }
        });

        //Star buttons
        resenaOneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calification = 1;
                resenaOneBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);

                resenaTwoBtn.setBackgroundResource(android.R.drawable.btn_star_big_off);
                resenaThreeBtn.setBackgroundResource(android.R.drawable.btn_star_big_off);
                resenaFourBtn.setBackgroundResource(android.R.drawable.btn_star_big_off);
                resenaFiveBtn.setBackgroundResource(android.R.drawable.btn_star_big_off);
            }
        });
        resenaTwoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calification = 2;
                resenaOneBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                resenaTwoBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);

                resenaThreeBtn.setBackgroundResource(android.R.drawable.btn_star_big_off);
                resenaFourBtn.setBackgroundResource(android.R.drawable.btn_star_big_off);
                resenaFiveBtn.setBackgroundResource(android.R.drawable.btn_star_big_off);
            }
        });
        resenaThreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calification = 3;
                resenaThreeBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                resenaTwoBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                resenaOneBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);

                resenaFourBtn.setBackgroundResource(android.R.drawable.btn_star_big_off);
                resenaFiveBtn.setBackgroundResource(android.R.drawable.btn_star_big_off);
            }
        });
        resenaFourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calification = 4;
                resenaFourBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                resenaThreeBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                resenaTwoBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                resenaOneBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);

                resenaFiveBtn.setBackgroundResource(android.R.drawable.btn_star_big_off);
            }
        });
        resenaFiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calification = 5;
                resenaFiveBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                resenaFourBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                resenaThreeBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                resenaTwoBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
                resenaOneBtn.setBackgroundResource(android.R.drawable.btn_star_big_on);
            }
        });
    }

    // ask for photo review dialog
    protected void createResenaAndPhotoDialog(){
        AlertDialog.Builder ResenaAndPhotoBuilder = new AlertDialog.Builder(requireContext());
        final View ResenaAndPhotoPopupWindow = getLayoutInflater().inflate(R.layout.resena_and_photo_popup, null);
        TextView resenaSite = (TextView) ResenaAndPhotoPopupWindow.findViewById(R.id.lblNameSite);
        Button addPhotoBtn = (Button) ResenaAndPhotoPopupWindow.findViewById(R.id.addPhotoBtn);
        Button noPhotoBtn = (Button) ResenaAndPhotoPopupWindow.findViewById(R.id.noPhotoBtn);

        resenaSite.setText(site.getNombre());
        ResenaAndPhotoBuilder.setView(ResenaAndPhotoPopupWindow);
        resenaAndPhotoDialog = ResenaAndPhotoBuilder.create();
        resenaAndPhotoDialog.show();

        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createsResenaPhotosDialog();
                resenaAndPhotoDialog.hide();
            }
        });

        noPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadReview();
                resenaAndPhotoDialog.hide();
            }
        });
    }

    // photo selector for review dialog
    protected void createsResenaPhotosDialog(){
        AlertDialog.Builder resenaPhotosDialogBuilder = new AlertDialog.Builder(requireContext());
        final View resenaPhotosPopupWindow = getLayoutInflater().inflate(R.layout.resena_photos_popup, null);

        TextView resenaSite = (TextView) resenaPhotosPopupWindow.findViewById(R.id.lblNameSite);
        ImageView addImage = (ImageView) resenaPhotosPopupWindow.findViewById(R.id.addPhotos);
        Button resenaSendBtn = (Button) resenaPhotosPopupWindow.findViewById(R.id.sendBtn);
        Button resenaNoPhotosSendBtn = (Button) resenaPhotosPopupWindow.findViewById(R.id.cancelReviewPhotoBtn);
        LinearLayout resenaCancelBtn = (LinearLayout) resenaPhotosPopupWindow.findViewById(R.id.cancelLayout);

        resenaSite.setText(site.getNombre());
        resenaPhotosDialogBuilder.setView(resenaPhotosPopupWindow);
        resenaPhotosDialog = resenaPhotosDialogBuilder.create();
        resenaPhotosDialog.show();

        resenaCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resenaPhotosDialog.hide();
                cleanData();
            }
        });

        resenaNoPhotosSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadReview();
                resenaPhotosDialog.hide();
            }
        });

        resenaSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (opinion.equals("") || calification == 0 || photos.isEmpty()){
                    emptyValues();
                }
                else{
                    uploadReviewWPhotos();
                    resenaPhotosDialog.hide();
                }
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotos.launch("image/*");
            }
        });
    }

    protected void updateResenaPhotosDialog(Uri uri){
        LinearLayout photoPanel = (LinearLayout) resenaPhotosDialog.findViewById(R.id.addPhotosLinearLayout);

        ImageView iv = (ImageView)LayoutInflater.from(getContext()).inflate(R.layout.images_review, null);
        iv.setImageURI(uri);
        assert photoPanel != null;
        photoPanel.addView(iv, 0);
        photos.add(uri);
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

    // review database methods
    protected void uploadReview(){
        VisitanteSingleton user = VisitanteSingleton.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String[] idDoc = {""};

        Map<String, Object> data = new HashMap<>();
        data.put("autor", user.getNombre());
        data.put("calificacion", calification);
        data.put("comentario", opinion);
        data.put("fechaSubida", FieldValue.serverTimestamp());
        data.put("likes", 0);
        data.put("dislikes", 0);
        Task<DocumentReference> docRef = db.collection("Resena").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                        linkReviewSite(documentReference.getId());
                        cleanData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // todo error window
                        Log.w("Error", "Error adding document", e);
                    }
                });
    }

    protected void linkReviewSite(String id){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("idResena", id);
        data.put("idSitio", site.getIdSite());
        Task<DocumentReference> docRef = db.collection("ResenasXSitios").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // todo error window
                        Log.w("Error", "Error adding document", e);
                    }
                });
    }

    // review with photos databse methods
    protected void uploadReviewWPhotos(){
        VisitanteSingleton user = VisitanteSingleton.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("autor", user.getNombre());
        data.put("calificacion", calification);
        data.put("comentario", opinion);
        data.put("fechaSubida", FieldValue.serverTimestamp());
        data.put("likes", 0);
        data.put("dislikes", 0);
        Task<DocumentReference> docRef = db.collection("Resena").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                        linkReviewSite(documentReference.getId());
                        for (Uri photo : photos){
                            uploadPhotoReviewMedia(photo, documentReference.getId());
                        }
                        cleanData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // todo error window
                        Log.w("Error", "Error adding document", e);
                    }
                });
    }

    protected void uploadPhotoReviewMedia(Uri photo, String idReview){
        @SuppressLint("Recycle") Cursor returnCursor =
                getActivity().getApplicationContext().getContentResolver().query(photo, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        String filename = formatter.format(now) + "_" + returnCursor.getString(nameIndex);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("FotografiasResenas/" + filename);
        storageReference.putFile(photo)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadPhotoReview("FotografiasResenas/" + filename, idReview);
                    }
                });
    }

    protected void uploadPhotoReview(String imgPath, String idReview){
        VisitanteSingleton user = VisitanteSingleton.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("autor", user.getNombre());
        data.put("descripcion", opinion);
        data.put("fechaSubida", FieldValue.serverTimestamp());
        data.put("foto", imgPath);
        Task<DocumentReference> docRef = db.collection("Fotografia").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                        linkReviewPhotos(idReview, documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // todo error window
                        Log.w("Error", "Error adding document", e);
                    }
                });
    }

    protected void linkReviewPhotos(String idReview, String idPhoto){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("idResena", idReview);
        data.put("idFotografia", idPhoto);
        Task<DocumentReference> docRef = db.collection("ResenasXFotos").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // todo error window
                        Log.w("Error", "Error adding document", e);
                    }
                });
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
                        // todo error window
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
                        // todo error window
                        Log.w("Error", "Error adding document", e);
                    }
                });
    }
}