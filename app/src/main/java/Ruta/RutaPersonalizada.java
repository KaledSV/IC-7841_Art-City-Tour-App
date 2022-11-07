package Ruta;

import android.content.DialogInterface;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.example.artcitytourapp.R;
import com.example.artcitytourapp.fragments.SubPlanningFragment;
import com.example.artcitytourapp.fragments.SubPlanningMyRouteListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import Sitio.Sitio;
import Sitio.SitioLista;
import Sitio.SitioPersonalizado;
import Usuario.VisitanteSingleton;

public class RutaPersonalizada {
    // Singleton attribute
    private static final RutaPersonalizada instance = new RutaPersonalizada();

    //Atributos
    private String name;
    private int cantSitios;
    private String idMyRoute = null;
    private String idSharedRoute = null;
    private Timestamp lastModified = null;
    private List<SitioPersonalizado> myRoute = new ArrayList<SitioPersonalizado>();
    private List<String> myRoutePersonalizedSitesIds = new ArrayList<String>();
    private List<String> myRouteSitesIds = new ArrayList<String>();
    private List<SitioPersonalizado> sharedRoute = new ArrayList<SitioPersonalizado>();

    //Constructor

    public static void alterRutaPersonalizada(String idMyRoute, String idSharedRoute) {
        instance.setIdMyRoute(idMyRoute);
        instance.setIdSharedRoute(idSharedRoute);
        instance.bdGetMyRoute();
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

    public Timestamp getlastModified() { return lastModified;}

    public void setLastModified(Timestamp timestamp) {this.lastModified = timestamp;}

    public void setIdSharedRoute(String idSharedRoute) {
        this.idSharedRoute = idSharedRoute;
        this.bdGetSharedRoute();
    }

    public List<SitioPersonalizado> getMyRoute() {
        return myRoute;
    }

    public void setMyRoute(List<SitioPersonalizado> myRoute) {
        this.myRoute = myRoute;
    }

    public List<String> getMyRoutePersonalizedSitesIds() {
        return myRoutePersonalizedSitesIds;
    }

    public void setMyRoutePersonalizedSitesIds(List<String> myRoutePersonalizedSitesIds) {
        this.myRoutePersonalizedSitesIds = myRoutePersonalizedSitesIds;
    }

    public List<String> getMyRouteSitesIds() {
        return myRouteSitesIds;
    }

    public void setMyRouteSitesIds(List<String> myRouteSitesIds) {
        this.myRouteSitesIds = myRouteSitesIds;
    }

    public List<SitioPersonalizado> getSharedRoute() {
        return sharedRoute;
    }

    public void setSharedRoute(List<SitioPersonalizado> sharedRoute) {
        this.sharedRoute = sharedRoute;
    }

    // load methods
    @SuppressWarnings("unchecked")
    private void bdGetMyRoute(){
        if(!Objects.equals(instance.getIdMyRoute(), "")){
            instance.getMyRoutePersonalizedSitesIds().clear();
            instance.getMyRouteSitesIds().clear();
            instance.getMyRoute().clear();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("RutaPersonalizada").document(instance.getIdMyRoute());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<String> ids = (ArrayList<String>)document.get("sitiosPersonalizado");
                            instance.setName((String)document.get("nombre"));
                            instance.setLastModified((Timestamp) document.get("ultimaModificacion"));
                            if (ids != null){
                                instance.setCantSitios(ids.size());
                                for(String idPersonalizedSite : ids){
                                    instance.getMyRoutePersonalizedSitesIds().add(idPersonalizedSite);
                                    instance.getMyRoute().add(null);
                                    bdGetSiteMyRoute(idPersonalizedSite);

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

    private void bdGetSiteMyRoute(String idPersonalizedSite){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("SitioPersonalizado").document(idPersonalizedSite);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        SitioPersonalizado site = document.toObject(SitioPersonalizado.class);
                        if (site != null){
                            site.setIdSitioPersonalizado(document.getId());

                            instance.getMyRouteSitesIds().add(site.getIdSitio());
                            int i = instance.getMyRoutePersonalizedSitesIds().indexOf(idPersonalizedSite);
                            instance.getMyRoute().set(i,site);

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

    @SuppressWarnings("unchecked")
    private void bdGetSharedRoute(){
        if(!Objects.equals(instance.getIdSharedRoute(), "")){
            instance.getSharedRoute().clear();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("RutaPersonalizada").document(instance.getIdSharedRoute());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<String> ids = (ArrayList<String>) document.get("sitiosPersonalizado");
                            if (ids != null){
                                for(String idPersonalizedSite : ids){
                                    bdGetSiteSharedRoute(idPersonalizedSite);
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

    private void bdGetSiteSharedRoute(String idPersonalizedSite){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("SitioPersonalizado").document(idPersonalizedSite);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        SitioPersonalizado site = document.toObject(SitioPersonalizado.class);
                        if (site != null){
                            site.setIdSitioPersonalizado(document.getId());
                            instance.getSharedRoute().add(site);
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

    public void errorRemovingPersonalizedSite(View view){
        new AlertDialog.Builder(view.getContext())
                .setTitle("Error")
                .setMessage("Ha ocurrido un error al intentar eliminar el sitio a su plan personalizado")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    // Route methods
    public void addSiteMyRoute(Sitio site, View view){
        if (!instance.getMyRouteSitesIds().contains(site.getIdSite())) {
            Date date = new Date();

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            SitioPersonalizado pSite = new SitioPersonalizado("", site.getIdSite(), site.getNombre(),
                    site.getTipoSitio(), site.getIdFotoPredeterminada(), "", new Timestamp(date));
            Map<String, Object> data = new HashMap<>();
            data.put("comentario", pSite.getComentario());
            data.put("horaVisita", pSite.getHoraVisita());
            data.put("idFotoPredeterminada", pSite.getIdFotoPredeterminada());
            data.put("idSitio", pSite.getIdSitio());
            data.put("nombre", pSite.getNombre());
            data.put("tipoSitio", pSite.getTipoSitio());

            Task<DocumentReference> docRef = db.collection("SitioPersonalizado").add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                            pSite.setIdSitioPersonalizado(documentReference.getId());
                            addSiteMyRouteList(site.getIdSite(), pSite, view);
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

    private void addSiteMyRouteList(String idSite, SitioPersonalizado site, View view){
        instance.getMyRoutePersonalizedSitesIds().add(site.getIdSitioPersonalizado());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("RutaPersonalizada")
                .document(instance.getIdMyRoute())
                .update("sitiosPersonalizado", instance.getMyRoutePersonalizedSitesIds())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        instance.getMyRoute().add(site);
                        instance.getMyRouteSitesIds().add(idSite);
                        instance.setCantSitios(instance.getCantSitios() + 1);
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUplodingPersonalizedSite(view);
                        instance.getMyRoutePersonalizedSitesIds().remove(site.getIdSitio());
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

    private void removeSiteMyRoute(SitioPersonalizado site, View view){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SitioPersonalizado")
                .document(site.getIdSitioPersonalizado())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        instance.getMyRoute().remove(site);
                        instance.getMyRouteSitesIds().remove(site.getIdSitio());
                        instance.setCantSitios(instance.getCantSitios() - 1);
                        refreshPlanning();
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorRemovingPersonalizedSite(view);
                        instance.getMyRoutePersonalizedSitesIds().add(site.getIdSitio());
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

    public void removeSiteMyRouteList(SitioPersonalizado site, View view){
        instance.getMyRoutePersonalizedSitesIds().remove(site.getIdSitioPersonalizado());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("RutaPersonalizada")
                .document(instance.getIdMyRoute())
                .update("sitiosPersonalizado", instance.getMyRoutePersonalizedSitesIds())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        removeSiteMyRoute(site, view);
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorRemovingPersonalizedSite(view);
                        instance.getMyRoutePersonalizedSitesIds().add(site.getIdSitio());
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

    public static void registerRoute(String idUser){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Date date = new Date();

        Map<String, Object> data = new HashMap<>();
        data.put("creador", idUser);
        data.put("nombre", "Por el amor a Chepe");
        data.put("sitiosPersonalizado", new ArrayList<>());
        data.put("ultimaModificacion", new Timestamp(date));

        Task<DocumentReference> docRef = db.collection("RutaPersonalizada").add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                        instance.setIdMyRoute(documentReference.getId());
                        instance.bdGetMyRoute();
                        VisitanteSingleton.getInstance().updateMyRouteId(documentReference.getId(), idUser);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        registerRoute(idUser);
                        Log.w("Error", "Error adding document", e);
                    }
                });
    }

    public void resetMyRoute(View view){
        ArrayList<SitioPersonalizado> sitiosBorrar = new ArrayList<SitioPersonalizado>(instance.getMyRoute());
        for (SitioPersonalizado site : sitiosBorrar){
            removeSiteMyRouteList(site, view);
        }
    }

    public void refreshPlanning(){
        if (SubPlanningFragment.getContainer() != null){
            FragmentManager manager = SubPlanningFragment.getActivityContainer().getSupportFragmentManager();
            SubPlanningMyRouteListFragment subPlanningMyRoute = new SubPlanningMyRouteListFragment();

            manager.beginTransaction()
                    .replace(SubPlanningFragment.getContainer().getId(), subPlanningMyRoute, "subPlanningMyRouteListFragment")
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public String toString() {
        return "RutaPersonalizada{" +
                "name='" + name + '\'' +
                ", cantSitios=" + cantSitios +
                ", idMyRoute='" + idMyRoute + '\'' +
                ", idSharedRoute='" + idSharedRoute + '\'' +
                ", lastModified=" + lastModified +
                ", myRoute=" + myRoute +
                ", myRoutePersonalizedSitesIds=" + myRoutePersonalizedSitesIds +
                ", myRouteSitesIds=" + myRouteSitesIds +
                ", sharedRoute=" + sharedRoute +
                '}';
    }
}
