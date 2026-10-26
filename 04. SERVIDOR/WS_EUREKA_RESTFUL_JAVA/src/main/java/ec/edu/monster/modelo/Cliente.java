package ec.edu.monster.modelo;

/**
 * Cliente del banco (tabla cliente).
 *
 * @author Grupo 1 - NRC 36841
 */
public class Cliente {

    /** Código de 5 dígitos (chr_cliecodigo) */
    private String codigo;
    /** Apellido paterno */
    private String paterno;
    /** Apellido materno */
    private String materno;
    /** Nombres */
    private String nombre;
    /** Documento de identidad (8 dígitos) */
    private String cedula;
    /** Ciudad */
    private String ciudad;
    /** Dirección */
    private String direccion;
    /** Teléfono (opcional) */
    private String telefono;
    /** Correo (opcional) */
    private String email;

    public Cliente() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPaterno() {
        return paterno;
    }

    public void setPaterno(String paterno) {
        this.paterno = paterno;
    }

    public String getMaterno() {
        return materno;
    }

    public void setMaterno(String materno) {
        this.materno = materno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
