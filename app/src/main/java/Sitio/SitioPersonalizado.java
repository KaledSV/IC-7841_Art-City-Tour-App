package Sitio;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SitioPersonalizado implements Serializable {
    transient String idSitioPersonalizado;
    String idSitio;
    String nombre;
    String tipoSitio;
    String idFotoPredeterminada;
    String comentario;
    Timestamp horaVisita;

    public SitioPersonalizado(String idSitioPersonalizado, String idSitio, String nombre, String tipoSitio, String idFotoPredeterminada, String comentario, Timestamp horaVisita) {
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

    public Timestamp getHoraVisita() {
        return horaVisita;
    }

    public void setHoraVisita(Timestamp horaVisita) {
        this.horaVisita = horaVisita;
    }

    public String getFieldValues(){
        return "idSitio: " + this.idSitio + " nombre: " + this.nombre + ", tipo: " +
                this.tipoSitio + "foto: " + this.idFotoPredeterminada + ", comentario: " +
                this.comentario + ", horario: " + this.horaVisita;
    }

    // bd methods
    public void errorUpdatingPersonalizedSite(View view){
        new AlertDialog.Builder(view.getContext())
                .setTitle("Error")
                .setMessage("Ha ocurrido un error al intentar actualizar el sitio de su plan personalizado")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public void updateCommentBd(String comment, View view, TextView editTextComment){
        SitioPersonalizado site = this;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SitioPersonalizado")
                .document(site.getIdSitioPersonalizado())
                .update("comentario", comment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        site.setComentario(comment);
                        editTextComment.setText(comment);
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUpdatingPersonalizedSite(view);
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

    public void updateScheduleBd(Date date, View view, TextView siteScheduleTextView){
        Log.d("Dateeeeeeee", date.toString());
        SitioPersonalizado site = this;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("SitioPersonalizado")
                .document(site.getIdSitioPersonalizado())
                .update("horaVisita", date)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        site.setHoraVisita(new Timestamp(date));
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        formatter.setTimeZone(TimeZone.getTimeZone("UTC-6"));
                        siteScheduleTextView.setText(formatter.format(date));
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errorUpdatingPersonalizedSite(view);
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }
}
