package ec.edu.monster.vista;

import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.ResultadoOperacion;
import java.io.Console;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * Vista de texto del cliente de consola: menús, lectura de datos y mensajes
 * con colores ANSI y pequeñas animaciones.
 *
 * @author Grupo 1 - NRC 36841
 */
public class VistaConsola {

    private static final String R = "\u001B[0m", N = "\u001B[1m", AZUL = "\u001B[94m", CIAN = "\u001B[96m",
            VERDE = "\u001B[92m", ROJO = "\u001B[91m", AMARILLO = "\u001B[93m", GRIS = "\u001B[90m";

    private final Scanner entrada = new Scanner(System.in, StandardCharsets.UTF_8);
    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private final boolean color;

    public VistaConsola(boolean color) {
        this.color = color;
    }

    private String c(String codigo, String texto) {
        return color ? codigo + texto + R : texto;
    }

    public void limpiar() {
        if (color) {
            out.print("\u001B[H\u001B[2J");
        }
        out.println();
    }

    public void banner() {
        String[] arte = {
            " ___  _   _  ___  ___  _  __   _     ___    _    _  _  _  __",
            "| __|| | | || _ \\| __|| |/ /  /_\\   | _ )  /_\\  | \\| || |/ /",
            "| _| | |_| ||   /| _| | ' <  / _ \\  | _ \\ / _ \\ | .` || ' < ",
            "|___| \\___/ |_|_\\|___||_|\\_\\/_/ \\_\\ |___//_/ \\_\\|_|\\_||_|\\_\\"};
        String[] tonos = {AMARILLO, AMARILLO, CIAN, CIAN};
        for (int k = 0; k < arte.length; k++) {
            out.println(c(N + tonos[k], "   " + arte[k]));
            pausa(60);
        }
        out.println(c(GRIS, "   EurekaBank Monster · Cliente CONSOLA · RESTful Java + MariaDB"));
        out.println(c(GRIS, "   Grupo 1 · NRC 36841 · Arquitectura de Software ESPE"));
        linea();
    }

    public void linea() {
        out.println(c(GRIS, "   ─────────────────────────────────────────────────"));
    }

    public void titulo(String t) {
        out.println();
        out.println(c(N + CIAN, "   ▶ " + t));
        linea();
    }

    public String leerTexto(String etiqueta, String porDefecto) {
        out.print(c(AMARILLO, "   " + etiqueta) + (porDefecto != null ? c(GRIS, " [" + porDefecto + "]") : "") + ": ");
        String t = entrada.hasNextLine() ? entrada.nextLine().trim() : "";
        return t.isEmpty() && porDefecto != null ? porDefecto : t;
    }

    public String leerClave(String etiqueta) {
        Console consola = System.console();
        if (consola != null) {
            char[] c = consola.readPassword("   " + etiqueta + ": ");
            return c == null ? "" : new String(c);
        }
        return leerTexto(etiqueta, null);
    }

    public int leerOpcion(String etiqueta, int min, int max) {
        while (true) {
            String t = leerTexto(etiqueta, null);
            try {
                int n = Integer.parseInt(t);
                if (n >= min && n <= max) {
                    return n;
                }
            } catch (NumberFormatException e) {
                // se vuelve a pedir
            }
            error("Ingrese un número entre " + min + " y " + max);
        }
    }

    /** Importe positivo con máximo dos decimales. */
    public double leerImporte(String etiqueta) {
        while (true) {
            String t = leerTexto(etiqueta, null).replace(',', '.');
            if (t.matches("\\d+(\\.\\d{1,2})?")) {
                double v = Double.parseDouble(t);
                if (v > 0) {
                    return v;
                }
            }
            error("Ingrese un importe mayor que cero con máximo 2 decimales (ej. 150.50)");
        }
    }

    /** Número de cuenta de 8 dígitos (acepta 100001 y lo completa a 00100001). */
    public String leerCuenta(String etiqueta) {
        while (true) {
            String t = leerTexto(etiqueta, null);
            if (t.matches("\\d{1,8}")) {
                return "0".repeat(8 - t.length()) + t;
            }
            error("La cuenta tiene 8 dígitos, por ejemplo 00100001");
        }
    }

    /** Muestra un catálogo numerado y devuelve el elemento elegido. */
    public <T> T elegir(String titulo, List<T> opciones) {
        out.println(c(N, "   " + titulo));
        for (int k = 0; k < opciones.size(); k++) {
            out.println("     " + c(AZUL, (k + 1) + ".") + " " + opciones.get(k));
        }
        return opciones.get(leerOpcion("Opción", 1, opciones.size()) - 1);
    }

    /** Código de 5 dígitos del cliente (acepta 1 y lo completa a 00001). */
    public String leerCodigoCliente(String etiqueta) {
        while (true) {
            String t = leerTexto(etiqueta, null);
            if (t.matches("\\d{1,5}")) {
                return "0".repeat(5 - t.length()) + t;
            }
            error("El código de cliente tiene 5 dígitos, por ejemplo 00001");
        }
    }

    public boolean confirmar(String pregunta) {
        String t = leerTexto(pregunta + " (s/n)", "s");
        return t.equalsIgnoreCase("s") || t.equalsIgnoreCase("si") || t.equalsIgnoreCase("sí");
    }

