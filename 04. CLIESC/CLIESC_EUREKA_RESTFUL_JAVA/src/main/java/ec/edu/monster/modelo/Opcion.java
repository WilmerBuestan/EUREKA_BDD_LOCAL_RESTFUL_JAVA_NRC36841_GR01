package ec.edu.monster.modelo;

/**
 * Elemento de un catálogo del servidor (moneda o sucursal).
 *
 * @author Grupo 1 - NRC 36841
 */
public class Opcion {

    private String codigo;
    private String nombre;

    public Opcion() {
    }

    public Opcion(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** Texto para listas desplegables. */
    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
