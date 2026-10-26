package ec.edu.monster.dao;

import ec.edu.monster.db.ConexionBD;
import ec.edu.monster.modelo.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla cliente. CRUD con los nombres pedidos:
 * insertar, seleccionar, listar, actualizar y eliminar.
 * Cada método pide la conexión al Singleton y la cierra (try-with-resources).
 *
 * @author Grupo 1 - NRC 36841
 */
public class ClienteDAO {

    private static final String COLUMNAS = "chr_cliecodigo, vch_cliepaterno, vch_cliematerno, vch_clienombre, "
            + "chr_cliedni, vch_clieciudad, vch_cliedireccion, vch_clietelefono, vch_clieemail";

    private final ConexionBD bd = ConexionBD.getInstancia();

    /**
     * INSERT. El código se genera con la tabla contador (como en el modelo
     * original de EurekaBank) dentro de una transacción.
     *
     * @return el cliente con su código asignado
     */
    public Cliente insertar(Cliente c) throws SQLException {
        try (Connection cn = bd.getConexion()) {
            cn.setAutoCommit(false);
            try {
                int item;
                int longitud;
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT int_contitem, int_contlongitud FROM contador WHERE vch_conttabla = 'Cliente' FOR UPDATE");
                     ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("No existe el contador de clientes");
                    }
                    item = rs.getInt(1) + 1;
                    longitud = rs.getInt(2);
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE contador SET int_contitem = ? WHERE vch_conttabla = 'Cliente'")) {
                    ps.setInt(1, item);
                    ps.executeUpdate();
                }
                c.setCodigo(String.format("%0" + longitud + "d", item));
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO cliente (" + COLUMNAS + ") VALUES (?,?,?,?,?,?,?,?,?)")) {
                    ps.setString(1, c.getCodigo());
                    llenar(ps, c, 2);
                    ps.executeUpdate();
                }
                cn.commit();
                return c;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        }
    }

    /** SELECT por código; null si no existe. */
    public Cliente seleccionar(String codigo) throws SQLException {
        try (Connection cn = bd.getConexion();
             PreparedStatement ps = cn.prepareStatement("SELECT " + COLUMNAS + " FROM cliente WHERE chr_cliecodigo = ?")) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** SELECT de todos los clientes; si hay filtro busca por nombre, apellidos, cédula o código. */
    public List<Cliente> listar(String filtro) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM cliente";
        boolean filtrar = filtro != null && !filtro.isBlank();
        if (filtrar) {
            sql += " WHERE CONCAT_WS(' ', chr_cliecodigo, vch_clienombre, vch_cliepaterno, vch_cliematerno, chr_cliedni) LIKE ?";
        }
        sql += " ORDER BY chr_cliecodigo";
        List<Cliente> lista = new ArrayList<>();
        try (Connection cn = bd.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            if (filtrar) {
                ps.setString(1, "%" + filtro.trim() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /** UPDATE por código; true si se modificó una fila. */
    public boolean actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET vch_cliepaterno=?, vch_cliematerno=?, vch_clienombre=?, chr_cliedni=?, "
                + "vch_clieciudad=?, vch_cliedireccion=?, vch_clietelefono=?, vch_clieemail=? WHERE chr_cliecodigo=?";
        try (Connection cn = bd.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            llenar(ps, c, 1);
            ps.setString(9, c.getCodigo());
            return ps.executeUpdate() == 1;
        }
    }

    /** DELETE por código; true si se eliminó una fila. */
    public boolean eliminar(String codigo) throws SQLException {
        try (Connection cn = bd.getConexion();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM cliente WHERE chr_cliecodigo = ?")) {
            ps.setString(1, codigo);
            return ps.executeUpdate() == 1;
        }
    }

    /** Cuántas cuentas tiene el cliente (no se puede eliminar si tiene alguna). */
    public int contarCuentas(String codigo) throws SQLException {
        try (Connection cn = bd.getConexion();
             PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM cuenta WHERE chr_cliecodigo = ?")) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** true si otro cliente (distinto de excluirCodigo) ya usa esa cédula. */
    public boolean existeCedula(String cedula, String excluirCodigo) throws SQLException {
        try (Connection cn = bd.getConexion(); PreparedStatement ps = cn.prepareStatement(
                "SELECT 1 FROM cliente WHERE chr_cliedni = ? AND chr_cliecodigo <> ?")) {
            ps.setString(1, cedula);
            ps.setString(2, excluirCodigo == null ? "" : excluirCodigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void llenar(PreparedStatement ps, Cliente c, int i) throws SQLException {
        ps.setString(i, c.getPaterno());
        ps.setString(i + 1, c.getMaterno());
        ps.setString(i + 2, c.getNombre());
        ps.setString(i + 3, c.getCedula());
        ps.setString(i + 4, c.getCiudad());
        ps.setString(i + 5, c.getDireccion());
        ps.setString(i + 6, c.getTelefono());
        ps.setString(i + 7, c.getEmail());
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setCodigo(rs.getString("chr_cliecodigo"));
        c.setPaterno(rs.getString("vch_cliepaterno"));
        c.setMaterno(rs.getString("vch_cliematerno"));
        c.setNombre(rs.getString("vch_clienombre"));
        c.setCedula(rs.getString("chr_cliedni"));
        c.setCiudad(rs.getString("vch_clieciudad"));
        c.setDireccion(rs.getString("vch_cliedireccion"));
        c.setTelefono(rs.getString("vch_clietelefono"));
        c.setEmail(rs.getString("vch_clieemail"));
        return c;
    }
}
