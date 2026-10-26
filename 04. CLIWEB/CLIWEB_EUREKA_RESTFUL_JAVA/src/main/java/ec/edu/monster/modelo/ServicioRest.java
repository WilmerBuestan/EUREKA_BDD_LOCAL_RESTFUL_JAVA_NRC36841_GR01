package ec.edu.monster.modelo;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Consume la API REST del servidor WS_EUREKA_RESTFUL_JAVA.
 * Guarda el token Bearer de la sesión y lo envía en cada petición.
 *
 * @author Grupo 1 - NRC 36841
 */
public class ServicioRest {

    public static final String URL_POR_DEFECTO = "http://localhost:8080/WS_EUREKA_RESTFUL_JAVA/api";

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final String cliente;
    private String urlBase;
    private String token;
    private String usuario;
    private String nombreCompleto;

    public ServicioRest(String urlBase, String cliente) {
        setUrlBase(urlBase);
        this.cliente = cliente;
    }

    public final void setUrlBase(String url) {
        String u = url == null || url.isBlank() ? URL_POR_DEFECTO : url.trim();
        if (!u.startsWith("http")) {
            u = "http://" + u;
        }
        this.urlBase = u.endsWith("/") ? u.substring(0, u.length() - 1) : u;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public boolean haySesion() {
        return token != null;
    }

    // ------------------------------------------------------------ sesión

    public String iniciarSesion(String usuario, String clave) throws ExcepcionCliente {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("usuario", usuario);
        cuerpo.put("clave", clave);
        cuerpo.put("cliente", cliente);
        Map<String, Object> r = enviar("POST", "/auth/login", Json.escribir(cuerpo));
        Map<?, ?> d = (Map<?, ?>) r.get("datos");
        this.token = (String) d.get("token");
        this.usuario = (String) d.get("usuario");
        this.nombreCompleto = (String) d.get("nombreCompleto");
        return (String) r.get("mensaje");
    }

    public void cerrarSesion() {
        try {
            if (token != null) {
                enviar("POST", "/auth/logout", "{}");
            }
        } catch (ExcepcionCliente e) {
            // la sesión ya no existía en el servidor; igual se limpia localmente
        } finally {
            token = null;
            usuario = null;
            nombreCompleto = null;
        }
    }

    // ------------------------------------------------------------ cuentas

    public List<Cuenta> listarCuentas() throws ExcepcionCliente {
        List<Cuenta> lista = new ArrayList<>();
        for (Object o : lista(enviar("GET", "/cuentas", null))) {
            lista.add(Cuenta.desdeMapa((Map<?, ?>) o));
        }
        return lista;
    }

    public Cuenta consultarCuenta(String cuenta) throws ExcepcionCliente {
        return Cuenta.desdeMapa(datos(enviar("GET", "/cuentas/" + cod(cuenta), null)));
    }

    public List<Movimiento> listarMovimientos(String cuenta) throws ExcepcionCliente {
        List<Movimiento> lista = new ArrayList<>();
        for (Object o : lista(enviar("GET", "/cuentas/" + cod(cuenta) + "/movimientos", null))) {
            lista.add(Movimiento.desdeMapa((Map<?, ?>) o));
        }
        return lista;
    }

    /** Monedas y sucursales: {"monedas": [...], "sucursales": [...]} */
    public Map<String, List<Opcion>> listarCatalogos() throws ExcepcionCliente {
        Map<?, ?> d = datos(enviar("GET", "/cuentas/catalogos", null));
        Map<String, List<Opcion>> r = new LinkedHashMap<>();
        for (String k : new String[]{"monedas", "sucursales"}) {
            List<Opcion> l = new ArrayList<>();
            Object v = d.get(k);
            if (v instanceof List<?> lista) {
                for (Object o : lista) {
                    Map<?, ?> m = (Map<?, ?>) o;
                    l.add(new Opcion(String.valueOf(m.get("codigo")), String.valueOf(m.get("nombre"))));
                }
            }
            r.put(k, l);
        }
        return r;
    }

    /** Abre una cuenta nueva al cliente con un depósito inicial. */
    public Cuenta abrirCuenta(String cliente, String moneda, String sucursal, double importe, String clave) throws ExcepcionCliente {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("codigoCliente", cliente);
        cuerpo.put("codigoMoneda", moneda);
        cuerpo.put("codigoSucursal", sucursal);
        cuerpo.put("importe", importe);
        cuerpo.put("clave", clave);
        Map<String, Object> r = enviar("POST", "/cuentas", Json.escribir(cuerpo));
        ultimoMensaje = (String) r.get("mensaje");
        return Cuenta.desdeMapa(datos(r));
    }

    // ------------------------------------------------------------ operaciones

    /** Depósito en la cuenta; el mensaje del servidor queda en getUltimoMensaje(). */
    public ResultadoOperacion depositar(String cuenta, double importe) throws ExcepcionCliente {
        return operar("deposito", cuenta, null, importe);
    }

    public ResultadoOperacion retirar(String cuenta, double importe) throws ExcepcionCliente {
        return operar("retiro", cuenta, null, importe);
    }

    public ResultadoOperacion transferir(String origen, String destino, double importe) throws ExcepcionCliente {
        return operar("transferencia", origen, destino, importe);
    }

    private ResultadoOperacion operar(String tipo, String cuenta, String destino, double importe) throws ExcepcionCliente {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("cuenta", cuenta);
        if (destino != null) {
            cuerpo.put("cuentaDestino", destino);
        }
        cuerpo.put("importe", importe);
        Map<String, Object> r = enviar("POST", "/operaciones/" + tipo, Json.escribir(cuerpo));
        ultimoMensaje = (String) r.get("mensaje");
        return ResultadoOperacion.desdeMapa(datos(r));
    }

    private String ultimoMensaje;

    /** Mensaje que devolvió el servidor en la última operación. */
    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    // ------------------------------------------------------------ clientes (CRUD)

    public List<Cliente> listarClientes(String buscar) throws ExcepcionCliente {
        String q = buscar == null || buscar.isBlank() ? "" : "?buscar=" + URLEncoder.encode(buscar.trim(), StandardCharsets.UTF_8);
        List<Cliente> lista = new ArrayList<>();
        for (Object o : lista(enviar("GET", "/clientes" + q, null))) {
            lista.add(Cliente.desdeMapa((Map<?, ?>) o));
        }
        return lista;
    }

    public Cliente seleccionarCliente(String codigo) throws ExcepcionCliente {
        return Cliente.desdeMapa(datos(enviar("GET", "/clientes/" + cod(codigo), null)));
    }

    public Cliente insertarCliente(Cliente c) throws ExcepcionCliente {
        return Cliente.desdeMapa(datos(enviar("POST", "/clientes", Json.escribir(c.aMapa()))));
    }

    public Cliente actualizarCliente(Cliente c) throws ExcepcionCliente {
        return Cliente.desdeMapa(datos(enviar("PUT", "/clientes/" + cod(c.getCodigo()), Json.escribir(c.aMapa()))));
    }

    public String eliminarCliente(String codigo) throws ExcepcionCliente {
        return (String) enviar("DELETE", "/clientes/" + cod(codigo), null).get("mensaje");
    }

    // ------------------------------------------------------------ HTTP

    private static String cod(String t) {
        return URLEncoder.encode(t == null ? "" : t.trim(), StandardCharsets.UTF_8);
    }

    private static Map<?, ?> datos(Map<String, Object> r) {
        return (Map<?, ?>) r.get("datos");
    }

    private static List<?> lista(Map<String, Object> r) {
        Object d = r.get("datos");
        return d instanceof List<?> l ? l : List.of();
    }

    private Map<String, Object> enviar(String metodo, String ruta, String json) throws ExcepcionCliente {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(urlBase + ruta))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .header("X-Cliente", cliente);
        if (token != null) {
            b.header("Authorization", "Bearer " + token);
        }
        if (json != null) {
            b.header("Content-Type", "application/json; charset=UTF-8");
            b.method(metodo, HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));
        } else {
            b.method(metodo, metodo.equals("POST") ? HttpRequest.BodyPublishers.ofString("") : HttpRequest.BodyPublishers.noBody());
        }
        HttpResponse<String> res;
        try {
            res = http.send(b.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new ExcepcionCliente("No se pudo conectar con el servidor (" + urlBase + ")", 0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExcepcionCliente("Operación interrumpida", 0);
        }
        Map<String, Object> r;
        try {
            r = Json.objeto(res.body());
        } catch (RuntimeException e) {
            throw new ExcepcionCliente("Respuesta inesperada del servidor (HTTP " + res.statusCode() + ")", res.statusCode());
        }
        if (res.statusCode() == 401 && !ruta.startsWith("/auth/login")) {
            token = null;
        }
        if (!Boolean.TRUE.equals(r.get("exito"))) {
            throw new ExcepcionCliente(String.valueOf(r.get("mensaje")), res.statusCode());
        }
        return r;
    }
}
