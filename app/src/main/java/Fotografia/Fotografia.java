package Fotografia;

import java.util.Date;

public class Fotografia {
    //Atributos
    private String foto; //Buscar como hacer el manejo de datos en android
    private String autor;
    private String descripcion;
    private Date diaSubida;

    //Constructores

    public Fotografia(String foto, String autor, String descripcion, Date diaSubida) {
        this.foto = foto;
        this.autor = autor;
        this.descripcion = descripcion;
        this.diaSubida = diaSubida;
    }


    //Métodos

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

    public Date getDiaSubida() {
        return diaSubida;
    }

    public void setDiaSubida(Date diaSubida) {
        this.diaSubida = diaSubida;
    }
}
