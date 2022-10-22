package Usuario;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.artcitytourapp.R;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Fotografia.Fotografia;
import Resenna.Resenna;
import Ruta.RutaPersonalizada;
import Sitio.Sitio;

public class VisitanteSingleton extends Usuario {
    // Singleton attribute
    private static final VisitanteSingleton instance = new VisitanteSingleton();

    //Atributos
    private int permiso = 2;
    private List<RutaPersonalizada> rutas = null;
    private List<String> sitiosIdFavoritos = null;
    private List<String> reviewIdLike = null;
    private List<String> reviewIdDislike = null;
    private List<String> photoIdLike = null;
    private List<String> photoIdDislike = null;

    // Private constructor prevents instantiation from other classes
    public static VisitanteSingleton AlterSingleton(String id, String nombre, long numero, String correo, String contrasenna) {
        instance.setId(id);
        instance.setNombre(nombre);
        instance.setNumero(numero);
        instance.setCorreo(correo);
        instance.setContrasenna(contrasenna);
        instance.bdGetFavoritos();
        return instance;
    }

    public static VisitanteSingleton getInstance() {
        return instance;
    }

    private VisitanteSingleton(){
        super("", "", 0, "", "");
    }

    @SuppressWarnings("unchecked")
    public static void LoginVisitante(String correo, String contrasenna){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios")
                .whereEqualTo("correo", correo)
                .whereEqualTo("contraseña", contrasenna)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                VisitanteSingleton.AlterSingleton(doc.getId(), (String)doc.get("nombre"), (long)doc.get("numero"), correo, contrasenna);
                                instance.setReviewIdLike((List<String>)doc.get("reviewIdLike"));
                                instance.setReviewIdDislike((List<String>) doc.get("reviewIdDislike"));
                                instance.setPhotoIdLike((List<String>)doc.get("photoIdLike"));
                                instance.setPhotoIdDislike((List<String>) doc.get("photoIdDislike"));
                                RutaPersonalizada.alterRutaPersonalizada((String) doc.get("rutaPersonal"), (String) doc.get("rutaCompartida"));
                            }
                        } else {
                            // todo login incorrecto
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    // setters and getters
    public int getPermiso() {
        return permiso;
    }

    public void setPermiso(int permiso) {
        this.permiso = permiso;
    }

    public List<RutaPersonalizada> getRutas() {
        return rutas;
    }

    public void setRutas(List<RutaPersonalizada> rutas) {
        this.rutas = rutas;
    }

    public List<String> getSitiosFavoritos() {
        return sitiosIdFavoritos;
    }

    public void setSitiosFavoritos(List<String> sitiosIdFavoritos) {
        this.sitiosIdFavoritos = sitiosIdFavoritos;
    }

    public List<String> getReviewIdLike() {
        return reviewIdLike;
    }

    public void setReviewIdLike(List<String> reviewIdLike) {
        if (reviewIdLike==null)
            this.reviewIdLike = new ArrayList<String>();
        else
            this.reviewIdLike = reviewIdLike;
    }

    public List<String> getReviewIdDislike() {
        return reviewIdDislike;
    }

    public void setReviewIdDislike(List<String> reviewIdDislike) {
        if (reviewIdDislike==null)
            this.reviewIdDislike = new ArrayList<String>();
        else
            this.reviewIdDislike = reviewIdDislike;
    }

    public List<String> getPhotoIdLike() {
        return photoIdLike;
    }

    public void setPhotoIdLike(List<String> photoIdLike) {
        this.photoIdLike = photoIdLike;
    }

    public List<String> getPhotoIdDislike() {
        return photoIdDislike;
    }

    public void setPhotoIdDislike(List<String> photoIdDislike) {
        this.photoIdDislike = photoIdDislike;
    }

    // load methods
    private void bdGetFavoritos(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        db.collection("Favoritos")
                .whereEqualTo("idUsuario", instance.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<String> sitesIDs = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sitesIDs.add((String) document.getData().get("idSitio"));
                            }
                            instance.setSitiosFavoritos(sitesIDs);
                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    // Favorite methods
    public boolean siteFavoriteStatus(Sitio espSite){
        return instance.getSitiosFavoritos().contains(espSite.getIdSite());
    }

    public boolean siteFavoriteStatus(String idSite){
        return instance.getSitiosFavoritos().contains(idSite);
    }

    public void errorUploding(View view){
        new AlertDialog.Builder(view.getContext())
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

    public void bdRemoveFavorite(String idSite, View view){
        VisitanteSingleton user = VisitanteSingleton.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Favoritos")
                .whereEqualTo("idUsuario", user.getId())
                .whereEqualTo("idSitio", idSite)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("Favoritos").document(document.getId()).delete();
                                Log.d("TAG", "DocumentSnapshot eliminated with ID: " + document.getId());
                            }
                            user.getSitiosFavoritos().remove(idSite);
                        } else {
                            errorUploding(view);
                            Log.w("TAG", "Error removing documents.", task.getException());
                        }
                    }
                });
    }

    public void bdAddFavorite(String idSite, View view){
        VisitanteSingleton user = VisitanteSingleton.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("idSitio", idSite);
        data.put("idUsuario", user.getId());
        Task<DocumentReference> docRef = db.collection("Favoritos").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        user.getSitiosFavoritos().add(idSite);
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUploding(view);
                        Log.w("Error", "Error adding document", e);
                    }
                });
    }

    //Error method for likes
    public void errorUplodingLikeChange(View view){
        new AlertDialog.Builder(view.getContext())
                .setTitle("Error")
                .setMessage("Ha ocurrido un error al intentar dar like o dislike en la base de datos")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    // Review methods
    public boolean reviewLikeStatus(String reviewId){
        return instance.getReviewIdLike().contains(reviewId);
    }

    public boolean reviewDislikeStatus(String reviewId){
        return instance.getReviewIdDislike().contains(reviewId);
    }

    public void bdUpdateLike(View view, Resenna resenna, boolean increment){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios")
                .document(instance.getId())
                .update("reviewIdLike", instance.getReviewIdLike())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUplodingLikeChange(view);
                        Log.w("TAG", "Error updating document", e);
                    }
                });

        int i = 0;
        if (increment)
            i++;
        else
            i--;
        int numLike = resenna.getLikes();
        resenna.setLikes(numLike + i);
        db.collection("Resena")
                .document(resenna.getIdResenna())
                .update("likes", FieldValue.increment(i))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUplodingLikeChange(view);
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

    public void bdUpdateDislike(View view, Resenna resenna, boolean increment){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios")
                .document(instance.getId())
                .update("reviewIdDislike", instance.getReviewIdDislike())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUplodingLikeChange(view);
                        Log.w("TAG", "Error updating document", e);
                    }
                });

        int i = 0;
        if (increment)
            i++;
        else
            i--;
        int numDislike = resenna.getDislikes();
        resenna.setDislikes(numDislike + i);
        db.collection("Resena")
                .document(resenna.getIdResenna())
                .update("dislikes", FieldValue.increment(i))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUplodingLikeChange(view);
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

    public void removeLike(String idReview, View view, Resenna resenna){
        instance.getReviewIdLike().remove(idReview);
        bdUpdateLike(view, resenna, false);
    }

    public void addLike(String idReview, View view, Resenna resenna){
        instance.getReviewIdLike().add(idReview);
        bdUpdateLike(view, resenna, true);
    }

    public void removeDislike(String idReview, View view, Resenna resenna){
        instance.getReviewIdDislike().remove(idReview);
        bdUpdateDislike(view, resenna, false);
    }

    public void addDislike(String idReview, View view, Resenna resenna){
        instance.getReviewIdDislike().add(idReview);
        bdUpdateDislike(view, resenna, true);
    }

    // Photo methods
    public boolean photoLikeStatus(String idPhoto){
        return instance.getPhotoIdLike().contains(idPhoto);
    }

    public boolean photoDislikeStatus(String idPhoto){
        return instance.getPhotoIdDislike().contains(idPhoto);
    }

    public void bdUpdateLikePhoto(View view, Fotografia foto, boolean increment){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios")
                .document(instance.getId())
                .update("photoIdLike", instance.getPhotoIdLike())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUplodingLikeChange(view);
                        Log.w("TAG", "Error updating document", e);
                    }
                });

        int i = 0;
        if (increment)
            i++;
        else
            i--;
        int numLike = foto.getLikes();
        foto.setLikes(numLike + i);
        db.collection("Fotografia")
                .document(foto.getIdFoto())
                .update("likes", FieldValue.increment(i))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUplodingLikeChange(view);
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

    public void bdUpdateDislikePhoto(View view, Fotografia foto, boolean increment){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios")
                .document(instance.getId())
                .update("photoIdDislike", instance.getPhotoIdDislike())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUplodingLikeChange(view);
                        Log.w("TAG", "Error updating document", e);
                    }
                });

        int i = 0;
        if (increment)
            i++;
        else
            i--;
        int numDislike = foto.getDislikes();
        foto.setDislikes(numDislike + i);
        db.collection("Fotografia")
                .document(foto.getIdFoto())
                .update("dislikes", FieldValue.increment(i))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUplodingLikeChange(view);
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

    public void removeLikePhoto(String idPhoto, View view, Fotografia foto){
        instance.getPhotoIdLike().remove(idPhoto);
        bdUpdateLikePhoto(view, foto, false);
    }

    public void addLikePhoto(String idPhoto, View view, Fotografia foto){
        instance.getPhotoIdLike().add(idPhoto);
        bdUpdateLikePhoto(view, foto, true);
    }

    public void removeDislikePhoto(String idPhoto, View view, Fotografia foto){
        instance.getPhotoIdDislike().remove(idPhoto);
        bdUpdateDislikePhoto(view, foto, false);
    }

    public void addDislikePhoto(String idPhoto, View view, Fotografia foto){
        instance.getPhotoIdDislike().add(idPhoto);
        bdUpdateDislikePhoto(view, foto, true);
    }
}
