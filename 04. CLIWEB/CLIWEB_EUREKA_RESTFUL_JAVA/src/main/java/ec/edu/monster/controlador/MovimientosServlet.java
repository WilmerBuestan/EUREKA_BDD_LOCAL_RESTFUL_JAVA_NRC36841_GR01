package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.ServicioRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Estado de cuenta: saldo y movimientos de la cuenta elegida.
 *
 * @author Grupo 1 - NRC 36841
 */
@WebServlet(name = "MovimientosServlet", urlPatterns = {"/movimientos"})
public class MovimientosServlet extends ServletBase {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ServicioRest s = protegido(req, res);
        if (s == null) {
            return;
        }
        String cuenta = param(req, "cuenta");
        try {
            req.setAttribute("cuentas", s.listarCuentas());
            if (!cuenta.isEmpty()) {
                req.setAttribute("cuenta", s.consultarCuenta(cuenta));
                req.setAttribute("movimientos", s.listarMovimientos(cuenta));
            }
        } catch (ExcepcionCliente e) {
            if (manejarError(e, req, res)) {
                return;
            }
        }
        req.setAttribute("cuentaSel", cuenta);
        vista(req, res, "movimientos.jsp", "movimientos");
    }
}
