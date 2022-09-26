package Usuario;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.artcitytourapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Ruta.RutaPersonalizada;

public class VisitanteSingleton extends Usuario {
    // Singleton attribute
    private static final VisitanteSingleton instance = new VisitanteSingleton();

    //Atributos
    private int permiso = 2;
    private List<RutaPersonalizada> rutas = null;
    private List<String> sitiosIdFavoritos = null;

    // Private constructor prevents instantiation from other classes
    public static VisitanteSingleton AlterSingleton(String id, String nombre, int numero, String correo, String contrasenna) {
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

    public void bdGetFavoritos(){
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
}
