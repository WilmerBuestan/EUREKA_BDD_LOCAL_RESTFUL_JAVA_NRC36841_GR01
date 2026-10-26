package ec.edu.monster.modelo;

/**
 * Datos para abrir una cuenta nueva a un cliente.
 *
 * @author Grupo 1 - NRC 36841
 */
public class SolicitudApertura {

    /** Cliente titular (5 dígitos) */
    private String codigoCliente;
    /** Código de moneda (02 = Dólares) */
    private String codigoMoneda;
    /** Sucursal (3 dígitos) */
    private String codigoSucursal;
    /** Depósito inicial */
    private double importe;
    /** Clave de la cuenta (6 dígitos) */
    private String clave;

    public SolicitudApertura() {
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getCodigoMoneda() {
        return codigoMoneda;
    }

    public void setCodigoMoneda(String codigoMoneda) {
        this.codigoMoneda = codigoMoneda;
    }

    public String getCodigoSucursal() {
        return codigoSucursal;
    }

    public void setCodigoSucursal(String codigoSucursal) {
        this.codigoSucursal = codigoSucursal;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

}
