package ec.edu.monster.climov.modelo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Consume la API REST del servidor WS_EUREKA_RESTFUL_JAVA (cliente MOVIL).
 * Los métodos son bloqueantes: se llaman desde un hilo secundario.
 *
 * @author Grupo 1 - NRC 36841
 */
public class ServicioRest {

    public static final String URL_POR_DEFECTO = "http://10.40.49.158:8080/WS_EUREKA_RESTFUL_JAVA/api";
    private static final String CLIENTE = "MOVIL";

    private String urlBase = URL_POR_DEFECTO;
    private String token;
    private String usuario;
    private String nombreCompleto;

    public void setUrlBase(String url) {
        String u = url == null || url.trim().isEmpty() ? URL_POR_DEFECTO : url.trim();
        if (!u.startsWith("http")) {
            u = "http://" + u;
        }
        urlBase = u.endsWith("/") ? u.substring(0, u.length() - 1) : u;
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

    public String iniciarSesion(String usuario, String clave) throws ExcepcionCliente {
        try {
            JSONObject cuerpo = new JSONObject();
            cuerpo.put("usuario", usuario);
            cuerpo.put("clave", clave);
            cuerpo.put("cliente", CLIENTE);
            JSONObject r = enviar("POST", "/auth/login", cuerpo.toString());
            JSONObject datos = r.getJSONObject("datos");
            token = datos.getString("token");
            this.usuario = datos.getString("usuario");
            this.nombreCompleto = datos.optString("nombreCompleto", this.usuario);
            return r.getString("mensaje");
        } catch (JSONException e) {
            throw new ExcepcionCliente("Respuesta inválida del servidor", 0);
        }
    }

    public void cerrarSesion() {
        try {
            if (token != null) {
                enviar("POST", "/auth/logout", "{}");
            }
        } catch (ExcepcionCliente e) {
            // la sesión ya no existía en el servidor
        } finally {
            token = null;
            usuario = null;
            nombreCompleto = null;
        }
    }

    public List<Cuenta> listarCuentas() throws ExcepcionCliente {
        try {
            JSONArray datos = enviar("GET", "/cuentas", null).getJSONArray("datos");
            List<Cuenta> lista = new ArrayList<>();
            for (int i = 0; i < datos.length(); i++) {
                lista.add(aCuenta(datos.getJSONObject(i)));
            }
            return lista;
        } catch (JSONException e) {
            throw new ExcepcionCliente("Respuesta inválida del servidor", 0);
        }
    }

    public Cuenta consultarCuenta(String cuenta) throws ExcepcionCliente {
        try {
            return aCuenta(enviar("GET", "/cuentas/" + cuenta, null).getJSONObject("datos"));
        } catch (JSONException e) {
            throw new ExcepcionCliente("Respuesta inválida del servidor", 0);
        }
    }

    public List<Movimiento> listarMovimientos(String cuenta) throws ExcepcionCliente {
        try {
            JSONArray datos = enviar("GET", "/cuentas/" + cuenta + "/movimientos", null).getJSONArray("datos");
            List<Movimiento> lista = new ArrayList<>();
            for (int i = 0; i < datos.length(); i++) {
                JSONObject o = datos.getJSONObject(i);
                Movimiento m = new Movimiento();
                m.numero = o.getInt("numero");
                m.fecha = o.optString("fecha");
                m.empleado = o.optString("empleado");
                m.tipo = o.optString("tipo");
                m.accion = o.optString("accion");
                m.importe = o.getDouble("importe");
                m.referencia = o.has("referencia") && !o.isNull("referencia") ? o.getString("referencia") : null;
                lista.add(m);
            }
            return lista;
        } catch (JSONException e) {
            throw new ExcepcionCliente("Respuesta inválida del servidor", 0);
        }
    }

    /** tipo: deposito, retiro o transferencia. destino solo para transferencias. */
    public ResultadoOperacion operar(String tipo, String cuenta, String destino, double importe) throws ExcepcionCliente {
        try {
            JSONObject cuerpo = new JSONObject();
            cuerpo.put("cuenta", cuenta);
            if (destino != null) {
                cuerpo.put("cuentaDestino", destino);
            }
            cuerpo.put("importe", importe);
            JSONObject r = enviar("POST", "/operaciones/" + tipo, cuerpo.toString());
            JSONObject d = r.getJSONObject("datos");
            ResultadoOperacion x = new ResultadoOperacion();
            x.operacion = d.getString("operacion");
            x.cuenta = d.getString("cuenta");
            x.cuentaDestino = d.has("cuentaDestino") && !d.isNull("cuentaDestino") ? d.getString("cuentaDestino") : null;
            x.importe = d.getDouble("importe");
            x.saldoAnterior = d.getDouble("saldoAnterior");
            x.saldoActual = d.getDouble("saldoActual");
            x.moneda = d.optString("moneda");
            x.numeroMovimiento = d.optInt("numeroMovimiento");
            x.empleado = d.optString("empleado");
            x.fecha = d.optString("fecha");
            x.mensaje = r.optString("mensaje");
            return x;
        } catch (JSONException e) {
            throw new ExcepcionCliente("Respuesta inválida del servidor", 0);
        }
    }

    private static Cuenta aCuenta(JSONObject o) throws JSONException {
        Cuenta c = new Cuenta();
        c.codigo = o.getString("codigo");
        c.codigoCliente = o.optString("codigoCliente");
        c.cliente = o.optString("cliente");
        c.moneda = o.optString("moneda");
        c.codigoMoneda = o.optString("codigoMoneda");
        c.sucursal = o.optString("sucursal");
        c.saldo = o.getDouble("saldo");
        c.estado = o.optString("estado");
        c.fechaCreacion = o.optString("fechaCreacion");
        c.totalMovimientos = o.optInt("totalMovimientos");
        return c;
    }

    private JSONObject enviar(String metodo, String ruta, String json) throws ExcepcionCliente {
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL(urlBase + ruta).openConnection();
            con.setRequestMethod(metodo);
            con.setConnectTimeout(6000);
            con.setReadTimeout(10000);
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("X-Cliente", CLIENTE);
            if (token != null) {
                con.setRequestProperty("Authorization", "Bearer " + token);
            }
            if (json != null) {
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                try (OutputStream os = con.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }
            }
            int codigo = con.getResponseCode();
            InputStream is = codigo >= 400 ? con.getErrorStream() : con.getInputStream();
            String cuerpo = leer(is);
            JSONObject r;
            try {
                r = new JSONObject(cuerpo);
            } catch (JSONException e) {
                throw new ExcepcionCliente("Respuesta inesperada del servidor (HTTP " + codigo + ")", codigo);
            }
            if (codigo == 401) {
                token = null;
            }
            if (!r.optBoolean("exito", false)) {
                throw new ExcepcionCliente(r.optString("mensaje", "Error en la solicitud"), codigo);
            }
            return r;
        } catch (IOException e) {
            throw new ExcepcionCliente("No se pudo conectar con el servidor. Verifique la URL y que el celular esté en la misma red.", 0);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private static String leer(InputStream is) throws IOException {
        if (is == null) {
            return "";
        }
        try (InputStream in = is; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) > 0) {
                out.write(b, 0, n);
            }
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
