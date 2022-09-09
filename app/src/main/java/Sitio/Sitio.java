package Sitio;

import java.util.List;

import Fotografia.Fotografia;
import Resenna.Resenna;

public class Sitio {

    //Atributos
    private String nombre;
    private Coordenada direccion;
    private String nombreRuta;
    private int capacidad;
    private int tiempoEspera;
    List<Fotografia> fotos = null;
    List<Resenna> resenna = null;

    //Constructor

    public Sitio(String nombre, Coordenada direccion, String nombreRuta, int capacidad, int tiempoEspera) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.nombreRuta = nombreRuta;
        this.capacidad = capacidad;
        this.tiempoEspera = tiempoEspera;
    }


    //Métodos


}
