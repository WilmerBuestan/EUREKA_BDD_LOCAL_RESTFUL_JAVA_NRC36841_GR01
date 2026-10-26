package ec.edu.monster.modelo;

import java.util.Map;

/**
 * Cuenta bancaria tal como la devuelve el servidor.
 *
 * @author Grupo 1 - NRC 36841
 */
public class Cuenta {

    private String codigo;
    private String codigoCliente;
    private String cliente;
    private String moneda;
    private String codigoMoneda;
    private String sucursal;
    private double saldo;
    private String estado;
    private String fechaCreacion;
    private int totalMovimientos;

    public Cuenta() {
    }

    /** Construye el objeto a partir del mapa JSON que devuelve el servidor. */
    public static Cuenta desdeMapa(Map<?, ?> m) {
        Cuenta o = new Cuenta();
        o.codigo = m.get("codigo") == null ? null : String.valueOf(m.get("codigo"));
        o.codigoCliente = m.get("codigoCliente") == null ? null : String.valueOf(m.get("codigoCliente"));
        o.cliente = m.get("cliente") == null ? null : String.valueOf(m.get("cliente"));
        o.moneda = m.get("moneda") == null ? null : String.valueOf(m.get("moneda"));
        o.codigoMoneda = m.get("codigoMoneda") == null ? null : String.valueOf(m.get("codigoMoneda"));
        o.sucursal = m.get("sucursal") == null ? null : String.valueOf(m.get("sucursal"));
        o.saldo = m.get("saldo") == null ? 0 : ((Number) m.get("saldo")).doubleValue();
        o.estado = m.get("estado") == null ? null : String.valueOf(m.get("estado"));
        o.fechaCreacion = m.get("fechaCreacion") == null ? null : String.valueOf(m.get("fechaCreacion"));
        o.totalMovimientos = m.get("totalMovimientos") == null ? 0 : ((Number) m.get("totalMovimientos")).intValue();
        return o;
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

    public boolean isActiva() {
        return "ACTIVO".equalsIgnoreCase(estado);
    }

    /** Texto para listas desplegables. */
    @Override
    public String toString() {
        return codigo + " - " + cliente + " (" + moneda + ")";
    }

}
