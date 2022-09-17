package Ruta;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.List;

import Sitio.Sitio;

public class Ruta implements Serializable, Parcelable {
    //Atributos

    private String idRoute;
    private String nombre;
    private int cantSitios;
    private String fotoPredeterminada;
    List<Sitio> sitios;

    //Constructores
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(idRoute);
        parcel.writeString(nombre);
        parcel.writeInt(cantSitios);
        parcel.writeString(fotoPredeterminada);
    }

    public Ruta(String idRoute, String nombre, int cantSitios, String fotoPredeterminada, List<Sitio> sitios) {
        this.idRoute = idRoute;
        this.nombre = nombre;
        this.cantSitios = cantSitios;
        this.fotoPredeterminada = fotoPredeterminada;
        this.sitios = sitios;
    }

    public Ruta(){

    }

    protected Ruta(Parcel in) {
        idRoute = in.readString();
        nombre = in.readString();
        cantSitios = in.readInt();
        fotoPredeterminada = in.readString();
    }

    public static final Creator<Ruta> CREATOR = new Creator<Ruta>() {
        @Override
        public Ruta createFromParcel(Parcel in) {
            return new Ruta(in);
        }

        @Override
        public Ruta[] newArray(int size) {
            return new Ruta[size];
        }
    };

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

    public String getFotoPredeterminada() {
        return fotoPredeterminada;
    }

    public void setFotoPredeterminada(String fotoPredeterminada) {
        this.fotoPredeterminada = fotoPredeterminada;
    }

    public String getIdRoute() {
        return idRoute;
    }

    public void setIdRoute(String idRoute) {
        this.idRoute = idRoute;
    }

    public String getFieldValues(){
        return "idRuta: " + this.idRoute + ", nombre: " + this.nombre + ", cantSitios: " + this.cantSitios
            + ", fotoPath: " + this.fotoPredeterminada;
    }
}
