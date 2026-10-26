package ec.edu.monster.controlador;

import ec.edu.monster.config.Seguro;
import ec.edu.monster.modelo.ResultadoOperacion;
import ec.edu.monster.modelo.SolicitudOperacion;
import ec.edu.monster.servicio.ServicioOperacion;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.function.Supplier;

/**
 * Recurso REST de operaciones: depósito, retiro y transferencia (requiere token).
 * El empleado que registra el movimiento es el usuario de la sesión.
 *
 * @author Grupo 1 - NRC 36841
 */
@Path("operaciones")
@Seguro
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OperacionControlador extends ControladorBase {

    private final ServicioOperacion servicio = ServicioOperacion.getInstancia();

    @POST
    @Path("deposito")
    public Response depositar(SolicitudOperacion s) {
        return operar("DEPOSITO", () -> servicio.depositar(s, sesion()));
    }

    @POST
    @Path("retiro")
    public Response retirar(SolicitudOperacion s) {
        return operar("RETIRO", () -> servicio.retirar(s, sesion()));
    }

    @POST
    @Path("transferencia")
    public Response transferir(SolicitudOperacion s) {
        return operar("TRANSFERENCIA", () -> servicio.transferir(s, sesion()));
    }

    private Response operar(String tipo, Supplier<ResultadoOperacion> accion) {
        return ejecutar(tipo, () -> {
            ResultadoOperacion r = accion.get();
            monitoreo.registrarOperacion(tipo);
            return r;
        }, OperacionControlador::mensaje);
    }

    private static String mensaje(ResultadoOperacion r) {
        String base = switch (r.getOperacion()) {
            case "DEPOSITO" -> String.format("Depósito de %.2f en la cuenta %s", r.getImporte(), r.getCuenta());
            case "RETIRO" -> String.format("Retiro de %.2f de la cuenta %s", r.getImporte(), r.getCuenta());
            default -> String.format("Transferencia de %.2f de %s a %s", r.getImporte(), r.getCuenta(), r.getCuentaDestino());
        };
        String fin = "TRANSFERENCIA".equals(r.getOperacion()) ? " realizada" : " realizado";
        return base + fin + String.format(". Nuevo saldo: %.2f %s", r.getSaldoActual(), r.getMoneda());
    }
}
