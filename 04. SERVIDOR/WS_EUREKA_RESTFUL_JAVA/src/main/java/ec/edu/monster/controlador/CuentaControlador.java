package ec.edu.monster.controlador;

import ec.edu.monster.config.Seguro;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.SolicitudApertura;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import ec.edu.monster.servicio.ServicioCuenta;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Recurso REST de cuentas: listado, saldo y movimientos (requiere token).
 *
 * @author Grupo 1 - NRC 36841
 */
@Path("cuentas")
@Seguro
@Produces(MediaType.APPLICATION_JSON)
public class CuentaControlador extends ControladorBase {

    private final ServicioCuenta servicio = ServicioCuenta.getInstancia();

    @GET
    public Response listar() {
        return ejecutar("CUENTAS", servicio::listarCuentas, l -> l.size() + " cuenta(s) registradas");
    }

    /** Abre una cuenta nueva para un cliente (depósito inicial + movimiento 001). */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response abrirCuenta(SolicitudApertura s) {
        return ejecutar("CUENTA NUEVA", 201, () -> servicio.abrirCuenta(s, sesion()),
                (Cuenta c) -> String.format("Cuenta %s abierta para %s con %.2f %s", c.getCodigo(), c.getCliente(), c.getSaldo(), c.getMoneda()));
    }

    /** Monedas y sucursales para el formulario de apertura. */
    @GET
    @Path("catalogos")
    public Response catalogos() {
        return ejecutar("CATALOGOS", servicio::catalogos, m -> "Monedas y sucursales");
    }

    @GET
    @Path("{codigo}")
    public Response consultarSaldo(@PathParam("codigo") String codigo) {
        return ejecutar("SALDO", () -> servicio.consultarCuenta(codigo),
                (Cuenta c) -> String.format("Saldo de la cuenta %s: %.2f %s", c.getCodigo(), c.getSaldo(), c.getMoneda()));
    }

    @GET
    @Path("{codigo}/movimientos")
    public Response listarMovimientos(@PathParam("codigo") String codigo) {
        return ejecutar("MOVIMIENTOS", () -> servicio.listarMovimientos(codigo),
                l -> l.isEmpty() ? "La cuenta " + codigo + " no tiene movimientos" : l.size() + " movimiento(s) de la cuenta " + codigo);
    }
}
