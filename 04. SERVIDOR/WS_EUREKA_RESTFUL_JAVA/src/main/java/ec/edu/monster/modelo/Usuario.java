package ec.edu.monster.modelo;

/**
 * Usuario del sistema (tabla usuario + empleado).
 *
 * @author Grupo 1 - NRC 36841
 */
public class Usuario {

    /** Código del empleado */
    private String codigoEmpleado;
    /** Nombre de usuario */
    private String usuario;
    /** Nombre del empleado */
    private String nombreCompleto;
    /** ACTIVO / ANULADO */
    private String estado;

    public Usuario() {
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
