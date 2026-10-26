package ec.edu.monster.dao;

import ec.edu.monster.db.ConexionBD;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.Opcion;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla cuenta (con su cliente, moneda y sucursal).
 * Los métodos que reciben una Connection participan en la transacción del
 * servicio de operaciones; los demás abren y cierran su propia conexión.
 *
 * @author Grupo 1 - NRC 36841
 */
public class CuentaDAO {

    private static final String SELECT = "SELECT c.chr_cuencodigo, c.chr_cliecodigo, "
            + "CONCAT(cl.vch_clienombre, ' ', cl.vch_cliepaterno, ' ', cl.vch_cliematerno) AS cliente, "
            + "c.chr_monecodigo, m.vch_monedescripcion, s.vch_sucunombre, c.dec_cuensaldo, c.vch_cuenestado, "
            + "DATE_FORMAT(c.dtt_cuenfechacreacion, '%Y-%m-%d') AS fecha, c.int_cuencontmov "
            + "FROM cuenta c JOIN cliente cl ON cl.chr_cliecodigo = c.chr_cliecodigo "
            + "JOIN moneda m ON m.chr_monecodigo = c.chr_monecodigo "
            + "JOIN sucursal s ON s.chr_sucucodigo = c.chr_sucucodigo ";

    private final ConexionBD bd = ConexionBD.getInstancia();

    /** SELECT de todas las cuentas. */
    public List<Cuenta> listar() throws SQLException {
        List<Cuenta> lista = new ArrayList<>();
        try (Connection cn = bd.getConexion();
             PreparedStatement ps = cn.prepareStatement(SELECT + "ORDER BY c.chr_cuencodigo");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** SELECT de una cuenta; null si no existe. */
    public Cuenta seleccionar(String codigo) throws SQLException {
        try (Connection cn = bd.getConexion()) {
            return seleccionar(cn, codigo, false);
        }
    }

    /**
     * SELECT dentro de una transacción. Con bloquear=true usa FOR UPDATE para
     * que nadie modifique el saldo hasta el commit.
     */
    public Cuenta seleccionar(Connection cn, String codigo, boolean bloquear) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(SELECT + "WHERE c.chr_cuencodigo = ?" + (bloquear ? " FOR UPDATE" : ""))) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** UPDATE del saldo y del contador de movimientos (dentro de la transacción). */
    public void actualizarSaldo(Connection cn, String codigo, BigDecimal saldo, int contadorMovimientos) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE cuenta SET dec_cuensaldo = ?, int_cuencontmov = ? WHERE chr_cuencodigo = ?")) {
            ps.setBigDecimal(1, saldo);
            ps.setInt(2, contadorMovimientos);
            ps.setString(3, codigo);
            if (ps.executeUpdate() != 1) {
                throw new SQLException("No se pudo actualizar la cuenta " + codigo);
            }
        }
    }

    /**
     * Siguiente número de cuenta de la sucursal: código de sucursal (3) +
     * correlativo (5). Usa el contador de la sucursal (bloqueado) y, por
     * seguridad, el mayor correlativo ya existente.
     */
    public String siguienteCodigo(Connection cn, String sucursal) throws SQLException {
        int contador;
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT int_sucucontcuenta FROM sucursal WHERE chr_sucucodigo = ? FOR UPDATE")) {
            ps.setString(1, sucursal);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                contador = rs.getInt(1);
            }
        }
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT COALESCE(MAX(CAST(SUBSTRING(chr_cuencodigo, 4) AS UNSIGNED)), 0) FROM cuenta WHERE chr_sucucodigo = ?")) {
            ps.setString(1, sucursal);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                contador = Math.max(contador, rs.getInt(1));
            }
        }
        int siguiente = contador + 1;
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE sucursal SET int_sucucontcuenta = ? WHERE chr_sucucodigo = ?")) {
            ps.setInt(1, siguiente);
            ps.setString(2, sucursal);
            ps.executeUpdate();
        }
        return sucursal + String.format("%05d", siguiente);
    }

    /** INSERT de una cuenta nueva en estado ACTIVO (dentro de la transacción). */
    public void insertar(Connection cn, String codigo, String moneda, String sucursal, String empleado,
                         String cliente, BigDecimal saldo, String clave) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("INSERT INTO cuenta (chr_cuencodigo, chr_monecodigo, "
                + "chr_sucucodigo, chr_emplcreacuenta, chr_cliecodigo, dec_cuensaldo, dtt_cuenfechacreacion, "
                + "vch_cuenestado, int_cuencontmov, chr_cuenclave) VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, 'ACTIVO', 1, ?)")) {
            ps.setString(1, codigo);
            ps.setString(2, moneda);
            ps.setString(3, sucursal);
            ps.setString(4, empleado);
            ps.setString(5, cliente);
            ps.setBigDecimal(6, saldo);
            ps.setString(7, clave);
            ps.executeUpdate();
        }
    }

    /** SELECT de monedas o sucursales para las listas de los clientes. */
    public List<Opcion> listarCatalogo(String tabla) throws SQLException {
        String sql = "moneda".equals(tabla)
                ? "SELECT chr_monecodigo, vch_monedescripcion FROM moneda ORDER BY (vch_monedescripcion LIKE 'D%') DESC, 1"
                : "SELECT chr_sucucodigo, CONCAT(vch_sucunombre, ' - ', vch_sucuciudad) FROM sucursal ORDER BY 1";
        List<Opcion> lista = new ArrayList<>();
        try (Connection cn = bd.getConexion(); PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Opcion o = new Opcion();
                o.setCodigo(rs.getString(1));
                o.setNombre(rs.getString(2));
                lista.add(o);
            }
        }
        return lista;
    }

    private Cuenta mapear(ResultSet rs) throws SQLException {
        Cuenta c = new Cuenta();
        c.setCodigo(rs.getString(1));
        c.setCodigoCliente(rs.getString(2));
        c.setCliente(rs.getString(3));
        c.setCodigoMoneda(rs.getString(4));
        c.setMoneda(rs.getString(5));
        c.setSucursal(rs.getString(6));
        c.setSaldo(rs.getBigDecimal(7).doubleValue());
        c.setEstado(rs.getString(8));
        c.setFechaCreacion(rs.getString(9));
        c.setTotalMovimientos(rs.getInt(10));
        return c;
    }
}
