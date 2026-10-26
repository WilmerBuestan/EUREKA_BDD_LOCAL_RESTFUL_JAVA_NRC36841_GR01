package ec.edu.monster.servicio;

import ec.edu.monster.dao.CuentaDAO;
import ec.edu.monster.dao.MovimientoDAO;
import ec.edu.monster.db.ConexionBD;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.ResultadoOperacion;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.modelo.SolicitudOperacion;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Operaciones bancarias transaccionales: depósito, retiro y transferencia.
 *
 * <p>Cada operación usa UNA conexión con autoCommit=false: bloquea las cuentas
 * (SELECT ... FOR UPDATE), valida, actualiza saldos, inserta los movimientos y
 * hace commit. Ante cualquier error hace rollback, así el saldo nunca queda a
 * medias. En transferencias las cuentas se bloquean siempre en el mismo orden
 * para evitar bloqueos mutuos (deadlocks).</p>
 *
 * Tipos de movimiento: 003 Depósito, 004 Retiro, 009 Transferencia (salida),
 * 008 Transferencia (ingreso).
 *
 * @author Grupo 1 - NRC 36841
 */
public final class ServicioOperacion {

    public static final String TIPO_DEPOSITO = "003";
    public static final String TIPO_RETIRO = "004";
    public static final String TIPO_TRANSF_INGRESO = "008";
    public static final String TIPO_TRANSF_SALIDA = "009";
    public static final BigDecimal IMPORTE_MAXIMO = new BigDecimal("100000.00");

    private static final ServicioOperacion INSTANCIA = new ServicioOperacion();
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final MovimientoDAO movimientoDAO = new MovimientoDAO();

    private ServicioOperacion() {
    }

    public static ServicioOperacion getInstancia() {
        return INSTANCIA;
    }

    public ResultadoOperacion depositar(SolicitudOperacion s, Sesion sesion) {
        return operar("DEPOSITO", s, sesion);
    }

    public ResultadoOperacion retirar(SolicitudOperacion s, Sesion sesion) {
        return operar("RETIRO", s, sesion);
    }

    public ResultadoOperacion transferir(SolicitudOperacion s, Sesion sesion) {
        return operar("TRANSFERENCIA", s, sesion);
    }

    private ResultadoOperacion operar(String operacion, SolicitudOperacion s, Sesion sesion) {
        if (s == null) {
            throw ExcepcionNegocio.validacion("Envíe los datos de la operación");
        }
        boolean transferencia = "TRANSFERENCIA".equals(operacion);
        String origen = ServicioCuenta.validarCuenta(s.getCuenta());
        String destino = transferencia ? ServicioCuenta.validarCuenta(s.getCuentaDestino()) : null;
        if (transferencia && origen.equals(destino)) {
            throw ExcepcionNegocio.validacion("La cuenta destino debe ser diferente de la cuenta origen");
        }
        BigDecimal importe = validarImporte(s.getImporte());
        String empleado = sesion.getCodigoEmpleado();

        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            cn.setAutoCommit(false);
            try {
                // Bloqueo en orden estable (menor código primero) para evitar deadlocks
                Cuenta cOrigen;
                Cuenta cDestino = null;
                if (transferencia && destino.compareTo(origen) < 0) {
                    cDestino = bloquear(cn, destino);
                    cOrigen = bloquear(cn, origen);
                } else {
                    cOrigen = bloquear(cn, origen);
                    if (transferencia) {
                        cDestino = bloquear(cn, destino);
                    }
                }
                if (transferencia && !cOrigen.getCodigoMoneda().equals(cDestino.getCodigoMoneda())) {
                    throw ExcepcionNegocio.validacion("No se puede transferir entre cuentas de distinta moneda ("
                            + cOrigen.getMoneda() + " → " + cDestino.getMoneda() + ")");
                }

                BigDecimal saldoAnterior = BigDecimal.valueOf(cOrigen.getSaldo());
                boolean salida = !"DEPOSITO".equals(operacion);
                BigDecimal saldoNuevo = salida ? saldoAnterior.subtract(importe) : saldoAnterior.add(importe);
                if (saldoNuevo.signum() < 0) {
                    throw ExcepcionNegocio.conflicto("Saldo insuficiente. Saldo disponible: "
                            + saldoAnterior.setScale(2) + " " + cOrigen.getMoneda());
                }
                int numero = cOrigen.getTotalMovimientos() + 1;
                cuentaDAO.actualizarSaldo(cn, origen, saldoNuevo, numero);
                String tipo = switch (operacion) {
                    case "DEPOSITO" -> TIPO_DEPOSITO;
                    case "RETIRO" -> TIPO_RETIRO;
                    default -> TIPO_TRANSF_SALIDA;
                };
                movimientoDAO.insertar(cn, origen, numero, empleado, tipo, importe, destino);

                if (transferencia) {
                    int numDestino = cDestino.getTotalMovimientos() + 1;
                    BigDecimal saldoDestino = BigDecimal.valueOf(cDestino.getSaldo()).add(importe);
                    cuentaDAO.actualizarSaldo(cn, destino, saldoDestino, numDestino);
                    movimientoDAO.insertar(cn, destino, numDestino, empleado, TIPO_TRANSF_INGRESO, importe, origen);
                }
                cn.commit();

                ResultadoOperacion r = new ResultadoOperacion();
                r.setOperacion(operacion);
                r.setCuenta(origen);
                r.setCuentaDestino(destino);
                r.setImporte(importe.doubleValue());
                r.setSaldoAnterior(saldoAnterior.doubleValue());
                r.setSaldoActual(saldoNuevo.doubleValue());
                r.setMoneda(cOrigen.getMoneda());
                r.setNumeroMovimiento(numero);
                r.setEmpleado(empleado);
                r.setFecha(LocalDateTime.now().format(FECHA));
                return r;
            } catch (SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    private Cuenta bloquear(Connection cn, String codigo) throws SQLException {
        Cuenta c = cuentaDAO.seleccionar(cn, codigo, true);
        if (c == null) {
            throw ExcepcionNegocio.noEncontrado("La cuenta " + codigo + " no existe");
        }
        if (!"ACTIVO".equalsIgnoreCase(c.getEstado())) {
            throw ExcepcionNegocio.conflicto("La cuenta " + codigo + " no está activa (" + c.getEstado() + ")");
        }
        return c;
    }

    /** Importe positivo, finito, con máximo 2 decimales y hasta IMPORTE_MAXIMO. */
    public static BigDecimal validarImporte(double valor) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw ExcepcionNegocio.validacion("El importe debe ser un número mayor que cero");
        }
        BigDecimal b = BigDecimal.valueOf(valor);
        if (b.stripTrailingZeros().scale() > 2) {
            throw ExcepcionNegocio.validacion("El importe admite máximo dos decimales");
        }
        if (b.compareTo(IMPORTE_MAXIMO) > 0) {
            throw ExcepcionNegocio.validacion("El importe máximo por operación es " + IMPORTE_MAXIMO);
        }
        return b.setScale(2);
    }
}
