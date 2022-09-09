package Usuario;

import java.util.List;

import Ruta.RutaPersonalizada;

public class Visitante extends Usuario {
    //Atributos
    private int permiso = 2;
    private List<RutaPersonalizada> rutas = null;
    private List<Favorito> sitiosFavoritos = null;

    //Constructor
    public Visitante(String nombre, int numero, String correo, String contrasenna) {
        super(nombre, numero, correo, contrasenna);
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
}
