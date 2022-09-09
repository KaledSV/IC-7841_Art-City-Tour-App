package Ruta;

import java.util.List;

import Sitio.SitioLista;

public class RutaPersonalizada {

    //Atributos
    private int cantSitios;
    private List<SitioLista> sitio;

    //Constructor

    public RutaPersonalizada(int cantSitios, List<SitioLista> sitio) {
        this.cantSitios = cantSitios;
        this.sitio = sitio;
    }


    //Metodos


    public int getCantSitios() {
        return cantSitios;
    }

    public void setCantSitios(int cantSitios) {
        this.cantSitios = cantSitios;
    }

    public List<SitioLista> getSitio() {
        return sitio;
    }

    public void setSitio(List<SitioLista> sitio) {
        this.sitio = sitio;
    }

    public void addSitio(SitioLista psitio){
        this.sitio.add(psitio);
    }

}
