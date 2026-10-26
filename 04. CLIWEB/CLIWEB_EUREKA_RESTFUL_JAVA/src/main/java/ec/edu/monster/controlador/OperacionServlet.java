package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.ResultadoOperacion;
import ec.edu.monster.modelo.ServicioRest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Depósito, retiro y transferencia con comprobante.
 *
 * @author Grupo 1 - NRC 36841
 */
@WebServlet(name = "OperacionServlet", urlPatterns = {"/operaciones"})
public class OperacionServlet extends ServletBase {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ServicioRest s = protegido(req, res);
        if (s != null) {
            mostrar(req, res, s, tipo(req));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        ServicioRest s = protegido(req, res);
        if (s == null) {
            return;
        }
        String tipo = tipo(req);
        String cuenta = param(req, "cuenta");
        String destino = param(req, "destino");
        String importeTxt = param(req, "importe").replace(',', '.');
        req.setAttribute("cuentaSel", cuenta);
        req.setAttribute("destinoSel", destino);
        if (!importeTxt.matches("\\d+(\\.\\d{1,2})?") || Double.parseDouble(importeTxt) <= 0) {
            req.setAttribute("error", "Ingrese un importe mayor que cero con máximo 2 decimales (ej. 150.50)");
            req.setAttribute("importe", importeTxt);
        } else {
            double importe = Double.parseDouble(importeTxt);
            try {
                ResultadoOperacion r = switch (tipo) {
                    case "retiro" -> s.retirar(cuenta, importe);
                    case "transferencia" -> s.transferir(cuenta, destino, importe);
                    default -> s.depositar(cuenta, importe);
                };
                req.setAttribute("resultado", r);
                req.setAttribute("aviso", s.getUltimoMensaje());
            } catch (ExcepcionCliente e) {
                if (manejarError(e, req, res)) {
                    return;
                }
                req.setAttribute("importe", importeTxt);
            }
        }
        mostrar(req, res, s, tipo);
    }

    private void mostrar(HttpServletRequest req, HttpServletResponse res, ServicioRest s, String tipo)
            throws ServletException, IOException {
        try {
            req.setAttribute("cuentas", s.listarCuentas());
        } catch (ExcepcionCliente e) {
            if (manejarError(e, req, res)) {
                return;
            }
        }
        req.setAttribute("tipo", tipo);
        vista(req, res, "operaciones.jsp", "operaciones");
    }

    private static String tipo(HttpServletRequest req) {
        String t = param(req, "tipo").toLowerCase();
        return t.equals("retiro") || t.equals("transferencia") ? t : "deposito";
    }
}
