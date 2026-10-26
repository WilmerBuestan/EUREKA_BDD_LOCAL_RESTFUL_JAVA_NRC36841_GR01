package ec.edu.monster.controlador;

import ec.edu.monster.config.FiltroAutenticacion;
import ec.edu.monster.config.Seguro;
import ec.edu.monster.modelo.RespuestaApi;
import ec.edu.monster.modelo.RespuestaLogin;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.modelo.SolicitudLogin;
import ec.edu.monster.servicio.ExcepcionNegocio;
import ec.edu.monster.servicio.ServicioAutenticacion;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.format.DateTimeFormatter;

/**
 * Recurso REST de seguridad: inicio y cierre de sesión (usuarios de la BD).
 *
 * @author Grupo 1 - NRC 36841
 */
@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AutenticacionControlador extends ControladorBase {

    private final ServicioAutenticacion autenticacion = ServicioAutenticacion.getInstancia();

    @POST
    @Path("login")
    public Response iniciarSesion(SolicitudLogin s) {
        String cliente = s == null ? null : s.getCliente();
        if (s == null || vacio(s.getUsuario()) || vacio(s.getClave())) {
            monitoreo.registrarEvento("LOGIN", cliente, "Campos vacíos", false);
            return Response.status(400).entity(RespuestaApi.error("Ingrese usuario y clave")).build();
        }
        try {
            Sesion sesion = autenticacion.iniciarSesion(s.getUsuario(), s.getClave(), cliente);
            RespuestaLogin r = new RespuestaLogin();
            r.setToken(sesion.getToken());
            r.setUsuario(sesion.getUsuario().getUsuario());
            r.setNombreCompleto(sesion.getUsuario().getNombreCompleto());
            r.setCodigoEmpleado(sesion.getCodigoEmpleado());
            r.setExpira(sesion.getExpira().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            r.setMinutosVigencia(ServicioAutenticacion.MINUTOS_SESION);
            monitoreo.registrarEvento("LOGIN", sesion.getCliente(), "Inicio de sesión de " + r.getUsuario(), true);
            return Response.ok(RespuestaApi.ok("Bienvenido a EurekaBank, " + r.getNombreCompleto(), r)).build();
        } catch (ExcepcionNegocio e) {
            monitoreo.registrarEvento("LOGIN", cliente, e.getMessage() + " ('" + s.getUsuario() + "')", false);
            return Response.status(e.getEstado()).entity(RespuestaApi.error(e.getMessage())).build();
        }
    }

    @POST
    @Path("logout")
    @Seguro
    @Consumes(MediaType.WILDCARD)
    public Response cerrarSesion() {
        String token = FiltroAutenticacion.extraerToken(headers.getHeaderString(HttpHeaders.AUTHORIZATION));
        Sesion sesion = autenticacion.validarToken(token);
        autenticacion.cerrarSesion(token);
        monitoreo.registrarEvento("LOGOUT", sesion == null ? null : sesion.getCliente(), "Cierre de sesión", true);
        return Response.ok(RespuestaApi.ok("Sesión cerrada correctamente", null)).build();
    }

    private static boolean vacio(String t) {
        return t == null || t.isBlank();
    }
}
