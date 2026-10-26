package ec.edu.monster.modelo;

/**
 * Resultado de una operación bancaria.
 *
 * @author Grupo 1 - NRC 36841
 */
public class ResultadoOperacion {

    /** DEPOSITO / RETIRO / TRANSFERENCIA */
    private String operacion;
    /** Cuenta afectada (origen) */
    private String cuenta;
    /** Cuenta destino (transferencias) */
    private String cuentaDestino;
    /** Importe procesado */
    private double importe;
    /** Saldo antes de la operación */
    private double saldoAnterior;
    /** Saldo después de la operación */
    private double saldoActual;
    /** Moneda de la cuenta */
    private String moneda;
    /** Número de movimiento generado */
    private int numeroMovimiento;
    /** Empleado que registró la operación */
    private String empleado;
    /** Fecha y hora de la operación */
    private String fecha;

    public ResultadoOperacion() {
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
