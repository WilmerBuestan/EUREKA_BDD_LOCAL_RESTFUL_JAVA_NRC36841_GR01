package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.ServicioRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controlador del inicio de sesión del cliente web.
 *
 * @author Grupo 1 - NRC 36841
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends ServletBase {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (servicio(req).haySesion()) {
            res.sendRedirect("cuentas");
            return;
        }
        vista(req, res, "login.jsp", null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String usuario = param(req, "usuario");
        String clave = param(req, "clave");
        ServicioRest servicio = servicio(req);
        String servidor = param(req, "servidor");
        if (!servidor.isEmpty()) {
            servicio.setUrlBase(servidor);
        }
        req.setAttribute("usuario", usuario);
        if (usuario.isEmpty() || clave.isEmpty()) {
            req.setAttribute("error", "Ingrese usuario y clave");
            vista(req, res, "login.jsp", null);
            return;
        }
        try {
            req.getSession().setAttribute("aviso", servicio.iniciarSesion(usuario, clave));
            res.sendRedirect("cuentas");
        } catch (ExcepcionCliente e) {
            req.setAttribute("error", e.getMessage());
            vista(req, res, "login.jsp", null);
        }
    }
}
