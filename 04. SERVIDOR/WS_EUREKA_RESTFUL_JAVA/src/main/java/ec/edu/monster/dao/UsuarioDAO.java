package ec.edu.monster.dao;

import ec.edu.monster.db.ConexionBD;
import ec.edu.monster.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Acceso a datos de la tabla usuario (empleados que usan el sistema).
 *
 * @author Grupo 1 - NRC 36841
 */
public class UsuarioDAO {

    /** Usuario encontrado junto con el hash SHA-1 de su clave (el hash no sale del servidor). */
    public record Credencial(Usuario usuario, String hashClave) {
    }

    private final ConexionBD bd = ConexionBD.getInstancia();

    /**
     * SELECT del usuario con su empleado y el hash de su clave.
     *
     * @return la credencial o null si el usuario no existe
     */
    public Credencial seleccionar(String usuario) throws SQLException {
        String sql = "SELECT u.chr_emplcodigo, u.vch_emplusuario, u.vch_emplclave, u.vch_emplestado, "
                + "CONCAT(e.vch_emplnombre, ' ', e.vch_emplpaterno) "
                + "FROM usuario u JOIN empleado e ON e.chr_emplcodigo = u.chr_emplcodigo "
                + "WHERE u.vch_emplusuario = ?";
        try (Connection cn = bd.getConexion(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Usuario u = new Usuario();
                u.setCodigoEmpleado(rs.getString(1));
                u.setUsuario(rs.getString(2));
                u.setEstado(rs.getString(4));
                u.setNombreCompleto(rs.getString(5));
                return new Credencial(u, rs.getString(3));
            }
        }
    }
}
