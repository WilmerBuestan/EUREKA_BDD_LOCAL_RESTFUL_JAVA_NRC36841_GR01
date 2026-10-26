package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Cliente;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.ServicioRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * CRUD de clientes: listar/buscar (GET), editar (GET ?codigo=),
 * guardar nuevo o existente y eliminar (POST accion=guardar|eliminar).
 *
 * @author Grupo 1 - NRC 36841
 */
@WebServlet(name = "ClientesServlet", urlPatterns = {"/clientes"})
public class ClientesServlet extends ServletBase {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ServicioRest s = protegido(req, res);
        if (s == null) {
            return;
        }
        String codigo = param(req, "codigo");
        if (!codigo.isEmpty()) {
            try {
                req.setAttribute("cliente", s.seleccionarCliente(codigo));
            } catch (ExcepcionCliente e) {
                if (manejarError(e, req, res)) {
                    return;
                }
            }
        }
        mostrar(req, res, s);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ServicioRest s = protegido(req, res);
        if (s == null) {
            return;
        }
        if ("abrirCuenta".equals(param(req, "accion"))) {
            abrirCuenta(req, res, s);
            return;
        }
        Cliente c = new Cliente();
        c.setCodigo(param(req, "codigo").isEmpty() ? null : param(req, "codigo"));
        c.setNombre(param(req, "nombre"));
        c.setPaterno(param(req, "paterno"));
        c.setMaterno(param(req, "materno"));
        c.setCedula(param(req, "cedula"));
        c.setCiudad(param(req, "ciudad"));
        c.setDireccion(param(req, "direccion"));
        c.setTelefono(param(req, "telefono"));
        c.setEmail(param(req, "email"));
        try {
            if ("eliminar".equals(param(req, "accion")) && c.getCodigo() != null) {
                req.getSession().setAttribute("aviso", s.eliminarCliente(c.getCodigo()));
                res.sendRedirect("clientes");
                return;
            }
            Cliente r = c.getCodigo() == null ? s.insertarCliente(c) : s.actualizarCliente(c);
            req.getSession().setAttribute("aviso", (c.getCodigo() == null ? "Cliente registrado con el código " : "Cliente actualizado: ")
                    + r.getCodigo());
            res.sendRedirect("clientes?codigo=" + r.getCodigo());
            return;
        } catch (ExcepcionCliente e) {
            if (manejarError(e, req, res)) {
                return;
            }
            req.setAttribute("cliente", c); // se conservan los datos escritos
        }
        mostrar(req, res, s);
    }

    /** Abre una cuenta nueva al cliente seleccionado (depósito inicial + clave). */
    private void abrirCuenta(HttpServletRequest req, HttpServletResponse res, ServicioRest s) throws ServletException, IOException {
        String codigo = param(req, "codigo");
        String imp = param(req, "importe").replace(',', '.');
        try {
            if (!imp.matches("\\d+(\\.\\d{1,2})?") || Double.parseDouble(imp) <= 0) {
                req.setAttribute("errorCuenta", "El depósito inicial debe ser mayor que cero con máximo 2 decimales");
            } else {
                Cuenta c = s.abrirCuenta(codigo, param(req, "moneda"), param(req, "sucursal"), Double.parseDouble(imp), param(req, "clave"));
                req.getSession().setAttribute("aviso", s.getUltimoMensaje() + ". Ya puede operar con la cuenta " + c.getCodigo() + ".");
                res.sendRedirect("clientes?codigo=" + codigo);
                return;
            }
        } catch (ExcepcionCliente e) {
            if (manejarError(e, req, res)) {
                return;
            }
            req.setAttribute("errorCuenta", req.getAttribute("error"));
            req.removeAttribute("error");
        }
        try {
            req.setAttribute("cliente", s.seleccionarCliente(codigo));
        } catch (ExcepcionCliente e) {
            if (manejarError(e, req, res)) {
                return;
            }
        }
        mostrar(req, res, s);
    }

    private void mostrar(HttpServletRequest req, HttpServletResponse res, ServicioRest s) throws ServletException, IOException {
        String buscar = param(req, "buscar");
        try {
            req.setAttribute("clientes", s.listarClientes(buscar));
        } catch (ExcepcionCliente e) {
            if (manejarError(e, req, res)) {
                return;
            }
        }
        req.setAttribute("buscar", buscar);
        Object cli = req.getAttribute("cliente");
        if (cli instanceof Cliente c && c.getCodigo() != null) {
            try {
                req.setAttribute("catalogos", s.listarCatalogos());
                java.util.List<Cuenta> propias = new java.util.ArrayList<>();
                for (Cuenta k : s.listarCuentas()) {
                    if (c.getCodigo().equals(k.getCodigoCliente())) {
                        propias.add(k);
                    }
                }
                req.setAttribute("cuentasCliente", propias);
            } catch (ExcepcionCliente e) {
                if (manejarError(e, req, res)) {
                    return;
                }
            }
        }
        vista(req, res, "clientes.jsp", "clientes");
    }
}
