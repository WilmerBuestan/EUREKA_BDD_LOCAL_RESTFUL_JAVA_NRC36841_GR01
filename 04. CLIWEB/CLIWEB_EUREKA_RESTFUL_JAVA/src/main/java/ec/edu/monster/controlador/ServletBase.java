package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.ServicioRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Base de los controladores web: cada usuario del navegador tiene su propio
 * ServicioRest (con su token) guardado en la sesión HTTP.
 *
 * @author Grupo 1 - NRC 36841
 */
public abstract class ServletBase extends HttpServlet {

    static final String SERVICIO = "servicioRest";

    /** ServicioRest de este navegador (se crea en la primera visita). */
    protected static ServicioRest servicio(HttpServletRequest req) {
        HttpSession s = req.getSession();
        ServicioRest r = (ServicioRest) s.getAttribute(SERVICIO);
        if (r == null) {
            r = new ServicioRest(req.getServletContext().getInitParameter("urlServidor"), "WEB");
            s.setAttribute(SERVICIO, r);
        }
        return r;
    }

    /** Devuelve el servicio con sesión o redirige al login (y devuelve null). */
    protected static ServicioRest protegido(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.setCharacterEncoding("UTF-8");
        ServicioRest s = servicio(req);
        if (!s.haySesion()) {
            res.sendRedirect("login");
            return null;
        }
        return s;
    }

    /** Si el token expiró vuelve al login; si no, deja el mensaje de error en la vista. */
    protected static boolean manejarError(ExcepcionCliente e, HttpServletRequest req, HttpServletResponse res) throws IOException {
        if (e.sesionExpirada()) {
            req.getSession().invalidate();
            req.getSession().setAttribute("aviso", "Su sesión expiró. Inicie sesión nuevamente.");
            res.sendRedirect("login");
            return true;
        }
        req.setAttribute("error", e.getMessage());
        return false;
    }

    /** Muestra la vista JSP con los datos comunes de la cabecera. */
    protected static void vista(HttpServletRequest req, HttpServletResponse res, String jsp, String menu)
            throws ServletException, IOException {
        ServicioRest s = servicio(req);
        req.setAttribute("menu", menu);
        req.setAttribute("usuarioSesion", s.getNombreCompleto());
        req.setAttribute("servidor", s.getUrlBase());
        HttpSession ses = req.getSession();
        Object aviso = ses.getAttribute("aviso");
        if (aviso != null && req.getAttribute("aviso") == null) {
            req.setAttribute("aviso", aviso);
            ses.removeAttribute("aviso");
        }
        req.getRequestDispatcher("/WEB-INF/vistas/" + jsp).forward(req, res);
    }

    protected static String param(HttpServletRequest req, String nombre) {
        String v = req.getParameter(nombre);
        return v == null ? "" : v.trim();
    }
}
