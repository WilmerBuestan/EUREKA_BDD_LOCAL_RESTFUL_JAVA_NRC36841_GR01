package ec.edu.monster.servicio;

import ec.edu.monster.dao.UsuarioDAO;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.util.UtilHash;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Seguridad: valida las credenciales contra la tabla usuario de la BD
 * (clave en SHA-1), emite tokens con expiración y administra las sesiones.
 * Patrón Singleton.
 *
 * @author Grupo 1 - NRC 36841
 */
public final class ServicioAutenticacion {

    public static final int MINUTOS_SESION = 30;
    private static final ServicioAutenticacion INSTANCIA = new ServicioAutenticacion();

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Map<String, Sesion> sesiones = new ConcurrentHashMap<>();
    private final SecureRandom aleatorio = new SecureRandom();

    private ServicioAutenticacion() {
    }

    public static ServicioAutenticacion getInstancia() {
        return INSTANCIA;
    }

    /**
     * @return la sesión creada
     * @throws ExcepcionNegocio 401 si las credenciales son incorrectas,
     *                          403 si el usuario está inactivo, 503 sin BD
     */
    public Sesion iniciarSesion(String usuario, String clave, String cliente) {
        String u = usuario == null ? "" : usuario.trim();
        UsuarioDAO.Credencial cred;
        try {
            cred = usuarioDAO.seleccionar(u);
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
        if (cred == null || clave == null || !UtilHash.iguales(cred.hashClave(), UtilHash.sha1(clave))) {
            throw new ExcepcionNegocio(401, "Usuario o clave incorrectos");
        }
        if (!"ACTIVO".equalsIgnoreCase(cred.usuario().getEstado())) {
            throw new ExcepcionNegocio(403, "El usuario está inactivo. Consulte con el administrador.");
        }
        byte[] bytes = new byte[32];
        aleatorio.nextBytes(bytes);
        String token = HexFormat.of().formatHex(bytes);
        LocalDateTime ahora = LocalDateTime.now();
        Sesion s = new Sesion(token, cred.usuario(), normalizarCliente(cliente), ahora, ahora.plusMinutes(MINUTOS_SESION));
        sesiones.put(token, s);
        return s;
    }

    /** @return la sesión vigente del token o null si no existe o expiró */
    public Sesion validarToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        Sesion s = sesiones.get(token.trim());
        if (s == null) {
            return null;
        }
        if (!s.estaVigente()) {
            sesiones.remove(s.getToken());
            return null;
        }
        return s;
    }

    public boolean cerrarSesion(String token) {
        return token != null && sesiones.remove(token.trim()) != null;
    }

    public int contarSesionesActivas() {
        sesiones.values().removeIf(s -> !s.estaVigente());
        return sesiones.size();
    }

    public static String normalizarCliente(String cliente) {
        if (cliente == null || cliente.isBlank()) {
            return "DESCONOCIDO";
        }
        return cliente.trim().toUpperCase();
    }
}
