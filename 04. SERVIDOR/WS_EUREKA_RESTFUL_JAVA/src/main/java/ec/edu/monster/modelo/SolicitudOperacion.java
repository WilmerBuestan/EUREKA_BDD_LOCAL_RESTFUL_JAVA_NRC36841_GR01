package ec.edu.monster.modelo;

/**
 * Datos que envía el cliente para depósito, retiro o transferencia.
 *
 * @author Grupo 1 - NRC 36841
 */
public class SolicitudOperacion {

    /** Cuenta (origen en transferencias) */
    private String cuenta;
    /** Cuenta destino (solo transferencias) */
    private String cuentaDestino;
    /** Importe de la operación */
    private double importe;

    public SolicitudOperacion() {
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

}
