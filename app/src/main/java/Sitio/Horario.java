package Sitio;

import androidx.annotation.NonNull;

import java.sql.Time;

public class Horario {
    //Atributos
    private Dia dia;
    private Time abrir;
    private Time cerrar;


    //Constructor

    public Horario(Dia dia, Time abrir, Time cerrar) {
        this.dia = dia;
        this.abrir = abrir;
        this.cerrar = cerrar;
    }

    //Metodos


    public Dia getDia() {
        return dia;
    }

    public void setDia(Dia dia) {
        this.dia = dia;
    }

    public Time getAbrir() {
        return abrir;
    }

    public void setAbrir(Time abrir) {
        this.abrir = abrir;
    }

    public Time getCerrar() {
        return cerrar;
    }

    public void setCerrar(Time cerrar) {
        this.cerrar = cerrar;
    }

    @NonNull
    @Override
    public String toString() {
        return "Horario{" +
                "dia=" + dia +
                ", abierto=" + abrir +
                ", cerrado=" + cerrar +
                '}';
    }
}
