package Resenna;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Fotografia.Fotografia;
import Sitio.Sitio;
import Usuario.VisitanteSingleton;

public class Resenna implements Serializable {
    //Atributos
    private transient String idResenna;
    private String autor;
    private int calificacion;
    private String comentario;
    private Date fechaSubida;
    private int likes;
    private int dislikes;
    private boolean tieneFotos;
    private transient List<Fotografia> fotos;

    //Constructor

    public Resenna(String idResenna, String autor, int calificacion, String comentario, Date fechaSubida, int likes, int dislikes, boolean tieneFotos, List<Fotografia> fotos) {
        this.idResenna = idResenna;
        this.autor = autor;
        this.calificacion = calificacion;
        this.comentario = comentario;
        this.fechaSubida = fechaSubida;
        this.likes = likes;
        this.dislikes = dislikes;
        this.tieneFotos = tieneFotos;
        this.fotos = fotos;
    }

    public Resenna(){

    }

    //Metodos

    public String getIdResenna() {
        return idResenna;
    }

    public void setIdResenna(String idResenna) {
        this.idResenna = idResenna;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
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

    public boolean isTieneFotos() {
        return tieneFotos;
    }

    public void setTieneFotos(boolean tieneFotos) {
        this.tieneFotos = tieneFotos;
    }

    public List<Fotografia> getFotos() {
        return fotos;
    }

    public void setFotos(List<Fotografia> fotos) {
        this.fotos = fotos;
    }
}
