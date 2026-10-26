package ec.edu.monster.modelo;

import java.util.Map;

/**
 * Resultado de un depósito, retiro o transferencia.
 *
 * @author Grupo 1 - NRC 36841
 */
public class ResultadoOperacion {

    private String operacion;
    private String cuenta;
    private String cuentaDestino;
    private double importe;
    private double saldoAnterior;
    private double saldoActual;
    private String moneda;
    private int numeroMovimiento;
    private String empleado;
    private String fecha;

    public ResultadoOperacion() {
    }

    /** Construye el objeto a partir del mapa JSON que devuelve el servidor. */
    public static ResultadoOperacion desdeMapa(Map<?, ?> m) {
        ResultadoOperacion o = new ResultadoOperacion();
        o.operacion = m.get("operacion") == null ? null : String.valueOf(m.get("operacion"));
        o.cuenta = m.get("cuenta") == null ? null : String.valueOf(m.get("cuenta"));
        o.cuentaDestino = m.get("cuentaDestino") == null ? null : String.valueOf(m.get("cuentaDestino"));
        o.importe = m.get("importe") == null ? 0 : ((Number) m.get("importe")).doubleValue();
        o.saldoAnterior = m.get("saldoAnterior") == null ? 0 : ((Number) m.get("saldoAnterior")).doubleValue();
        o.saldoActual = m.get("saldoActual") == null ? 0 : ((Number) m.get("saldoActual")).doubleValue();
        o.moneda = m.get("moneda") == null ? null : String.valueOf(m.get("moneda"));
        o.numeroMovimiento = m.get("numeroMovimiento") == null ? 0 : ((Number) m.get("numeroMovimiento")).intValue();
        o.empleado = m.get("empleado") == null ? null : String.valueOf(m.get("empleado"));
        o.fecha = m.get("fecha") == null ? null : String.valueOf(m.get("fecha"));
        return o;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public double getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(double saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public double getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(double saldoActual) {
        this.saldoActual = saldoActual;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public int getNumeroMovimiento() {
        return numeroMovimiento;
    }

    public void setNumeroMovimiento(int numeroMovimiento) {
        this.numeroMovimiento = numeroMovimiento;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}
