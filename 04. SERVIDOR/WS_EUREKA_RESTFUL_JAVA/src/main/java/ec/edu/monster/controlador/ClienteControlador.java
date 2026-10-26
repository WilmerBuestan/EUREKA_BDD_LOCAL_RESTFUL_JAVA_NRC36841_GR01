package ec.edu.monster.controlador;

import ec.edu.monster.config.Seguro;
import ec.edu.monster.modelo.Cliente;
import ec.edu.monster.servicio.ServicioCliente;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Recurso REST de clientes: CRUD completo con los verbos HTTP
 * (GET listar/seleccionar, POST insertar, PUT actualizar, DELETE eliminar).
 *
 * @author Grupo 1 - NRC 36841
 */
@Path("clientes")
@Seguro
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteControlador extends ControladorBase {

    private final ServicioCliente servicio = ServicioCliente.getInstancia();

    @GET
    public Response listar(@QueryParam("buscar") String buscar) {
        return ejecutar("CLIENTES", () -> servicio.listar(buscar), l -> l.size() + " cliente(s) encontrados");
    }

    @GET
    @Path("{codigo}")
    public Response seleccionar(@PathParam("codigo") String codigo) {
        return ejecutar("CLIENTES", () -> servicio.seleccionar(codigo), c -> "Cliente " + c.getCodigo());
    }

    @POST
    public Response insertar(Cliente c) {
        return ejecutar("CLIENTE NUEVO", 201, () -> servicio.insertar(c),
                x -> "Cliente " + x.getCodigo() + " registrado: " + x.getNombre() + " " + x.getPaterno());
    }

    @PUT
    @Path("{codigo}")
    public Response actualizar(@PathParam("codigo") String codigo, Cliente c) {
        return ejecutar("CLIENTE EDITADO", () -> servicio.actualizar(codigo, c), x -> "Cliente " + x.getCodigo() + " actualizado");
    }

    @DELETE
    @Path("{codigo}")
    @Consumes(MediaType.WILDCARD)
    public Response eliminar(@PathParam("codigo") String codigo) {
        return ejecutar("CLIENTE ELIMINADO", () -> servicio.eliminar(codigo), x -> "Cliente " + x.getCodigo() + " eliminado");
    }
}
