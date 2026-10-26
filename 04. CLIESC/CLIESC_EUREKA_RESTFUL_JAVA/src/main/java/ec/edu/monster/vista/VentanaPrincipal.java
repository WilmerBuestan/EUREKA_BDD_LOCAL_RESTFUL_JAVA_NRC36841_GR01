package ec.edu.monster.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 * Ventana principal: cabecera con el Monster, menú lateral y las cuatro
 * vistas del sistema (Cuentas, Operaciones, Movimientos y Clientes).
 *
 * @author Grupo 1 - NRC 36841
 */
public class VentanaPrincipal extends JFrame {

    public static final String CUENTAS = "Cuentas";
    public static final String OPERACIONES = "Operaciones";
    public static final String MOVIMIENTOS = "Movimientos";
    public static final String CLIENTES = "Clientes";

    private final JLabel lblUsuario = Componentes.etiqueta("", Estilo.NORMAL, Estilo.TEXTO);
    private final JButton btnCerrarSesion = Componentes.boton("Cerrar sesión", Estilo.ROJO);
    private final JLabel lblEstado = Componentes.etiqueta(" ", Estilo.PEQUENA, Estilo.TENUE);
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final Map<String, JButton> menu = new LinkedHashMap<>();

    private final PanelCuentas panelCuentas = new PanelCuentas();
    private final PanelOperaciones panelOperaciones = new PanelOperaciones();
    private final PanelMovimientos panelMovimientos = new PanelMovimientos();
    private final PanelClientes panelClientes = new PanelClientes();
    private Consumer<String> alCambiarVista = v -> { };

    public VentanaPrincipal() {
        super("EurekaBank Monster - Cliente Escritorio");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(1180, 720);
        setMinimumSize(Componentes.dim(1060, 660));
        setLocationRelativeTo(null);
        setIconImage(Estilo.monster(64).getImage());

        // Cabecera
        JPanel cabecera = new Componentes.Degradado(new BorderLayout(), new Color(0x1D3561), Estilo.FONDO);
        cabecera.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        marca.setOpaque(false);
        marca.add(new JLabel(Estilo.monster(52)));
        JPanel titulos = new JPanel(new GridLayout(2, 1));
        titulos.setOpaque(false);
        titulos.add(Componentes.etiqueta("EurekaBank Monster", Estilo.TITULO, Estilo.DORADO));
        titulos.add(Componentes.etiqueta("RESTful Java + MariaDB · Grupo 1 · NRC 36841", Estilo.PEQUENA, Estilo.CIAN));
        marca.add(titulos);
        JPanel usuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        usuario.setOpaque(false);
        usuario.add(lblUsuario);
        usuario.add(btnCerrarSesion);
        cabecera.add(marca, BorderLayout.WEST);
        cabecera.add(usuario, BorderLayout.EAST);

        // Menú lateral
        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBackground(Estilo.PANEL);
        lateral.setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));
        String[][] opciones = {{CUENTAS, "Cuentas y saldos"}, {OPERACIONES, "Depósito · Retiro · Transferencia"},
            {MOVIMIENTOS, "Estado de cuenta"}, {CLIENTES, "Registrar y editar clientes"}};
        for (String[] o : opciones) {
            JButton b = new JButton("<html><b>" + o[0] + "</b><br><span style='font-size:9px'>" + o[1] + "</span></html>");
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setFont(Estilo.NORMAL);
            b.setForeground(Estilo.TEXTO);
            b.setBackground(Estilo.PANEL);
            b.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setMaximumSize(Componentes.dim(230, 58));
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.addActionListener(e -> mostrar(o[0]));
            menu.put(o[0], b);
            lateral.add(b);
            lateral.add(Box.createVerticalStrut(6));
        }
        lateral.add(Box.createVerticalGlue());
        JLabel pie = Componentes.etiqueta("<html>Empleado responsable de<br>los movimientos: el usuario<br>de la sesión.</html>",
                Estilo.PEQUENA, Estilo.TENUE);
        lateral.add(pie);

        contenido.setBackground(Estilo.FONDO);
        contenido.add(panelCuentas, CUENTAS);
        contenido.add(panelOperaciones, OPERACIONES);
        contenido.add(panelMovimientos, MOVIMIENTOS);
        contenido.add(panelClientes, CLIENTES);

        JPanel pieEstado = new JPanel(new BorderLayout());
        pieEstado.setBackground(Estilo.PANEL);
        pieEstado.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        pieEstado.add(lblEstado);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilo.FONDO);
        raiz.add(cabecera, BorderLayout.NORTH);
        raiz.add(lateral, BorderLayout.WEST);
        raiz.add(contenido, BorderLayout.CENTER);
        raiz.add(pieEstado, BorderLayout.SOUTH);
        setContentPane(raiz);
        mostrar(CUENTAS);
    }

    /** Cambia la vista visible y resalta la opción del menú. */
    public final void mostrar(String vista) {
        tarjetas.show(contenido, vista);
        menu.forEach((k, b) -> {
            boolean activo = k.equals(vista);
            b.setBackground(activo ? Estilo.PANEL2 : Estilo.PANEL);
            b.setForeground(activo ? Estilo.DORADO : Estilo.TEXTO);
        });
        alCambiarVista.accept(vista);
    }

    public void setAlCambiarVista(Consumer<String> c) {
        this.alCambiarVista = c;
    }

    public void setUsuario(String nombre) {
        lblUsuario.setText("Empleado: " + nombre);
    }

    public void setEstado(String texto) {
        lblEstado.setText(texto);
    }

    public JButton getBtnCerrarSesion() {
        return btnCerrarSesion;
    }

    public PanelCuentas getPanelCuentas() {
        return panelCuentas;
    }

    public PanelOperaciones getPanelOperaciones() {
        return panelOperaciones;
    }

    public PanelMovimientos getPanelMovimientos() {
        return panelMovimientos;
    }

    public PanelClientes getPanelClientes() {
        return panelClientes;
    }
}
