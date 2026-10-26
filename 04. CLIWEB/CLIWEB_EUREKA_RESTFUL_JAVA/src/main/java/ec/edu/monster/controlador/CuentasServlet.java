package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.ServicioRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Pantalla de inicio: resumen y listado de cuentas.
 *
 * @author Grupo 1 - NRC 36841
 */
@WebServlet(name = "CuentasServlet", urlPatterns = {"/cuentas"})
public class CuentasServlet extends ServletBase {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ServicioRest s = protegido(req, res);
        if (s == null) {
            return;
        }
        try {
            List<Cuenta> cuentas = s.listarCuentas();
            double dolares = 0;
            int activas = 0;
            for (Cuenta c : cuentas) {
                if (c.isActiva()) {
                    activas++;
                    if (c.getMoneda() != null && c.getMoneda().startsWith("D")) {
                        dolares += c.getSaldo();
                    }
                }
            }
            req.setAttribute("cuentas", cuentas);
            req.setAttribute("activas", activas);
            req.setAttribute("totalDolares", dolares);
        } catch (ExcepcionCliente e) {
            if (manejarError(e, req, res)) {
                return;
            }
        }
        vista(req, res, "cuentas.jsp", "cuentas");
    }
}
