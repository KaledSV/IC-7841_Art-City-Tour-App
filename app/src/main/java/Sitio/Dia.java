package Sitio;

public enum Dia {
    LUNES(1),
    MARTES(2),
    MIERCOLES(3),
    JUEVES(4),
    VIERNES(5),
    SABADO(6),
    DOMINGO(7);

    private final int valor;

    Dia(int pvalor) {
        this.valor = pvalor;
    }

    private int valor() {return valor;}
}
