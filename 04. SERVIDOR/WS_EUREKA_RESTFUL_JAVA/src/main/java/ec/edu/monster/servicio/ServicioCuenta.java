package ec.edu.monster.servicio;

import ec.edu.monster.dao.CuentaDAO;
import ec.edu.monster.dao.MovimientoDAO;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.Opcion;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.modelo.SolicitudApertura;
import ec.edu.monster.dao.ClienteDAO;
import ec.edu.monster.db.ConexionBD;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.sql.SQLException;
import java.util.List;

/**
 * Consultas de cuentas: listado, saldo y movimientos. Patrón Singleton.
 *
 * @author Grupo 1 - NRC 36841
 */
public final class ServicioCuenta {

    private static final ServicioCuenta INSTANCIA = new ServicioCuenta();

    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    /** Tipo de movimiento 001 - Apertura de Cuenta. */
    public static final String TIPO_APERTURA = "001";

    private ServicioCuenta() {
    }

    public static ServicioCuenta getInstancia() {
        return INSTANCIA;
    }

    public List<Cuenta> listarCuentas() {
        try {
            return cuentaDAO.listar();
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    /** Datos y saldo de la cuenta. */
    public Cuenta consultarCuenta(String codigo) {
        String c = validarCuenta(codigo);
        try {
            Cuenta cuenta = cuentaDAO.seleccionar(c);
            if (cuenta == null) {
                throw ExcepcionNegocio.noEncontrado("La cuenta " + c + " no existe");
            }
            return cuenta;
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    public List<Movimiento> listarMovimientos(String codigo) {
        Cuenta cuenta = consultarCuenta(codigo);
        try {
            return movimientoDAO.listar(cuenta.getCodigo());
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    /** Monedas y sucursales para abrir cuentas. */
    public Map<String, List<Opcion>> catalogos() {
        try {
            Map<String, List<Opcion>> m = new LinkedHashMap<>();
            m.put("monedas", cuentaDAO.listarCatalogo("moneda"));
            m.put("sucursales", cuentaDAO.listarCatalogo("sucursal"));
            return m;
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    /**
     * Abre una cuenta nueva: genera el número con el contador de la sucursal,
     * inserta la cuenta con el depósito inicial y registra el movimiento
     * 001 - Apertura de Cuenta, todo en una sola transacción.
     */
    public Cuenta abrirCuenta(SolicitudApertura s, Sesion sesion) {
        if (s == null) {
            throw ExcepcionNegocio.validacion("Envíe los datos de la cuenta");
        }
        String cliente = s.getCodigoCliente() == null ? "" : s.getCodigoCliente().trim();
        String moneda = s.getCodigoMoneda() == null ? "" : s.getCodigoMoneda().trim();
        String sucursal = s.getCodigoSucursal() == null ? "" : s.getCodigoSucursal().trim();
        String clave = s.getClave() == null ? "" : s.getClave().trim();
        if (!cliente.matches("\\d{5}")) {
            throw ExcepcionNegocio.validacion("Seleccione el cliente titular (código de 5 dígitos)");
        }
        if (!moneda.matches("\\d{2}")) {
            throw ExcepcionNegocio.validacion("Seleccione la moneda");
        }
        if (!sucursal.matches("\\d{3}")) {
            throw ExcepcionNegocio.validacion("Seleccione la sucursal");
        }
        if (!clave.matches("\\d{6}")) {
            throw ExcepcionNegocio.validacion("El PIN de la cuenta debe tener 6 dígitos");
        }
        BigDecimal importe = ServicioOperacion.validarImporte(s.getImporte());
        try {
            if (clienteDAO.seleccionar(cliente) == null) {
                throw ExcepcionNegocio.noEncontrado("El cliente " + cliente + " no existe");
            }
            if (catalogos().get("monedas").stream().noneMatch(o -> o.getCodigo().equals(moneda))) {
                throw ExcepcionNegocio.validacion("La moneda " + moneda + " no existe");
            }
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            cn.setAutoCommit(false);
            try {
                String codigo = cuentaDAO.siguienteCodigo(cn, sucursal);
                if (codigo == null) {
                    throw ExcepcionNegocio.validacion("La sucursal " + sucursal + " no existe");
                }
                cuentaDAO.insertar(cn, codigo, moneda, sucursal, sesion.getCodigoEmpleado(), cliente, importe, clave);
                movimientoDAO.insertar(cn, codigo, 1, sesion.getCodigoEmpleado(), TIPO_APERTURA, importe, null);
                cn.commit();
                return cuentaDAO.seleccionar(codigo);
            } catch (SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    /** La cuenta debe tener exactamente 8 dígitos (se conservan los ceros iniciales). */
    public static String validarCuenta(String codigo) {
        if (codigo == null || !codigo.trim().matches("\\d{8}")) {
            throw ExcepcionNegocio.validacion("Ingrese un número de cuenta de 8 dígitos, por ejemplo 00100001");
        }
        return codigo.trim();
    }
}
