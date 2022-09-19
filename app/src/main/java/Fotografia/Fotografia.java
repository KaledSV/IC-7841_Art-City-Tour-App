package Fotografia;

import java.io.Serializable;
import java.util.Date;

public class Fotografia implements Serializable {
    //Atributos
    private transient String idFoto;
    private String foto; //Buscar como hacer el manejo de datos en android
    private String autor;
    private String descripcion;
    private Date fechaSubida;
    private int likes;
    private int dislikes;

    //Constructores

    public Fotografia(String idFoto, String foto, String autor, String descripcion, Date fechaSubida, int likes, int dislikes) {
        this.idFoto = idFoto;
        this.foto = foto;
        this.autor = autor;
        this.descripcion = descripcion;
        this.fechaSubida = fechaSubida;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public Fotografia(){

    }

    //Métodos
    public String getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(String idFoto) {
        this.idFoto = idFoto;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Date fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public String getFieldValues(){
        return "idFoto: " + this.idFoto + " foto: " + this.foto + ", autor: " + this.autor + "descripcion: "
                + this.descripcion + ", fechaSubida: " + this.fechaSubida;
    }
}
