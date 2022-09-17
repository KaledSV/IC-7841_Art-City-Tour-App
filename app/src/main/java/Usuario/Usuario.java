package Usuario;

public abstract class Usuario {

    //Atributos
    private String id;
    private String nombre;
    private int numero;
    private String correo;
    private String contrasenna;

    //Constructor
    public Usuario(String id, String nombre, int numero, String correo, String contrasenna) {
        this.id = id;
        this.nombre = nombre;
        this.numero = numero;
        this.correo = correo;
        this.contrasenna = contrasenna;
    }

    public Usuario (){

    }

    //Metodos
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenna() {
        return contrasenna;
    }

    public void setContrasenna(String contrasenna) {
        this.contrasenna = contrasenna;
    }

    public void registrarse(){
        //TODO
    }

    public void iniciarSesion(){
        //TODO
    }

}

