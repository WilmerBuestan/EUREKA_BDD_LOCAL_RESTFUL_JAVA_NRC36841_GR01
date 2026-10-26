package ec.edu.monster.modelo;

import java.util.Map;

/**
 * Cliente del banco.
 *
 * @author Grupo 1 - NRC 36841
 */
public class Cliente {

    private String codigo;
    private String paterno;
    private String materno;
    private String nombre;
    private String cedula;
    private String ciudad;
    private String direccion;
    private String telefono;
    private String email;

    public Cliente() {
    }

    /** Construye el objeto a partir del mapa JSON que devuelve el servidor. */
    public static Cliente desdeMapa(Map<?, ?> m) {
        Cliente o = new Cliente();
        o.codigo = m.get("codigo") == null ? null : String.valueOf(m.get("codigo"));
        o.paterno = m.get("paterno") == null ? null : String.valueOf(m.get("paterno"));
        o.materno = m.get("materno") == null ? null : String.valueOf(m.get("materno"));
        o.nombre = m.get("nombre") == null ? null : String.valueOf(m.get("nombre"));
        o.cedula = m.get("cedula") == null ? null : String.valueOf(m.get("cedula"));
        o.ciudad = m.get("ciudad") == null ? null : String.valueOf(m.get("ciudad"));
        o.direccion = m.get("direccion") == null ? null : String.valueOf(m.get("direccion"));
        o.telefono = m.get("telefono") == null ? null : String.valueOf(m.get("telefono"));
        o.email = m.get("email") == null ? null : String.valueOf(m.get("email"));
        return o;
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

    public String getNombreCompleto() {
        return nombre + " " + paterno + " " + materno;
    }

    /** Mapa JSON para enviar al servidor (POST / PUT). */
    public Map<String, Object> aMapa() {
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("paterno", paterno);
        m.put("materno", materno);
        m.put("nombre", nombre);
        m.put("cedula", cedula);
        m.put("ciudad", ciudad);
        m.put("direccion", direccion);
        m.put("telefono", telefono);
        m.put("email", email);
        return m;
    }

}
