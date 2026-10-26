package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Cliente;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.ExcepcionCliente;
import ec.edu.monster.modelo.ResultadoOperacion;
import ec.edu.monster.modelo.ServicioRest;
import ec.edu.monster.vista.Estilo;
import ec.edu.monster.vista.PanelClientes;
import ec.edu.monster.vista.PanelCuentas;
import ec.edu.monster.vista.PanelMovimientos;
import ec.edu.monster.vista.PanelOperaciones;
import ec.edu.monster.vista.VentanaLogin;
import ec.edu.monster.vista.VentanaPrincipal;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Controlador del cliente de escritorio. Las llamadas al servidor se hacen en
 * segundo plano (SwingWorker) para no congelar la interfaz.
 *
 * @author Grupo 1 - NRC 36841
 */
public class ControladorEscritorio {

    private final ServicioRest servicio;
    private final VentanaLogin login;
    private final VentanaPrincipal principal = new VentanaPrincipal();
    private final PanelCuentas pCuentas = principal.getPanelCuentas();
    private final PanelOperaciones pOperaciones = principal.getPanelOperaciones();
    private final PanelMovimientos pMovimientos = principal.getPanelMovimientos();
    private final PanelClientes pClientes = principal.getPanelClientes();

    public ControladorEscritorio(ServicioRest servicio) {
        this.servicio = servicio;
        this.login = new VentanaLogin(servicio.getUrlBase());
        login.getBtnIngresar().addActionListener(e -> iniciarSesion());
        principal.getBtnCerrarSesion().addActionListener(e -> cerrarSesion());
        principal.setAlCambiarVista(this::alCambiarVista);

        pCuentas.getBtnActualizar().addActionListener(e -> cargarCuentas());
        pOperaciones.getBtnProcesar().addActionListener(e -> procesarOperacion());
        pOperaciones.getCmbCuenta().addActionListener(e -> pOperaciones.actualizarSaldoSeleccion());
        pMovimientos.getBtnConsultar().addActionListener(e -> consultarMovimientos());
        pClientes.getBtnBuscar().addActionListener(e -> cargarClientes());
        pClientes.getTxtBuscar().addActionListener(e -> cargarClientes());
        pClientes.getBtnNuevo().addActionListener(e -> pClientes.limpiar());
        pClientes.getBtnGuardar().addActionListener(e -> guardarCliente());
        pClientes.getBtnEliminar().addActionListener(e -> eliminarCliente());
        pClientes.getBtnAbrirCuenta().addActionListener(e -> abrirCuenta());
        pClientes.getTabla().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarCliente();
            }
        });
        principal.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                servicio.cerrarSesion();
                System.exit(0);
            }
        });
    }

    public void iniciar() {
        login.setVisible(true);
    }

    // ------------------------------------------------------------ sesión

    private void iniciarSesion() {
        if (login.getUsuario().isEmpty() || login.getClave().isEmpty()) {
            login.error("Ingrese usuario y clave");
            return;
        }
        servicio.setUrlBase(login.getServidor());
        login.cargando(true);
        enSegundoPlano(() -> servicio.iniciarSesion(login.getUsuario(), login.getClave()), msg -> {
            login.cargando(false);
            principal.setUsuario(servicio.getNombreCompleto() + " (" + servicio.getUsuario() + ")");
            principal.setEstado(msg + "   ·   Servidor: " + servicio.getUrlBase() + "   ·   Sesión válida por 30 minutos");
            login.setVisible(false);
            login.limpiar();
            principal.setVisible(true);
            principal.mostrar(VentanaPrincipal.CUENTAS);
        }, e -> {
            login.cargando(false);
            login.error(e.getMessage());
        });
    }

    private void cerrarSesion() {
        int r = JOptionPane.showConfirmDialog(principal, "¿Desea cerrar la sesión?", "Cerrar sesión",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Estilo.monster(48));
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        enSegundoPlano(() -> {
            servicio.cerrarSesion();
            return true;
        }, x -> volverAlLogin("Sesión cerrada correctamente"), e -> volverAlLogin(null));
    }

    private void volverAlLogin(String mensaje) {
        principal.setVisible(false);
        login.setVisible(true);
        if (mensaje != null) {
            login.mensaje(mensaje, Estilo.VERDE);
        }
    }

    /** Al entrar a una vista se cargan sus datos del servidor. */
    private void alCambiarVista(String vista) {
        if (!servicio.haySesion()) {
            return;
        }
        switch (vista) {
            case VentanaPrincipal.CLIENTES -> cargarClientes();
            default -> cargarCuentas();
        }
    }

    // ------------------------------------------------------------ cuentas

    private void cargarCuentas() {
        enSegundoPlano(servicio::listarCuentas, (List<Cuenta> l) -> {
            pCuentas.mostrar(l);
            pOperaciones.setCuentas(l);
            pMovimientos.setCuentas(l);
        }, e -> principal.setEstado("Error: " + e.getMessage()));
    }

    private void consultarMovimientos() {
        Cuenta c = pMovimientos.getCuenta();
        if (c == null) {
            return;
        }
        enSegundoPlano(() -> new Object[]{servicio.consultarCuenta(c.getCodigo()), servicio.listarMovimientos(c.getCodigo())},
                r -> {
                    @SuppressWarnings("unchecked")
                    List<ec.edu.monster.modelo.Movimiento> movs = (List<ec.edu.monster.modelo.Movimiento>) r[1];
                    pMovimientos.mostrar((Cuenta) r[0], movs);
                }, e -> JOptionPane.showMessageDialog(principal, e.getMessage(), "Movimientos", JOptionPane.WARNING_MESSAGE));
    }

    // ------------------------------------------------------------ operaciones

    private void procesarOperacion() {
        Cuenta c = pOperaciones.getCuenta();
        String op = pOperaciones.getOperacion();
        Cuenta d = pOperaciones.getDestino();
        String texto = pOperaciones.getImporteTexto();
        if (c == null) {
            pOperaciones.mostrarError("Seleccione una cuenta");
            return;
        }
        if (!texto.matches("\\d+(\\.\\d{1,2})?") || Double.parseDouble(texto) <= 0) {
            pOperaciones.mostrarError("Ingrese un importe mayor que cero con máximo 2 decimales (ej. 150.50)");
            return;
        }
        double importe = Double.parseDouble(texto);
        if (op.equals("TRANSFERENCIA") && (d == null || d.getCodigo().equals(c.getCodigo()))) {
            pOperaciones.mostrarError("Seleccione una cuenta destino diferente de la cuenta origen");
            return;
        }
        String pregunta = String.format("¿Confirma %s de %s %s%s?", op.toLowerCase(), Estilo.dinero(importe), c.getMoneda(),
                op.equals("TRANSFERENCIA") ? " de " + c.getCodigo() + " a " + d.getCodigo() : " en la cuenta " + c.getCodigo());
        if (JOptionPane.showConfirmDialog(principal, pregunta, "Confirmar operación", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, Estilo.monster(48)) != JOptionPane.YES_OPTION) {
            return;
        }
        pOperaciones.cargando(true);
        enSegundoPlano(() -> switch (op) {
            case "DEPOSITO" -> servicio.depositar(c.getCodigo(), importe);
            case "RETIRO" -> servicio.retirar(c.getCodigo(), importe);
            default -> servicio.transferir(c.getCodigo(), d.getCodigo(), importe);
        }, (ResultadoOperacion r) -> {
            pOperaciones.cargando(false);
            pOperaciones.mostrarComprobante(r, servicio.getUltimoMensaje());
            cargarCuentas();
        }, e -> {
            pOperaciones.cargando(false);
            pOperaciones.mostrarError(e.getMessage());
        });
    }

    // ------------------------------------------------------------ clientes (CRUD)

    private void cargarClientes() {
        enSegundoPlano(() -> servicio.listarClientes(pClientes.getFiltro()), pClientes::mostrar,
                e -> pClientes.mensaje(e.getMessage(), Estilo.ROJO));
    }

    private void seleccionarCliente() {
        Cliente sel = pClientes.getSeleccionado();
        if (sel == null) {
            return;
        }
        enSegundoPlano(() -> servicio.seleccionarCliente(sel.getCodigo()), pClientes::cargar,
                e -> pClientes.mensaje(e.getMessage(), Estilo.ROJO));
    }

    private void guardarCliente() {
        Cliente c = pClientes.leerFormulario();
        boolean nuevo = c.getCodigo() == null;
        enSegundoPlano(() -> nuevo ? servicio.insertarCliente(c) : servicio.actualizarCliente(c), (Cliente r) -> {
            pClientes.cargar(r);
            pClientes.mensaje(nuevo ? "Cliente " + r.getCodigo() + " registrado correctamente"
                    : "Cliente " + r.getCodigo() + " actualizado correctamente", Estilo.VERDE);
            cargarClientes();
        }, e -> pClientes.mensaje(e.getMessage(), Estilo.ROJO));
    }

    private void eliminarCliente() {
        String codigo = pClientes.getCodigoActual();
        if (codigo == null) {
            return;
        }
        if (JOptionPane.showConfirmDialog(principal, "¿Eliminar el cliente " + codigo + "?", "Eliminar cliente",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, Estilo.monster(48)) != JOptionPane.YES_OPTION) {
            return;
        }
        enSegundoPlano(() -> servicio.eliminarCliente(codigo), msg -> {
            pClientes.limpiar();
            pClientes.mensaje(msg, Estilo.VERDE);
            cargarClientes();
        }, e -> pClientes.mensaje(e.getMessage(), Estilo.ROJO));
    }

    /** Pide moneda, sucursal, depósito inicial y clave, y abre la cuenta. */
    private void abrirCuenta() {
        String codigo = pClientes.getCodigoActual();
        if (codigo == null) {
            return;
        }
        enSegundoPlano(servicio::listarCatalogos, cat -> {
            javax.swing.JComboBox<ec.edu.monster.modelo.Opcion> cmbMoneda = new javax.swing.JComboBox<>(
                    cat.get("monedas").toArray(new ec.edu.monster.modelo.Opcion[0]));
            javax.swing.JComboBox<ec.edu.monster.modelo.Opcion> cmbSucursal = new javax.swing.JComboBox<>(
                    cat.get("sucursales").toArray(new ec.edu.monster.modelo.Opcion[0]));
            javax.swing.JTextField txtImporte = new javax.swing.JTextField();
            javax.swing.JPasswordField txtClave = new javax.swing.JPasswordField();
            Object[] campos = {"Titular: " + codigo + " · " + pClientes.getNombreActual(), " ",
                "Moneda", cmbMoneda, "Sucursal", cmbSucursal, "Depósito inicial (USD)", txtImporte, "PIN de la cuenta (6 dígitos que elige el cliente)", txtClave, " ", "El número de cuenta se genera automáticamente."};
            if (JOptionPane.showConfirmDialog(principal, campos, "Abrir cuenta", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, Estilo.monster(48)) != JOptionPane.OK_OPTION) {
                return;
            }
            String imp = txtImporte.getText().trim().replace(',', '.');
            if (!imp.matches("\\d+(\\.\\d{1,2})?") || Double.parseDouble(imp) <= 0) {
                pClientes.mensaje("El depósito inicial debe ser mayor que cero con máximo 2 decimales", Estilo.ROJO);
                return;
            }
            String moneda = ((ec.edu.monster.modelo.Opcion) cmbMoneda.getSelectedItem()).getCodigo();
            String sucursal = ((ec.edu.monster.modelo.Opcion) cmbSucursal.getSelectedItem()).getCodigo();
            String clave = new String(txtClave.getPassword());
            enSegundoPlano(() -> servicio.abrirCuenta(codigo, moneda, sucursal, Double.parseDouble(imp), clave), (Cuenta c) -> {
                pClientes.mensaje(servicio.getUltimoMensaje(), Estilo.VERDE);
                cargarCuentas();
                JOptionPane.showMessageDialog(principal, "Cuenta " + c.getCodigo() + " abierta.\nSaldo inicial: "
                        + Estilo.dinero(c.getSaldo()) + " " + c.getMoneda() + "\nYa aparece en Cuentas, Operaciones y Movimientos.",
                        "Cuenta abierta", JOptionPane.INFORMATION_MESSAGE, Estilo.monster(48));
            }, e -> pClientes.mensaje(e.getMessage(), Estilo.ROJO));
        }, e -> pClientes.mensaje(e.getMessage(), Estilo.ROJO));
    }

    /** Ejecuta una llamada al servidor sin bloquear la interfaz. */
    private <T> void enSegundoPlano(Callable<T> tarea, Consumer<T> alTerminar, Consumer<ExcepcionCliente> alFallar) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return tarea.call();
            }

            @Override
            protected void done() {
                try {
                    alTerminar.accept(get());
                } catch (java.util.concurrent.ExecutionException ex) {
                    Throwable c = ex.getCause();
                    ExcepcionCliente e = c instanceof ExcepcionCliente ec ? ec : new ExcepcionCliente("Error: " + c.getMessage(), 0);
                    if (e.sesionExpirada() && principal.isVisible()) {
                        JOptionPane.showMessageDialog(principal, e.getMessage(), "Sesión expirada", JOptionPane.WARNING_MESSAGE);
                        volverAlLogin(null);
                        login.error("Su sesión expiró. Inicie sesión nuevamente.");
                        return;
                    }
                    alFallar.accept(e);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }.execute();
    }
}
