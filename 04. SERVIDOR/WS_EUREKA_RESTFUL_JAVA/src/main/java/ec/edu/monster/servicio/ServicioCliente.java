package ec.edu.monster.servicio;

import ec.edu.monster.dao.ClienteDAO;
import ec.edu.monster.modelo.Cliente;
import java.sql.SQLException;
import java.util.List;

/**
 * Reglas de negocio del CRUD de clientes. Patrón Singleton.
 *
 * @author Grupo 1 - NRC 36841
 */
public final class ServicioCliente {

    private static final ServicioCliente INSTANCIA = new ServicioCliente();
    private static final String LETRAS = "[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' .-]+";

    private final ClienteDAO dao = new ClienteDAO();

    private ServicioCliente() {
    }

    public static ServicioCliente getInstancia() {
        return INSTANCIA;
    }

    public List<Cliente> listar(String filtro) {
        try {
            return dao.listar(filtro);
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    public Cliente seleccionar(String codigo) {
        String c = validarCodigo(codigo);
        try {
            Cliente cli = dao.seleccionar(c);
            if (cli == null) {
                throw ExcepcionNegocio.noEncontrado("El cliente " + c + " no existe");
            }
            return cli;
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    public Cliente insertar(Cliente c) {
        normalizarYValidar(c);
        try {
            if (dao.existeCedula(c.getCedula(), null)) {
                throw ExcepcionNegocio.conflicto("Ya existe un cliente con la cédula " + c.getCedula());
            }
            return dao.insertar(c);
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    public Cliente actualizar(String codigo, Cliente c) {
        seleccionar(codigo); // valida que exista
        normalizarYValidar(c);
        c.setCodigo(codigo.trim());
        try {
            if (dao.existeCedula(c.getCedula(), c.getCodigo())) {
                throw ExcepcionNegocio.conflicto("Ya existe otro cliente con la cédula " + c.getCedula());
            }
            dao.actualizar(c);
            return c;
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    public Cliente eliminar(String codigo) {
        Cliente c = seleccionar(codigo);
        try {
            int cuentas = dao.contarCuentas(c.getCodigo());
            if (cuentas > 0) {
                throw ExcepcionNegocio.conflicto("No se puede eliminar: el cliente tiene " + cuentas + " cuenta(s) registrada(s)");
            }
            dao.eliminar(c.getCodigo());
            return c;
        } catch (SQLException e) {
            throw ExcepcionNegocio.deBaseDatos(e);
        }
    }

    private static String validarCodigo(String codigo) {
        if (codigo == null || !codigo.trim().matches("\\d{5}")) {
            throw ExcepcionNegocio.validacion("El código de cliente debe tener 5 dígitos, por ejemplo 00001");
        }
        return codigo.trim();
    }

    /** Limpia espacios, pasa nombres a mayúsculas y valida longitudes de la tabla. */
    private static void normalizarYValidar(Cliente c) {
        if (c == null) {
            throw ExcepcionNegocio.validacion("Envíe los datos del cliente");
        }
        c.setPaterno(texto(c.getPaterno(), "Apellido paterno", 25, true));
        c.setMaterno(texto(c.getMaterno(), "Apellido materno", 25, true));
        c.setNombre(texto(c.getNombre(), "Nombre", 30, true));
        c.setCiudad(texto(c.getCiudad(), "Ciudad", 30, true));
        c.setDireccion(texto(c.getDireccion(), "Dirección", 50, false));
        String cedula = c.getCedula() == null ? "" : c.getCedula().trim();
        if (!cedulaValida(cedula)) {
            throw ExcepcionNegocio.validacion("Cédula ecuatoriana inválida: debe tener 10 dígitos, provincia 01-24 y dígito verificador correcto");
        }
        c.setCedula(c.getCedula().trim());
        String tel = c.getTelefono() == null ? "" : c.getTelefono().trim();
        if (!tel.isEmpty() && !tel.matches("[0-9 +()-]{6,20}")) {
            throw ExcepcionNegocio.validacion("Teléfono inválido (6 a 20 dígitos)");
        }
        c.setTelefono(tel.isEmpty() ? null : tel);
        String mail = c.getEmail() == null ? "" : c.getEmail().trim().toLowerCase();
        if (!mail.isEmpty() && (mail.length() > 50 || !mail.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"))) {
            throw ExcepcionNegocio.validacion("Correo electrónico inválido");
        }
        c.setEmail(mail.isEmpty() ? null : mail);
    }

    /**
     * Valida una cédula ecuatoriana: 10 dígitos, provincia 01-24 (o 30),
     * tercer dígito menor que 6 y dígito verificador con el algoritmo módulo 10
     * (coeficientes 2,1,2,1,2,1,2,1,2; si el producto pasa de 9 se le resta 9).
     */
    public static boolean cedulaValida(String cedula) {
        if (cedula == null || !cedula.matches("\\d{10}")) {
            return false;
        }
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if ((provincia < 1 || provincia > 24) && provincia != 30) {
            return false;
        }
        if (cedula.charAt(2) - '0' >= 6) {
            return false;
        }
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int v = (cedula.charAt(i) - '0') * (i % 2 == 0 ? 2 : 1);
            suma += v > 9 ? v - 9 : v;
        }
        int verificador = (10 - suma % 10) % 10;
        return verificador == cedula.charAt(9) - '0';
    }

    private static String texto(String valor, String campo, int max, boolean soloLetras) {
        String v = valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
        if (v.isEmpty()) {
            throw ExcepcionNegocio.validacion(campo + " es obligatorio");
        }
        if (v.length() > max) {
            throw ExcepcionNegocio.validacion(campo + " admite máximo " + max + " caracteres");
        }
        if (soloLetras && !v.matches(LETRAS)) {
            throw ExcepcionNegocio.validacion(campo + " solo admite letras");
        }
        return v.toUpperCase();
    }
}
