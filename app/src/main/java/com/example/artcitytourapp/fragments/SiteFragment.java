package com.example.artcitytourapp.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.Navigation;

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
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import Fotografia.Fotografia;
import Resenna.Resenna;
import Sitio.Sitio;
import Usuario.VisitanteSingleton;

public class SiteFragment extends Fragment {
    View view;
    ViewFlipper flipper;
    LinearLayout layoutResenas;
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

        // load all data from database
        Bundle b = this.getArguments();
        if (b != null) {
            site = (Sitio) b.get("Sitio");
            idRoute = (String) b.get("idRuta");
            loadData();
        }

        // Data of fragment
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

        ImageView shareSite = (ImageView) view.findViewById(R.id.shareImage_SiteDes);
        shareSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        layoutResenas = (LinearLayout) view.findViewById(R.id.resenaJ);

        flipper = (ViewFlipper) view.findViewById(R.id.previewImages);
        flipper.setFlipInterval(3500);
        flipper.setAutoStart(true);
        flipper.setClickable(true);
        flipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable("Sitio", site);
                b.putSerializable("idRuta", idRoute);
                Navigation.findNavController(view).navigate(R.id.galleryFragment, b);
            }
        });
        ImageView buttonSchedule = view.findViewById(R.id.moreDats_SiteInfo);
        buttonSchedule.setClickable(true);
        buttonSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putSerializable("Sitio", site);
                Navigation.findNavController(view).navigate(R.id.scheduleFragment, b);
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
        ImageView backBtn = (ImageView) view.findViewById(R.id.backImageSiteInfo);
        backBtn.setClickable(true);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

        return view;
    }

    private void share() {
        Intent sendIntent = new Intent();
        String base = "Mirá que chiva el sitio: ";
        Uri.Builder builder = new Uri.Builder(); //Query builder URI for easier parsing
        builder.scheme("https")
                .authority("act.navigation.app")
                .appendPath("Sitio")
                .appendQueryParameter("id", site.getIdSite())
                .appendQueryParameter("sort", "relevance")
                .fragment("Sitios");
        String Uri = builder.build().toString();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,base + site.getNombre() + "\n Mas informacion en: "+Uri);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
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

        lblNameRoute.setText(site.getNombreRuta());
        lblNameSite.setText(site.getNombre());
        lblDescription.setText(site.getDescripcion());
        lblexpectedTime.setText("Tiempo de espera: " + Integer.toString(site.getTiempoEspera()));
        lblcapacity.setText("Capacidad Maxima: " + Integer.toString(site.getCapacidad()));

        bdGetReviewsIdBySite(site.getIdSite());
        bdGetPhotosIdSite(site.getIdSite());
        bdGetHorarioDiaIdSite(site.getIdSite());
        setFavButon();
    }

    protected void cleanData(){
        opinion = "";
        description = "";
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

    // setFavStatus and button functionality
    protected void setFavButon(){
        VisitanteSingleton user = VisitanteSingleton.getInstance();
        final ImageView favImage = (ImageView) view.findViewById(R.id.favImage_SiteDes);
        setFavoriteImage(user.siteFavoriteStatus(site), favImage);

        favImage.setClickable(true);
        favImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.siteFavoriteStatus(site)){
                    user.bdRemoveFavorite(site.getIdSite(), view);
                    setFavoriteImage(false, favImage);
                }
                else{
                    user.bdAddFavorite(site.getIdSite(), view);
                    setFavoriteImage(true, favImage);
                }
            }
        });
    }

    protected void setFavoriteImage(Boolean status, ImageView iv){
        if (status){
            iv.setImageResource(R.drawable.ic_baseline_favorite_on_24);
        }
        else{
            iv.setImageResource(R.drawable.ic_baseline_favorite_off_grey_24);
        }
    }

    // schedule of the day
    protected void bdGetHorarioDiaIdSite(String siteId){
        LinearLayout layoutH = view.findViewById(R.id.layoutH);
        TextView lyh = layoutH.findViewById(R.id.day_SiteInfo);
        TextView lyh2 = layoutH.findViewById(R.id.timeRange_SiteInfo);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Calendar c = Calendar.getInstance();
        int nD = c.get(Calendar.DAY_OF_WEEK);
        String varDia = "";
        switch (nD){
            case Calendar.SUNDAY: varDia = "Domingo";
                break;
            case Calendar.MONDAY: varDia = "Lunes";
                break;
            case Calendar.TUESDAY: varDia = "Martes";
                break;
            case Calendar.WEDNESDAY: varDia = "Miercoles";
                break;
            case Calendar.THURSDAY: varDia = "Jueves";
                break;
            case Calendar.FRIDAY: varDia = "Viernes";
                break;
            case Calendar.SATURDAY: varDia = "Sabado";
                break;
        }

        String finalVarDia = varDia;
        db.collection("Horario")
                .whereEqualTo("idSitio", siteId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean notFound = true;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String dia = (String) document.getData().get("dia");
                                if(finalVarDia.equals(dia)){
                                    String abierto = (String) document.getData().get("abierto");
                                    String cerrado = (String) document.getData().get("cerrado");
                                    lyh.setText(finalVarDia);
                                    lyh2.setText(abierto+"-"+cerrado);
                                    notFound = false;
                                    break;
                                }
                            }
                            if (notFound){
                                lyh.setText(finalVarDia);
                                lyh2.setText("Cerrado");
                            }
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    // Preview of gallery
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
                            if (fotosIDs.size()<=3){
                                bdGetPhotosBySite(fotosIDs);
                            }
                            else{
                                ArrayList<String> fotosIDsShorted = new ArrayList<String>();
                                for (int i=0; i<3; i++){
                                    fotosIDsShorted.add(fotosIDs.get(i));
                                }
                                bdGetPhotosBySite(fotosIDsShorted);
                            }
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
                            setPhoto(photo);
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

    protected void setPhoto(Fotografia photo) {
        if (photo.getFoto() == null) {
            photo.setFoto("Imagenes Interfaz/notFoundImage.png");
        }
        ImageView photoImageView = new ImageView(getContext());
        photoImageView.setPadding(10,10,10,10);

        bdGetPhoto(photoImageView, photo.getFoto());
        flipper.addView(photoImageView);
    }

    protected void bdGetPhoto(ImageView iv, String imgPath){
        StorageReference pathReference  = FirebaseStorage.getInstance().getReference(imgPath);
        try {
            File localFile = File.createTempFile("tempFile", imgPath.substring(imgPath.lastIndexOf(".")));
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 400, 400, false));
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    // reviews method
    protected void bdGetReviewsIdBySite(String SiteId){
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
                            bdGetReviewsBySite(resenaIDs);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    protected void bdGetReviewsBySite(ArrayList<String> resenaIDs){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<Resenna> resennas = new ArrayList<Resenna>();
        for(String resenaId : resenaIDs){
            DocumentReference docRef = db.collection("Resena").document(resenaId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Resenna resenna = document.toObject(Resenna.class);
                            assert resenna != null;
                            resenna.setIdResenna(resenaId);
                            resennas.add(resenna);
                            if (resennas.size() == resenaIDs.size()) {
                                sortResennas(resennas);
                                for (Resenna review : resennas){
                                    addReview(review);
                                }
                            }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void sortResennas(ArrayList<Resenna> resennas){
        resennas.sort(Comparator.comparing(Resenna::getFechaSubida));
    }

    protected void addReview(Resenna resenna){
        Date timeD = resenna.getFechaSubida();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(timeD);

        View ressenaView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_resena, null);

        TextView resName = ressenaView.findViewById(R.id.resenaNombre);
        resName.setText(resenna.getAutor());
        TextView resDate = ressenaView.findViewById(R.id.resenaFecha);
        resDate.setText(date);
        TextView resLikes = ressenaView.findViewById(R.id.countLikes);
        resLikes.setText(String.valueOf(resenna.getLikes()));
        TextView resDislikes = ressenaView.findViewById(R.id.countDislikes);
        resDislikes.setText(String.valueOf(resenna.getDislikes()));
        ExpandableTextView resComment = (ExpandableTextView) ressenaView.findViewById(R.id.expand_text_view);
        resComment.setText(resenna.getComentario());
        Button likeBtn = ressenaView.findViewById(R.id.likeBtn);
        Button dislikeBtn = ressenaView.findViewById(R.id.dislikeBtn);

        if (resenna.isTieneFotos()){
            LinearLayout layoutImagenes = ressenaView.findViewById(R.id.layoutImages);
            bdGetPhotosByReview(resenna.getIdResenna(), layoutImagenes);
        }
        else{
            ressenaView.findViewById(R.id.containerImgs).setVisibility(View.GONE);
        }
        setLikeAndDislikeBtn(resenna, likeBtn, dislikeBtn, resLikes, resDislikes);
        layoutResenas.addView(ressenaView);
    }

    protected void setLikeAndDislikeBtn(Resenna resenna, Button likeBtn, Button dislikeBtn, TextView resLikes,TextView resDislikes){
        VisitanteSingleton user = VisitanteSingleton.getInstance();
        setLikeImage(user.reviewLikeStatus(resenna.getIdResenna()), likeBtn);
        setDislikeImage(user.reviewDislikeStatus(resenna.getIdResenna()), dislikeBtn);

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.reviewLikeStatus(resenna.getIdResenna())){
                    user.removeLike(resenna.getIdResenna(), view, resenna);

                    // Deactivates like button and counter
                    setLikeImage(false, likeBtn);
                }
                else{
                    if (user.reviewDislikeStatus(resenna.getIdResenna())){
                        user.removeDislike(resenna.getIdResenna(), view, resenna);

                        // Update dislike button and counter if already used
                        setDislikeImage(false, dislikeBtn);
                        resDislikes.setText(String.valueOf(resenna.getDislikes()));
                    }
                    user.addLike(resenna.getIdResenna(), view, resenna);

                    // Activates like button and counter
                    setLikeImage(true, likeBtn);
                }
                resLikes.setText(String.valueOf(resenna.getLikes()));
            }
        });

        dislikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.reviewDislikeStatus(resenna.getIdResenna())){
                    user.removeDislike(resenna.getIdResenna(), view, resenna);

                    // Deactivates dislike button and counter
                    setDislikeImage(false, dislikeBtn);
                }
                else{
                    if (user.reviewLikeStatus(resenna.getIdResenna())){
                        user.removeLike(resenna.getIdResenna(), view, resenna);

                        // Update like button and counter if already used
                        setLikeImage(false, likeBtn);
                        resLikes.setText(String.valueOf(resenna.getLikes()));
                    }
                    user.addDislike(resenna.getIdResenna(), view, resenna);

                    // Activates dislike button and counter
                    setDislikeImage(true, dislikeBtn);
                }
                resDislikes.setText(String.valueOf(resenna.getDislikes()));
            }
        });
    }

    protected void setLikeImage(Boolean likeStatus, Button like){
        if (likeStatus){
            like.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24_positive);
        }
        else{
            like.setBackgroundResource(R.drawable.ic_baseline_thumb_up_24);
        }
    }

    protected void setDislikeImage(Boolean dislikeStatus, Button dislike){
        if (dislikeStatus){
            dislike.setBackgroundResource(R.drawable.ic_baseline_thumb_down_24_positive);
        }
        else{
            dislike.setBackgroundResource(R.drawable.ic_baseline_thumb_down_24);
        }
    }

    protected void bdGetPhotosByReview(String idRessena, LinearLayout layout){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        db.collection("ResenasXFotos")
                .whereEqualTo("idResena", idRessena)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> fotosIDs = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                fotosIDs.add((String) document.getData().get("idFotografia"));
                            }
                            bdGetPhotosByReview(fotosIDs, layout);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    protected void bdGetPhotosByReview(ArrayList<String> photosIDs, LinearLayout layout){
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
                            setPhotoReview(photo, layout);
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

    protected void setPhotoReview(Fotografia photo, LinearLayout layout) {
        if (photo.getFoto() == null) {
            photo.setFoto("Imagenes Interfaz/notFoundImage.png");
        }
        ImageView photoImageView = new ImageView(getContext());
        photoImageView.setPadding(10,10,10,10);

        bdGetPhotoReview(photoImageView, photo.getFoto());
        layout.addView(photoImageView);
    }

    protected void bdGetPhotoReview(ImageView iv, String imgPath){
        StorageReference pathReference  = FirebaseStorage.getInstance().getReference(imgPath);
        try {
            File localFile = File.createTempFile("tempFile", imgPath.substring(imgPath.lastIndexOf(".")));
            pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 230, 230, false));
                }
            });
        }
        catch (IOException e){
            e.printStackTrace();
        }
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

        Map<String, Object> data = new HashMap<>();
        data.put("autor", user.getNombre());
        data.put("calificacion", calification);
        data.put("comentario", opinion);
        data.put("fechaSubida", FieldValue.serverTimestamp());
        data.put("likes", 0);
        data.put("dislikes", 0);
        data.put("tieneFotos", false);
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
                        errorUploding();
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
                        errorUploding();
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
        data.put("tieneFotos", true);
        Task<DocumentReference> docRef = db.collection("Resena").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                        linkReviewSite(documentReference.getId());
                        for (Uri photo : photos){
                            uploadPhotoReviewMedia(photo, documentReference.getId());
                        }
                        //cleanData();
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
        data.put("likes", 0);
        data.put("dislikes", 0);

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
                        errorUploding();
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
                        errorUploding();
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

    // Schedule database methods

    protected String getTodaysSchedule(int SiteId){
        return "a";
    }

    protected String getTodaysHours(int SiteId){
        return "a";
    }
}