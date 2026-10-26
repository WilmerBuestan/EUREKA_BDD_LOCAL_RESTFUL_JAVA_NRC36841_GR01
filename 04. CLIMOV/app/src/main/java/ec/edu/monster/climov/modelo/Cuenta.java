package ec.edu.monster.climov.modelo;

/**
 * Cuenta bancaria devuelta por el servidor.
 *
 * @author Grupo 1 - NRC 36841
 */
public class Cuenta {

    public String codigo;
    public String codigoCliente;
    public String cliente;
    public String moneda;
    public String codigoMoneda;
    public String sucursal;
    public double saldo;
    public String estado;
    public String fechaCreacion;
    public int totalMovimientos;

    public boolean activa() {
        return "ACTIVO".equalsIgnoreCase(estado);
    }

    /** Texto mostrado en las listas desplegables. */
    @Override
    public String toString() {
        return codigo + " · " + cliente + " (" + moneda + ")";
    }
}
