package ec.edu.monster.climov.vista;

import android.graphics.Color;

/**
 * Colores por tipo de operación (iguales en todos los clientes).
 *
 * @author Grupo 1 - NRC 36841
 */
public final class Colores {

    public static final int VERDE = Color.parseColor("#22C55E");
    public static final int ROJO = Color.parseColor("#F43F5E");
    public static final int AZUL = Color.parseColor("#3D8BFD");
    public static final int DORADO = Color.parseColor("#F5B841");
    public static final int TEXTO = Color.parseColor("#E8EEFB");
    public static final int TENUE = Color.parseColor("#8FA3C7");
    public static final int PANEL2 = Color.parseColor("#1A2B4B");
    public static final int OSCURO = Color.parseColor("#06101F");

    private Colores() {
    }

    /** tipo: deposito / retiro / transferencia (o en mayúsculas). */
    public static int de(String tipo) {
        switch (tipo.toLowerCase()) {
            case "deposito":
                return VERDE;
            case "retiro":
                return ROJO;
            default:
                return AZUL;
        }
    }
}
