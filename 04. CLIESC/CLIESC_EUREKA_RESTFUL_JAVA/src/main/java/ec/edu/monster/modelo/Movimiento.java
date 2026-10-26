package ec.edu.monster.modelo;

import java.util.Map;

/**
 * Movimiento de una cuenta.
 *
 * @author Grupo 1 - NRC 36841
 */
public class Movimiento {

    private String cuenta;
    private int numero;
    private String fecha;
    private String empleado;
    private String codigoTipo;
    private String tipo;
    private String accion;
    private double importe;
    private String referencia;

    public Movimiento() {
    }

    /** Construye el objeto a partir del mapa JSON que devuelve el servidor. */
    public static Movimiento desdeMapa(Map<?, ?> m) {
        Movimiento o = new Movimiento();
        o.cuenta = m.get("cuenta") == null ? null : String.valueOf(m.get("cuenta"));
        o.numero = m.get("numero") == null ? 0 : ((Number) m.get("numero")).intValue();
        o.fecha = m.get("fecha") == null ? null : String.valueOf(m.get("fecha"));
        o.empleado = m.get("empleado") == null ? null : String.valueOf(m.get("empleado"));
        o.codigoTipo = m.get("codigoTipo") == null ? null : String.valueOf(m.get("codigoTipo"));
        o.tipo = m.get("tipo") == null ? null : String.valueOf(m.get("tipo"));
        o.accion = m.get("accion") == null ? null : String.valueOf(m.get("accion"));
        o.importe = m.get("importe") == null ? 0 : ((Number) m.get("importe")).doubleValue();
        o.referencia = m.get("referencia") == null ? null : String.valueOf(m.get("referencia"));
        return o;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public String getCodigoTipo() {
        return codigoTipo;
    }

    public void setCodigoTipo(String codigoTipo) {
        this.codigoTipo = codigoTipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public boolean isIngreso() {
        return "INGRESO".equalsIgnoreCase(accion);
    }

}
