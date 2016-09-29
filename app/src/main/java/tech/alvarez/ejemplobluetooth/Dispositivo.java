package tech.alvarez.ejemplobluetooth;

/**
 * Created by danyalvarez on 9/29/16.
 */

public class Dispositivo {

    private String nombre;
    private String direccion;

    public Dispositivo(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
