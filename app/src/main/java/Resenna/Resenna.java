package Resenna;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import Fotografia.Fotografia;

public class Resenna {
    //Atributos
    private List<Fotografia> fotos;
    private String autor;
    private String Comentario;
    private Date diaSubida;
    private int likes;
    private int dislikes;

    //Constructor

    public Resenna(List<Fotografia> fotos, String autor, String comentario, Date diaSubida, int likes, int dislikes) {
        this.fotos = fotos;
        this.autor = autor;
        Comentario = comentario;
        this.diaSubida = diaSubida;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    //Metodos


    public List<Fotografia> getFotos() {
        return fotos;
    }

    public void setFotos(List<Fotografia> fotos) {
        this.fotos = fotos;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getComentario() {
        return Comentario;
    }

    public void setComentario(String comentario) {
        Comentario = comentario;
    }

    public Date getDiaSubida() {
        return diaSubida;
    }

    public void setDiaSubida(Date diaSubida) {
        this.diaSubida = diaSubida;
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
}
