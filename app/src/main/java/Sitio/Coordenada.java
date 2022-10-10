package Sitio;

public class Coordenada {
    //Atributos
    public String nombre;
    public double latitud;
    public double longitud;


    //Constructor


    public Coordenada(String nombre, double latitud, double longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Coordenada(){

    }

    //Metodos

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
