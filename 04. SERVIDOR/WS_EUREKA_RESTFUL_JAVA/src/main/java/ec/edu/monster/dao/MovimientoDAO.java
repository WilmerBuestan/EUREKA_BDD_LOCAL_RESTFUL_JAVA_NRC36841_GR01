package ec.edu.monster.dao;

import ec.edu.monster.db.ConexionBD;
import ec.edu.monster.modelo.Movimiento;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla movimiento.
 *
 * @author Grupo 1 - NRC 36841
 */
public class MovimientoDAO {

    private final ConexionBD bd = ConexionBD.getInstancia();

    /** SELECT de los movimientos de una cuenta, del más reciente al más antiguo. */
    public List<Movimiento> listar(String cuenta) throws SQLException {
        String sql = "SELECT m.chr_cuencodigo, m.int_movinumero, DATE_FORMAT(m.dtt_movifecha, '%Y-%m-%d'), "
                + "m.chr_emplcodigo, m.chr_tipocodigo, t.vch_tipodescripcion, t.vch_tipoaccion, "
                + "m.dec_moviimporte, m.chr_cuenreferencia "
                + "FROM movimiento m JOIN tipomovimiento t ON t.chr_tipocodigo = m.chr_tipocodigo "
                + "WHERE m.chr_cuencodigo = ? ORDER BY m.int_movinumero DESC";
        List<Movimiento> lista = new ArrayList<>();
        try (Connection cn = bd.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cuenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movimiento m = new Movimiento();
                    m.setCuenta(rs.getString(1));
                    m.setNumero(rs.getInt(2));
                    m.setFecha(rs.getString(3));
                    m.setEmpleado(rs.getString(4));
                    m.setCodigoTipo(rs.getString(5));
                    m.setTipo(rs.getString(6));
                    m.setAccion(rs.getString(7));
                    m.setImporte(rs.getBigDecimal(8).doubleValue());
                    m.setReferencia(rs.getString(9));
                    lista.add(m);
                }
            }
        }
        return lista;
    }

    /** INSERT de un movimiento (dentro de la transacción de la operación). */
    public void insertar(Connection cn, String cuenta, int numero, String empleado, String tipo,
                         BigDecimal importe, String referencia) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement("INSERT INTO movimiento (chr_cuencodigo, int_movinumero, "
                + "dtt_movifecha, chr_emplcodigo, chr_tipocodigo, dec_moviimporte, chr_cuenreferencia) "
                + "VALUES (?, ?, CURRENT_DATE, ?, ?, ?, ?)")) {
            ps.setString(1, cuenta);
            ps.setInt(2, numero);
            ps.setString(3, empleado);
            ps.setString(4, tipo);
            ps.setBigDecimal(5, importe);
            ps.setString(6, referencia);
            ps.executeUpdate();
        }
    }
}
