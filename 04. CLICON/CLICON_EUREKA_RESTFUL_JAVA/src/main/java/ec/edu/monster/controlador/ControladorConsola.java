package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.Opcion;
import ec.edu.monster.modelo.ResultadoOperacion;
import ec.edu.monster.modelo.ServicioRest;
import ec.edu.monster.vista.VistaConsola;

/**
 * Controlador del cliente de consola: coordina la vista con el servicio REST.
 *
 * @author Grupo 1 - NRC 36841
 */
public class ControladorConsola {

    private final VistaConsola vista;
    private final ServicioRest servicio;

    public ControladorConsola(VistaConsola vista, ServicioRest servicio) {
        this.vista = vista;
        this.servicio = servicio;
    }

    public void iniciar() {
        vista.limpiar();
        vista.banner();
        servicio.setUrlBase(vista.leerTexto("URL del servidor", servicio.getUrlBase()));
        while (true) {
            if (!servicio.haySesion() && !login()) {
                vista.info("Gracias por usar EurekaBank. Hasta pronto");
                return;
            }
            int op = vista.menuPrincipal(servicio.getNombreCompleto());
            try {
                switch (op) {
                    case 1 -> {
                        vista.esperando("Consultando cuentas");
                        vista.cuentas(servicio.listarCuentas());
                        vista.pausaEnter();
                    }
                    case 2 -> consultarSaldo();
                    case 3 -> movimientos();
                    case 4 -> operar("DEPÓSITO");
                    case 5 -> operar("RETIRO");
                    case 6 -> operar("TRANSFERENCIA");
                    case 7 -> abrirCuenta();
                    case 8 -> {
                        servicio.cerrarSesion();
                        vista.exito("Sesión cerrada correctamente");
                    }
                    default -> {
                        servicio.cerrarSesion();
                        vista.info("Gracias por usar EurekaBank. Hasta pronto");
                        return;
                    }
                }
            } catch (ExcepcionCliente e) {
                vista.error(e.getMessage());
                if (e.sesionExpirada()) {
                    vista.info("Debe iniciar sesión nuevamente");
                }
            }
        }
    }

    /** @return false si el usuario decide salir */
    private boolean login() {
        while (true) {
            vista.titulo("INICIO DE SESIÓN");
            String usuario = vista.leerTexto("Usuario (o 'salir')", null);
            if (usuario.equalsIgnoreCase("salir")) {
                return false;
            }
            String clave = vista.leerClave("Clave");
            try {
                vista.esperando("Validando credenciales en la base de datos");
                vista.exito(servicio.iniciarSesion(usuario, clave));
                return true;
            } catch (ExcepcionCliente e) {
                vista.error(e.getMessage());
            }
        }
    }

    private void consultarSaldo() throws ExcepcionCliente {
        vista.titulo("CONSULTAR SALDO");
        String cuenta = vista.leerCuenta("Número de cuenta");
        vista.esperando("Consultando saldo");
        try {
            Cuenta c = servicio.consultarCuenta(cuenta);
            vista.saldo(c);
        } catch (ExcepcionCliente e) {
            relanzarSiExpiro(e);
            vista.error(e.getMessage());
        }
        vista.pausaEnter();
    }

    private void movimientos() throws ExcepcionCliente {
        vista.titulo("MOVIMIENTOS DE CUENTA");
        String cuenta = vista.leerCuenta("Número de cuenta");
        vista.esperando("Consultando movimientos");
        try {
            Cuenta c = servicio.consultarCuenta(cuenta);
            vista.movimientos(c, servicio.listarMovimientos(cuenta));
        } catch (ExcepcionCliente e) {
            relanzarSiExpiro(e);
            vista.error(e.getMessage());
        }
        vista.pausaEnter();
    }

    private void operar(String tipo) throws ExcepcionCliente {
        vista.titulo(tipo);
        boolean transferencia = tipo.equals("TRANSFERENCIA");
        String cuenta = vista.leerCuenta(transferencia ? "Cuenta ORIGEN" : "Número de cuenta");
        String destino = transferencia ? vista.leerCuenta("Cuenta DESTINO") : null;
        double importe = vista.leerImporte("Importe");
        if (!vista.confirmar(String.format("¿Confirma %s de %.2f%s?", tipo.toLowerCase(), importe,
                transferencia ? " de " + cuenta + " a " + destino : " en la cuenta " + cuenta))) {
            vista.info("Operación cancelada");
            return;
        }
        vista.esperando("Procesando " + tipo.toLowerCase());
        try {
            ResultadoOperacion r = switch (tipo) {
                case "DEPÓSITO" -> servicio.depositar(cuenta, importe);
                case "RETIRO" -> servicio.retirar(cuenta, importe);
                default -> servicio.transferir(cuenta, destino, importe);
            };
            vista.comprobante(r, servicio.getUltimoMensaje());
        } catch (ExcepcionCliente e) {
            relanzarSiExpiro(e);
            vista.error(e.getMessage());
        }
        vista.pausaEnter();
    }

    private void abrirCuenta() throws ExcepcionCliente {
        vista.titulo("ABRIR CUENTA");
        String cliente = vista.leerCodigoCliente("Código del cliente titular");
        try {
            var cli = servicio.seleccionarCliente(cliente);
            vista.info("Titular: " + cli.getNombreCompleto() + " · Cédula " + cli.getCedula());
            var cat = servicio.listarCatalogos();
            Opcion moneda = vista.elegir("Moneda", cat.get("monedas"));
            Opcion sucursal = vista.elegir("Sucursal", cat.get("sucursales"));
            double importe = vista.leerImporte("Depósito inicial");
            String clave = vista.leerClave("PIN de la cuenta (6 dígitos; el número de cuenta se genera solo)");
            if (!vista.confirmar("¿Abrir la cuenta en " + moneda.getNombre() + "?")) {
                vista.info("Operación cancelada");
                return;
            }
            vista.esperando("Abriendo cuenta");
            Cuenta c = servicio.abrirCuenta(cliente, moneda.getCodigo(), sucursal.getCodigo(), importe, clave);
            vista.exito(servicio.getUltimoMensaje());
            vista.saldo(c);
        } catch (ExcepcionCliente e) {
            relanzarSiExpiro(e);
            vista.error(e.getMessage());
        }
        vista.pausaEnter();
    }

    private static void relanzarSiExpiro(ExcepcionCliente e) throws ExcepcionCliente {
        if (e.sesionExpirada()) {
            throw e;
        }
    }
}