    public int menuPrincipal(String usuario) {
        titulo("MENÚ PRINCIPAL  ·  " + usuario);
        out.println("   " + c(AZUL, "1.") + " Listar cuentas");
        out.println("   " + c(AZUL, "2.") + " Consultar saldo");
        out.println("   " + c(AZUL, "3.") + " Ver movimientos");
        out.println("   " + c(VERDE, "4.") + " Depósito");
        out.println("   " + c(ROJO, "5.") + " Retiro");
        out.println("   " + c(CIAN, "6.") + " Transferencia");
        out.println("   " + c(AMARILLO, "7.") + " Abrir cuenta a un cliente");
        out.println("   " + c(AZUL, "8.") + " Cerrar sesión");
        out.println("   " + c(AZUL, "0.") + " Salir");
        linea();
        return leerOpcion("Opción", 0, 8);
    }

    public void cuentas(List<Cuenta> lista) {
        titulo("CUENTAS REGISTRADAS (" + lista.size() + ")");
        out.println(c(GRIS, String.format("   %-10s %-34s %-8s %14s  %s", "CUENTA", "TITULAR", "MONEDA", "SALDO", "ESTADO")));
        for (Cuenta k : lista) {
            String estado = k.isActiva() ? c(VERDE, k.getEstado()) : c(ROJO, k.getEstado());
            out.println(String.format("   %-10s %-34s %-8s %14s  %s", k.getCodigo(), recortar(k.getCliente(), 34),
                    k.getMoneda(), c(CIAN, String.format("%,.2f", k.getSaldo())), estado));
        }
    }

    public void saldo(Cuenta k) {
        String txt = String.format("Saldo: %,.2f %s", k.getSaldo(), k.getMoneda());
        out.println("   Cuenta   : " + c(N, k.getCodigo()) + "   (" + k.getEstado() + ")");
        out.println("   Titular  : " + k.getCliente());
        out.println("   Sucursal : " + k.getSucursal() + "   ·   Apertura: " + k.getFechaCreacion());
        caja(txt, VERDE);
    }

    public void movimientos(Cuenta k, List<Movimiento> lista) {
        out.println("   Cuenta " + c(N, k.getCodigo()) + " · " + k.getCliente() + " · "
                + c(CIAN, String.format("Saldo %,.2f %s", k.getSaldo(), k.getMoneda())));
        if (lista.isEmpty()) {
            info("La cuenta no tiene movimientos");
            return;
        }
        out.println(c(GRIS, String.format("   %4s  %-10s %-22s %14s  %-8s %s", "N.º", "FECHA", "TIPO", "IMPORTE", "EMPL.", "REF.")));
        for (Movimiento m : lista.subList(0, Math.min(15, lista.size()))) {
            String imp = String.format("%s%,.2f", m.isIngreso() ? "+" : "-", m.getImporte());
            out.println(String.format("   %4d  %-10s %-22s %s  %-8s %s", m.getNumero(), m.getFecha(), recortar(m.getTipo(), 22),
                    c(m.isIngreso() ? VERDE : ROJO, String.format("%14s", imp)), m.getEmpleado(),
                    m.getReferencia() == null ? "" : m.getReferencia()));
        }
        if (lista.size() > 15) {
            info("Mostrando los 15 más recientes de " + lista.size() + " movimientos");
        }
    }

    public void comprobante(ResultadoOperacion r, String mensaje) {
        out.println();
        out.println(c(N + VERDE, "   ✔ " + mensaje));
        out.println(c(GRIS, "   ── Comprobante ──────────────────────────────"));
        out.println("   Operación      : " + r.getOperacion());
        out.println("   Cuenta         : " + r.getCuenta() + (r.getCuentaDestino() != null ? "  →  " + r.getCuentaDestino() : ""));
        out.println(String.format("   Importe        : %,.2f %s", r.getImporte(), r.getMoneda()));
        out.println(String.format("   Saldo anterior : %,.2f", r.getSaldoAnterior()));
        out.println("   Movimiento N.º : " + r.getNumeroMovimiento() + "   ·   Empleado: " + r.getEmpleado());
        out.println("   Fecha          : " + r.getFecha());
        caja(String.format("Nuevo saldo: %,.2f %s", r.getSaldoActual(), r.getMoneda()), VERDE);
    }

    private void caja(String texto, String tono) {
        String marco = "═".repeat(texto.length() + 4);
        out.println(c(tono, "   ╔" + marco + "╗"));
        out.print(c(tono, "   ║  "));
        for (char ch : texto.toCharArray()) {
            out.print(c(N + tono, String.valueOf(ch)));
            pausa(15);
        }
        out.println(c(tono, "  ║"));
        out.println(c(tono, "   ╚" + marco + "╝"));
    }

    private static String recortar(String t, int max) {
        if (t == null) {
            return "";
        }
        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }

    /** Muestra una animación corta mientras se espera al servidor. */
    public void esperando(String texto) {
        String[] f = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
        for (int k = 0; k < 10; k++) {
            out.print("\r   " + c(CIAN, f[k]) + " " + texto + "…");
            pausa(35);
        }
        out.print("\r" + " ".repeat(texto.length() + 8) + "\r");
    }

    public void exito(String t) {
        out.println(c(VERDE, "   ✔ " + t));
    }

    public void error(String t) {
        out.println(c(ROJO, "   ✖ " + t));
    }

    public void info(String t) {
        out.println(c(GRIS, "   • " + t));
    }

    public void pausaEnter() {
        leerTexto("Presione ENTER para continuar", null);
    }

    private static void pausa(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
