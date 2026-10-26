package ec.edu.monster.modelo;

/**
 * Elemento de un catálogo (moneda o sucursal).
 *
 * @author Grupo 1 - NRC 36841
 */
public class Opcion {

    /** Código */
    private String codigo;
    /** Descripción */
    private String nombre;

    public Opcion() {
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

}
