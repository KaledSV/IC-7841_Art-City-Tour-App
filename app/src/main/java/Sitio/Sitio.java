package Sitio;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.List;

import Fotografia.Fotografia;
import Resenna.Resenna;

public class Sitio implements Serializable, Parcelable {

    //Atributos
    private String idSite;
    private String nombre;
    private String tipoSitio;
    private GeoPoint coordenadas;
    private String nombreRuta;
    private String descripcion;
    private int capacidad;
    private int tiempoEspera;
    private String idFotoPredeterminada;
    List<Fotografia> fotos = null;
    List<Resenna> resenna = null;

    public String getIdSite() {
        return idSite;
    }

    public void setIdSite(String idSite) {
        this.idSite = idSite;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public GeoPoint getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(GeoPoint coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getNombreRuta() {
        return nombreRuta;
    }

    public void setNombreRuta(String nombreRuta) {
        this.nombreRuta = nombreRuta;
    }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public int getTiempoEspera() {
        return tiempoEspera;
    }

    public void setTiempoEspera(int tiempoEspera) {
        this.tiempoEspera = tiempoEspera;
    }

    public String getIdFotoPredeterminada() {
        return idFotoPredeterminada;
    }

    public void setIdFotoPredeterminada(String idFotoPredeterminada) {
        this.idFotoPredeterminada = idFotoPredeterminada;
    }

    public String getTipoSitio() {
        return tipoSitio;
    }

    public void setTipoSitio(String tipoSitio) {
        this.tipoSitio = tipoSitio;
    }

    // Constructors

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(idSite);
        parcel.writeString(nombre);
        parcel.writeString(tipoSitio);
        parcel.writeString(nombreRuta);
        parcel.writeString(descripcion);
        parcel.writeInt(capacidad);
        parcel.writeInt(tiempoEspera);
        parcel.writeString(idFotoPredeterminada);
        parcel.writeDouble(coordenadas.getLatitude());
        parcel.writeDouble(coordenadas.getLongitude());
    }

    public Sitio(String idSite, String nombre, String tipoSitio, GeoPoint direccion, String nombreRuta, int capacidad, int tiempoEspera, String idFotoPredeterminada) {
        this.idSite = idSite;
        this.nombre = nombre;
        this.tipoSitio = tipoSitio;
        this.coordenadas = direccion;
        this.nombreRuta = nombreRuta;
        this.capacidad = capacidad;
        this.tiempoEspera = tiempoEspera;
        this.idFotoPredeterminada = idFotoPredeterminada;
    }

    public Sitio(){

    }

    protected Sitio(Parcel in) {
        idSite = in.readString();
        nombre = in.readString();
        tipoSitio = in.readString();
        nombreRuta = in.readString();
        descripcion = in.readString();
        capacidad = in.readInt();
        tiempoEspera = in.readInt();
        idFotoPredeterminada = in.readString();
        Double lat = in.readDouble();
        Double lng = in.readDouble();
        coordenadas = new GeoPoint(lat, lng);
    }

    public static final Creator<Sitio> CREATOR = new Creator<Sitio>() {
        @Override
        public Sitio createFromParcel(Parcel in) {
            return new Sitio(in);
        }

        @Override
        public Sitio[] newArray(int size) {
            return new Sitio[size];
        }
    };

    //Métodos

    public String getFieldValues(){
        return "idSitio: " + this.idSite + " nombre: " + this.nombre + ", tipo: " + this.tipoSitio + "descripcion: "
                + this.descripcion + ", nombreRuta: " + this.nombreRuta + ", Coordenadas: Latitud " +
                this.coordenadas.getLatitude() + " Longitud " + this.coordenadas.getLongitude() +
                ", capacidad: " + this.capacidad + ", tiempoEspera: " + this.getTiempoEspera() +
                ", idFotoPredeterminada: " + this.idFotoPredeterminada;
    }
}
