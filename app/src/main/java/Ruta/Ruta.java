package Ruta;

import java.util.List;

import Sitio.Sitio;

public class Ruta {
    //Atributos

    private String nombre;
    private int cantSitios;
    List<Sitio> sitios;

    //Constructores

    public Ruta(String nombre, int cantSitios, List<Sitio> sitios) {
        this.nombre = nombre;
        this.cantSitios = cantSitios;
        this.sitios = sitios;
    }


    //Metodos

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantSitios() {
        return cantSitios;
    }

    public void setCantSitios(int cantSitios) {
        this.cantSitios = cantSitios;
    }

    public List<Sitio> getSitios() {
        return sitios;
    }

    public void setSitios(List<Sitio> sitios) {
        this.sitios = sitios;
    }
}
