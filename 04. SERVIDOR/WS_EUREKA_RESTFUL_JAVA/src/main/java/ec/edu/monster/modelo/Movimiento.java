package ec.edu.monster.modelo;

/**
 * Movimiento de una cuenta (tabla movimiento + tipomovimiento).
 *
 * @author Grupo 1 - NRC 36841
 */
public class Movimiento {

    /** Cuenta del movimiento */
    private String cuenta;
    /** Número correlativo */
    private int numero;
    /** Fecha (yyyy-MM-dd) */
    private String fecha;
    /** Código del empleado que lo registró */
    private String empleado;
    /** Código del tipo de movimiento */
    private String codigoTipo;
    /** Descripción del tipo */
    private String tipo;
    /** INGRESO o SALIDA */
    private String accion;
    /** Importe */
    private double importe;
    /** Cuenta relacionada (transferencias) */
    private String referencia;

    public Movimiento() {
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

}
