package ec.edu.monster.prueba;

import ec.edu.monster.dao.ClienteDAO;
import ec.edu.monster.dao.CuentaDAO;
import ec.edu.monster.dao.MovimientoDAO;
import ec.edu.monster.dao.UsuarioDAO;
import ec.edu.monster.db.ConexionBD;
import ec.edu.monster.modelo.Cliente;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.util.UtilHash;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;

/**
 * Pruebas de conexión y de la capa de datos de EurekaBank.
 * Ejecutar en NetBeans: clic derecho sobre este archivo > Run File.
 *
 * <ol>
 * <li>Singleton: siempre la misma instancia de ConexionBD.</li>
 * <li>Conexión: se abre, se consulta y SIEMPRE se cierra en finally.</li>
 * <li>CRUD de cliente: insertar, seleccionar, listar, actualizar y eliminar
 * (cliente temporal que se borra al final; la BD queda igual).</li>
 * <li>Lecturas: usuario monster, cuentas y movimientos.</li>
 * </ol>
 *
 * @author Grupo 1 - NRC 36841
 */
public class PruebasConexion {

    private static int ok;
    private static int fallos;

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE CONEXION - EUREKABANK REST ===");

        // 1. Singleton
        ConexionBD a = ConexionBD.getInstancia();
        verificar("Singleton devuelve la misma instancia", a == ConexionBD.getInstancia());
        System.out.println("     URL: " + a.getUrl());

        // 2. Conexión: siempre se cierra
        Connection cn = null;
        try {
            cn = a.getConexion();
            DatabaseMetaData md = cn.getMetaData();
            verificar("Conexión abierta a " + md.getDatabaseProductName() + " " + md.getDatabaseProductVersion()
                    + " (BD " + cn.getCatalog() + ")", cn.isValid(2));
        } catch (Exception e) {
            verificar("Conexión a la BD: " + e.getMessage(), false);
        } finally {
            ConexionBD.cerrar(cn);
            try {
                verificar("Conexión cerrada en finally", cn == null || cn.isClosed());
            } catch (Exception e) {
                verificar("Conexión cerrada en finally", false);
            }
        }

        // 3. CRUD de cliente
        ClienteDAO dao = new ClienteDAO();
        String codigo = null;
        try {
            Cliente c = new Cliente();
            c.setPaterno("MONSTER");
            c.setMaterno("PRUEBA");
            c.setNombre("CLIENTE TEMPORAL");
            c.setCedula("1702233071");
            c.setCiudad("SANGOLQUI");
            c.setDireccion("ESPE - AV. GENERAL RUMIÑAHUI");
            c.setTelefono("0999999999");
            c.setEmail("monster@espe.edu.ec");
            codigo = dao.insertar(c).getCodigo();
            verificar("insertar -> código generado " + codigo, codigo != null);

            Cliente leido = dao.seleccionar(codigo);
            verificar("seleccionar -> " + (leido == null ? "null" : leido.getNombre() + " " + leido.getPaterno()), leido != null);

            List<Cliente> todos = dao.listar(null);
            verificar("listar -> " + todos.size() + " clientes", !todos.isEmpty());

            List<Cliente> filtrados = dao.listar("MONSTER");
            verificar("listar con filtro 'MONSTER' -> " + filtrados.size(), !filtrados.isEmpty());

            c.setCiudad("QUITO");
            boolean act = dao.actualizar(c) && "QUITO".equals(dao.seleccionar(codigo).getCiudad());
            verificar("actualizar -> ciudad QUITO", act);

            verificar("eliminar -> cliente " + codigo, dao.eliminar(codigo) && dao.seleccionar(codigo) == null);
            codigo = null;
        } catch (Exception e) {
            verificar("CRUD cliente: " + e.getMessage(), false);
        } finally {
            if (codigo != null) { // limpieza si algo falló a medias
                try {
                    dao.eliminar(codigo);
                } catch (Exception ignorada) {
                    // sin acción
                }
            }
        }

        // 4. Lecturas de usuario, cuentas y movimientos
        try {
            UsuarioDAO.Credencial cred = new UsuarioDAO().seleccionar("monster");
            verificar("usuario monster existe y su clave es monster69",
                    cred != null && UtilHash.iguales(cred.hashClave(), UtilHash.sha1("monster69")));

            List<Cuenta> cuentas = new CuentaDAO().listar();
            verificar("cuentas -> " + cuentas.size(), !cuentas.isEmpty());

            Cuenta c = new CuentaDAO().seleccionar("00100001");
            verificar("saldo 00100001 -> " + (c == null ? "no existe" : c.getSaldo() + " " + c.getMoneda()), c != null);

            int movs = new MovimientoDAO().listar("00100001").size();
            verificar("movimientos 00100001 -> " + movs, movs > 0);
        } catch (Exception e) {
            verificar("Lecturas: " + e.getMessage(), false);
        }

        System.out.println("---------------------------------------------");
        System.out.println("RESULTADO: " + ok + " OK, " + fallos + " fallos");
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            ok++;
            System.out.println("[OK]    " + descripcion);
        } else {
            fallos++;
            System.out.println("[FALLO] " + descripcion);
        }
    }
}
