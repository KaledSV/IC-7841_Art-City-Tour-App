package Sitio;

import java.sql.Time;//NOt sure if this is good

public class SitioLista {

    //Atributos
    private Sitio sitio;
    private String comentario;
    private Time horaVisita;

    //Constructor

    public SitioLista(Sitio sitio, String comentario, Time horaVisita) {
        this.sitio = sitio;
        this.comentario = comentario;
        this.horaVisita = horaVisita;
    }

    //Métodos


    public Sitio getSitio() {
        return sitio;
    }

    public void setSitio(Sitio sitio) {
        this.sitio = sitio;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Time getHoraVisita() {
        return horaVisita;
    }

    public void setHoraVisita(Time horaVisita) {
        this.horaVisita = horaVisita;
    }
}
