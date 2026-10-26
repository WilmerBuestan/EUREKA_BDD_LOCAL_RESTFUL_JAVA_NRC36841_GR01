package ec.edu.monster.controlador;

import ec.edu.monster.modelo.RespuestaApi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Raíz de la API (/api): presenta el servicio y sus endpoints.
 *
 * @author Grupo 1 - NRC 36841
 */
@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class InicioControlador {

    @GET
    public Response inicio() {
        Map<String, String> e = new LinkedHashMap<>();
        e.put("POST /api/auth/login", "Iniciar sesión {usuario, clave, cliente}");
        e.put("POST /api/auth/logout", "Cerrar sesión (token)");
        e.put("GET /api/cuentas", "Listar cuentas (token)");
        e.put("POST /api/cuentas", "Abrir cuenta {codigoCliente, codigoMoneda, codigoSucursal, importe, clave} (token)");
        e.put("GET /api/cuentas/catalogos", "Monedas y sucursales (token)");
        e.put("GET /api/cuentas/{cuenta}", "Consultar saldo (token)");
        e.put("GET /api/cuentas/{cuenta}/movimientos", "Movimientos de la cuenta (token)");
        e.put("POST /api/operaciones/deposito", "Depósito {cuenta, importe} (token)");
        e.put("POST /api/operaciones/retiro", "Retiro {cuenta, importe} (token)");
        e.put("POST /api/operaciones/transferencia", "Transferencia {cuenta, cuentaDestino, importe} (token)");
        e.put("GET /api/clientes?buscar=", "Listar / buscar clientes (token)");
        e.put("GET /api/clientes/{codigo}", "Seleccionar cliente (token)");
        e.put("POST /api/clientes", "Insertar cliente (token)");
        e.put("PUT /api/clientes/{codigo}", "Actualizar cliente (token)");
        e.put("DELETE /api/clientes/{codigo}", "Eliminar cliente (token)");
        e.put("GET /api/estado", "Estado del servidor y de la BD");
        return Response.ok(RespuestaApi.ok("Servicio RESTful EurekaBank Monster - Grupo 1", e)).build();
    }
}
