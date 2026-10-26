package ec.edu.monster.climov.controlador;

import ec.edu.monster.climov.modelo.Cuenta;
import ec.edu.monster.climov.modelo.Movimiento;
import ec.edu.monster.climov.modelo.ResultadoOperacion;
import ec.edu.monster.climov.modelo.ServicioRest;
import ec.edu.monster.climov.modelo.SesionApp;
import java.util.List;

/**
 * Controlador: casos de uso de la app (iniciar sesión, listar cuentas,
 * consultar movimientos, operar y cerrar sesión) que invocan las pantallas.
 * Cada llamada al servidor se ejecuta en segundo plano con Tareas.
 *
 * @author Grupo 1 - NRC 36841
 */
public class ControladorBanco {

    private final ServicioRest servicio = SesionApp.get().getServicio();

    public void iniciarSesion(String url, String usuario, String clave, Tareas.Exito<String> ok, Tareas.Fallo error) {
        servicio.setUrlBase(url);
        Tareas.ejecutar(() -> servicio.iniciarSesion(usuario, clave), ok, error);
    }

    public void listarCuentas(Tareas.Exito<List<Cuenta>> ok, Tareas.Fallo error) {
        Tareas.ejecutar(() -> {
            List<Cuenta> l = servicio.listarCuentas();
            SesionApp.get().setCuentas(l);
            return l;
        }, ok, error);
    }

    /** Devuelve {cuenta, movimientos}. */
    public void estadoDeCuenta(String cuenta, Tareas.Exito<Object[]> ok, Tareas.Fallo error) {
        Tareas.ejecutar(() -> new Object[]{servicio.consultarCuenta(cuenta), servicio.listarMovimientos(cuenta)}, ok, error);
    }

    public void operar(String tipo, String cuenta, String destino, double importe,
                       Tareas.Exito<ResultadoOperacion> ok, Tareas.Fallo error) {
        Tareas.ejecutar(() -> servicio.operar(tipo, cuenta, destino, importe), ok, error);
    }

    public void cerrarSesion(Tareas.Exito<Boolean> ok) {
        Tareas.ejecutar(() -> {
            servicio.cerrarSesion();
            return true;
        }, ok, e -> ok.listo(true));
    }

    public List<Cuenta> cuentas() {
        return SesionApp.get().getCuentas();
    }

    public String usuario() {
        return servicio.getNombreCompleto();
    }

    public String urlServidor() {
        return servicio.getUrlBase();
    }

    @SuppressWarnings("unchecked")
    public static List<Movimiento> movimientos(Object[] r) {
        return (List<Movimiento>) r[1];
    }
}
