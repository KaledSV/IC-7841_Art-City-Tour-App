package Sitio;

import java.io.Serializable;
import java.util.Date;

public class SitioPersonalizado implements Serializable {
    transient String idSitioPersonalizado;
    String idSitio;
    String nombre;
    String tipoSitio;
    String idFotoPredeterminada;
    String comentario;
    Date horaVisita;

    public SitioPersonalizado(String idSitioPersonalizado, String idSitio, String nombre, String tipoSitio, String idFotoPredeterminada, String comentario, Date horaVisita) {
        this.idSitioPersonalizado = idSitioPersonalizado;
        this.idSitio = idSitio;
        this.nombre = nombre;
        this.tipoSitio = tipoSitio;
        this.idFotoPredeterminada = idFotoPredeterminada;
        this.comentario = comentario;
        this.horaVisita = horaVisita;
    }

    public SitioPersonalizado(){

    }

    // setters and getters
    public String getIdSitioPersonalizado() {
        return idSitioPersonalizado;
    }

    public void setIdSitioPersonalizado(String idSitioPersonalizado) {
        this.idSitioPersonalizado = idSitioPersonalizado;
    }

    public String getIdSitio() {
        return idSitio;
    }

    public void setIdSitio(String idSitio) {
        this.idSitio = idSitio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoSitio() {
        return tipoSitio;
    }

    public void setTipoSitio(String tipoSitio) {
        this.tipoSitio = tipoSitio;
    }

    public String getIdFotoPredeterminada() {
        return idFotoPredeterminada;
    }

    public void setIdFotoPredeterminada(String idFotoPredeterminada) {
        this.idFotoPredeterminada = idFotoPredeterminada;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getHoraVisita() {
        return horaVisita;
    }

    public void setHoraVisita(Date horaVisita) {
        this.horaVisita = horaVisita;
    }

    public String getFieldValues(){
        return "idSitio: " + this.idSitio + " nombre: " + this.nombre + ", tipo: " +
                this.tipoSitio + "foto: " + this.idFotoPredeterminada + ", comentario: " +
                this.comentario + ", horario: " + this.horaVisita;
    }
}
