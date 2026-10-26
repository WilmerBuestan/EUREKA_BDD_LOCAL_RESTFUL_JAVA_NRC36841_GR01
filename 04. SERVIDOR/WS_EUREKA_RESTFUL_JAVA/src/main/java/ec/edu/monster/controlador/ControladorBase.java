package ec.edu.monster.controlador;

import ec.edu.monster.config.FiltroAutenticacion;
import ec.edu.monster.modelo.RespuestaApi;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.servicio.ExcepcionNegocio;
import ec.edu.monster.servicio.ServicioAutenticacion;
import ec.edu.monster.servicio.ServicioMonitoreo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base de los controladores: obtiene la sesión del token, ejecuta la acción
 * del servicio, registra el evento en el monitoreo y arma la respuesta JSON
 * estándar {exito, mensaje, datos}.
 *
 * @author Grupo 1 - NRC 36841
 */
public abstract class ControladorBase {

    @Context
    protected HttpHeaders headers;

    protected final ServicioMonitoreo monitoreo = ServicioMonitoreo.getInstancia();

    /** Sesión del token (el filtro @Seguro ya validó que existe). */
    protected Sesion sesion() {
        String token = FiltroAutenticacion.extraerToken(headers.getHeaderString(HttpHeaders.AUTHORIZATION));
        Sesion s = ServicioAutenticacion.getInstancia().validarToken(token);
        if (s == null) {
            throw new ExcepcionNegocio(401, "Sesión no válida o expirada. Inicie sesión nuevamente.");
        }
        return s;
    }

    /** Cliente que hace la petición: el de la sesión o el encabezado X-Cliente. */
    protected String cliente() {
        String token = FiltroAutenticacion.extraerToken(headers.getHeaderString(HttpHeaders.AUTHORIZATION));
        Sesion s = ServicioAutenticacion.getInstancia().validarToken(token);
        return s != null ? s.getCliente() : headers.getHeaderString("X-Cliente");
    }

    /**
     * Ejecuta la acción y responde 200 (o el estado indicado) con los datos;
     * si el servicio lanza ExcepcionNegocio responde con su código y mensaje.
     */
    protected <T> Response ejecutar(String evento, int estadoOk, Supplier<T> accion, Function<T, String> mensaje) {
        String cliente = cliente();
        try {
            T datos = accion.get();
            String m = mensaje.apply(datos);
            monitoreo.registrarEvento(evento, cliente, m, true);
            return Response.status(estadoOk).entity(RespuestaApi.ok(m, datos)).build();
        } catch (ExcepcionNegocio e) {
            monitoreo.registrarEvento(evento, cliente, e.getMessage(), false);
            return Response.status(e.getEstado()).entity(RespuestaApi.error(e.getMessage())).build();
        }
    }

    protected <T> Response ejecutar(String evento, Supplier<T> accion, Function<T, String> mensaje) {
        return ejecutar(evento, 200, accion, mensaje);
    }
}
