package ec.edu.monster.climov.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Datos compartidos entre pantallas mientras la app está abierta (Singleton).
 *
 * @author Grupo 1 - NRC 36841
 */
public final class SesionApp {

    private static final SesionApp INSTANCIA = new SesionApp();
    private final ServicioRest servicio = new ServicioRest();
    private List<Cuenta> cuentas = new ArrayList<>();

    private SesionApp() {
    }

    public static SesionApp get() {
        return INSTANCIA;
    }

    public ServicioRest getServicio() {
        return servicio;
    }

    public List<Cuenta> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<Cuenta> cuentas) {
        this.cuentas = cuentas;
    }
}
