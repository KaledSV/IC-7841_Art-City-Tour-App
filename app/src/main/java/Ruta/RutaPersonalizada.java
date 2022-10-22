package Ruta;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Sitio.SitioLista;
import Usuario.VisitanteSingleton;

public class RutaPersonalizada {
    // Singleton attribute
    private static final RutaPersonalizada instance = new RutaPersonalizada();

    //Atributos
    private String name;
    private int cantSitios;
    private String idMyRoute = null;
    private String idSharedRoute = null;
    private List<String> myRouteIds = null;
    private List<String> myRouteSitesIds = new ArrayList<String>();
    private List<String> sharedRouteIds = null;

    //Constructor

    public static void alterRutaPersonalizada(String idMyRoute, String idSharedRoute) {
        instance.setIdMyRoute(idMyRoute);
        instance.setIdSharedRoute(idSharedRoute);
        instance.bdGetMyRoute();
        instance.bdGetSharedRoute();
    }

    public static RutaPersonalizada getInstance() {
        return instance;
    }

    private RutaPersonalizada(){

    }

    //Metodos

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCantSitios() {
        return cantSitios;
    }

    public void setCantSitios(int cantSitios) {
        this.cantSitios = cantSitios;
    }

    public String getIdMyRoute() {
        return idMyRoute;
    }

    public void setIdMyRoute(String idMyRoute) {
        this.idMyRoute = idMyRoute;
    }

    public String getIdSharedRoute() {
        return idSharedRoute;
    }

    public void setIdSharedRoute(String idSharedRoute) {
        this.idSharedRoute = idSharedRoute;
    }

    public List<String> getMyRouteIds() {
        return myRouteIds;
    }

    public void setMyRouteIds(List<String> myRouteIds) {
        this.myRouteIds = myRouteIds;
    }

    public List<String> getMyRouteSitesIds() {
        return myRouteSitesIds;
    }

    public void setMyRouteSitesIds(List<String> myRouteSitesIds) {
        this.myRouteSitesIds = myRouteSitesIds;
    }

    public List<String> getSharedRouteIds() {
        return sharedRouteIds;
    }

    public void setSharedRouteIds(List<String> sharedRouteIds) {
        this.sharedRouteIds = sharedRouteIds;
    }

    // load methods
    @SuppressWarnings("unchecked")
    private void bdGetMyRoute(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("RutaPersonalizada").document(instance.getIdMyRoute());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        instance.setMyRouteIds((ArrayList<String>)document.get("sitiosPersonalizado"));
                        instance.setName((String)document.get("nombre"));
                        instance.setCantSitios(instance.getMyRouteIds().size());
                        for(String idPersonalizedSite : instance.getMyRouteIds()){
                            bdGetSiteMyRoute(idPersonalizedSite);
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

    private void bdGetSiteMyRoute(String idPersonalizedSite){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("SitioPersonalizado").document(idPersonalizedSite);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        instance.getMyRouteSitesIds().add((String) document.get("idSitio"));
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void bdGetSharedRoute(){
        if(!Objects.equals(instance.getIdSharedRoute(), "")){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("RutaPersonalizada").document(instance.getIdSharedRoute());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            instance.setMyRouteIds((ArrayList<String>)document.get("sitiosPersonalizado"));
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

    //Error method for routes
    public void errorUplodingPersonalizedSite(View view){
        new AlertDialog.Builder(view.getContext())
                .setTitle("Error")
                .setMessage("Ha ocurrido un error al intentar enlazar el sitio a su plan personalizado")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    // Route methods
    public void addSiteMyRoute(String idSite, View view){
        if (!instance.getMyRouteSitesIds().contains(idSite)) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Map<String, Object> data = new HashMap<>();
            data.put("idSitio", idSite);
            data.put("horaVisita", "00:00pm");
            data.put("comentario", "");
            Task<DocumentReference> docRef = db.collection("SitioPersonalizado").add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                            addSiteMyRouteList(idSite, documentReference.getId(), view);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Error", "Error adding document", e);
                            errorUplodingPersonalizedSite(view);
                        }
                    });
        }
    }

    private void addSiteMyRouteList(String idSite, String idPersonalizedSite, View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("RutaPersonalizada")
                .document(instance.getIdMyRoute())
                .update("sitiosPersonalizado", instance.getMyRouteIds())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        instance.getMyRouteIds().add(idPersonalizedSite);
                        instance.getMyRouteSitesIds().add(idSite);
                        instance.setCantSitios(instance.getCantSitios() + 1);
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUplodingPersonalizedSite(view);
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }
}
