package ec.edu.monster.controlador;

import ec.edu.monster.db.ConexionBD;
import ec.edu.monster.modelo.EstadoServidor;
import ec.edu.monster.modelo.RespuestaApi;
import ec.edu.monster.servicio.ServicioAutenticacion;
import ec.edu.monster.servicio.ServicioMonitoreo;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.Connection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Estado del servidor y de la base de datos para el panel de monitoreo
 * (no requiere token).
 *
 * @author Grupo 1 - NRC 36841
 */
@Path("estado")
@Produces(MediaType.APPLICATION_JSON)
public class EstadoControlador {

    @GET
    public Response consultarEstado() {
        ServicioMonitoreo m = ServicioMonitoreo.getInstancia();
        EstadoServidor e = new EstadoServidor();
        e.setEstado("ACTIVO");
        e.setBaseDatos(probarBaseDatos());
        e.setInicio(m.getInicio().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        e.setSegundosActivo(Duration.between(m.getInicio(), LocalDateTime.now()).getSeconds());
        e.setSesionesActivas(ServicioAutenticacion.getInstancia().contarSesionesActivas());
        e.setTotalOperaciones(m.getTotalOperaciones());
        e.setTotalPeticiones(m.getTotalPeticiones());
        e.setOperacionesPorTipo(m.operacionesPorTipo());
        e.setPeticionesPorCliente(m.peticionesPorCliente());
        e.setUltimosEventos(m.ultimosEventos(15));
        e.setUrlsRed(urlsRed());
        return Response.ok(RespuestaApi.ok("Servidor activo", e)).build();
    }

    /**
     * IP v4 de las tarjetas de red activas (Wi-Fi / Ethernet), sin las de
     * VirtualBox ni loopback. Sirven para escribir la URL en el celular,
     * porque la IP cambia según la red (casa, guardia, aula).
     */
    private static java.util.List<String> urlsRed() {
        java.util.List<String> urls = new java.util.ArrayList<>();
        try {
            for (java.net.NetworkInterface ni : java.util.Collections.list(java.net.NetworkInterface.getNetworkInterfaces())) {
                String nombre = (ni.getDisplayName() + " " + ni.getName()).toLowerCase();
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual() || nombre.contains("virtualbox")
                        || nombre.contains("vmware") || nombre.contains("hyper-v") || nombre.contains("vethernet")) {
                    continue;
                }
                for (java.net.InetAddress a : java.util.Collections.list(ni.getInetAddresses())) {
                    if (a instanceof java.net.Inet4Address && !a.isLinkLocalAddress()) {
                        urls.add("http://" + a.getHostAddress() + ":8080/WS_EUREKA_RESTFUL_JAVA/api");
                    }
                }
            }
        } catch (Exception ex) {
            // sin información de red
        }
        return urls;
    }

    /** Abre y cierra una conexión para saber si MariaDB responde. */
    private static String probarBaseDatos() {
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            return cn.isValid(2) ? "CONECTADA" : "SIN CONEXION";
        } catch (Exception ex) {
            return "SIN CONEXION";
        }
    }
}
