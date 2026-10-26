package ec.edu.monster.modelo;

/**
 * Cuenta bancaria con datos de su cliente, moneda y sucursal.
 *
 * @author Grupo 1 - NRC 36841
 */
public class Cuenta {

    /** Número de cuenta de 8 dígitos */
    private String codigo;
    /** Código del cliente titular */
    private String codigoCliente;
    /** Nombre completo del titular */
    private String cliente;
    /** Moneda (Dólares) */
    private String moneda;
    /** Código de moneda */
    private String codigoMoneda;
    /** Nombre de la sucursal */
    private String sucursal;
    /** Saldo actual */
    private double saldo;
    /** ACTIVO / ANULADO / CANCELADO */
    private String estado;
    /** Fecha de apertura */
    private String fechaCreacion;
    /** Contador de movimientos */
    private int totalMovimientos;

    public Cuenta() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getCodigoMoneda() {
        return codigoMoneda;
    }

    public void setCodigoMoneda(String codigoMoneda) {
        this.codigoMoneda = codigoMoneda;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getTotalMovimientos() {
        return totalMovimientos;
    }

    public void setTotalMovimientos(int totalMovimientos) {
        this.totalMovimientos = totalMovimientos;
    }

}
