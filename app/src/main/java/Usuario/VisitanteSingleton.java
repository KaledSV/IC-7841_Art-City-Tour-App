package Usuario;

import java.util.List;

import Ruta.RutaPersonalizada;

public class VisitanteSingleton extends Usuario {
    // Singleton attribute
    private static final VisitanteSingleton instance = new VisitanteSingleton();

    //Atributos
    private int permiso = 2;
    private List<RutaPersonalizada> rutas = null;
    private List<Favorito> sitiosFavoritos = null;

    // Private constructor prevents instantiation from other classes
    public static VisitanteSingleton AlterSingleton(String id, String nombre, int numero, String correo, String contrasenna) {
        instance.setId(id);
        instance.setNombre(nombre);
        instance.setNumero(numero);
        instance.setCorreo(correo);
        instance.setContrasenna(contrasenna);
        return instance;
    }

    public static VisitanteSingleton getInstance() {
        return instance;
    }

    private VisitanteSingleton(){
        super("", "", 0, "", "");
    }

    //Métodos
    public void crearRutaPersonalizada(){
        //TODO
    }

    public void modificarRuta(){
        //TODO
    }

    public void crearResennas(){
        //TODO
    }

    public void subirFotografias(){
        //TODO
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

    public List<Favorito> getSitiosFavoritos() {
        return sitiosFavoritos;
    }

    public void setSitiosFavoritos(List<Favorito> sitiosFavoritos) {
        this.sitiosFavoritos = sitiosFavoritos;
    }
}
