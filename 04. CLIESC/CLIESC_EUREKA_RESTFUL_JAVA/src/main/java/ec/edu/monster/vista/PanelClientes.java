package ec.edu.monster.vista;

import ec.edu.monster.modelo.Cliente;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Vista de clientes: búsqueda, tabla y formulario para el CRUD
 * (insertar, seleccionar, listar, actualizar y eliminar).
 *
 * @author Grupo 1 - NRC 36841
 */
public class PanelClientes extends JPanel {

    private final JTextField txtBuscar = Componentes.campo("Buscar por nombre, apellido, cédula o código");
    private final JButton btnBuscar = Componentes.boton("Buscar", Estilo.CIAN);
    private final DefaultTableModel modelo = Componentes.modelo("Código", "Nombre", "Ap. paterno", "Ap. materno", "Cédula", "Ciudad");
    private final JTable tabla = Componentes.tabla(modelo);

    private final JLabel lblCodigo = Componentes.etiqueta("Nuevo cliente", Estilo.SUBTITULO, Estilo.DORADO);
    private final JTextField txtNombre = Componentes.campo("Nombres");
    private final JTextField txtPaterno = Componentes.campo("Apellido paterno");
    private final JTextField txtMaterno = Componentes.campo("Apellido materno");
    private final JTextField txtCedula = Componentes.campo("10 dígitos");
    private final JTextField txtCiudad = Componentes.campo("Ciudad");
    private final JTextField txtDireccion = Componentes.campo("Dirección");
    private final JTextField txtTelefono = Componentes.campo("Opcional");
    private final JTextField txtEmail = Componentes.campo("Opcional");
    private final JButton btnNuevo = Componentes.boton("Nuevo", Estilo.PANEL2);
    private final JButton btnGuardar = Componentes.boton("Guardar", Estilo.VERDE);
    private final JButton btnEliminar = Componentes.boton("Eliminar", Estilo.ROJO);
    private final JButton btnAbrirCuenta = Componentes.boton("Abrir cuenta a este cliente", Estilo.DORADO);
    private final JLabel lblMensaje = Componentes.etiqueta(" ", Estilo.PEQUENA, Estilo.TENUE);
    private final Componentes.Tarjeta formulario = new Componentes.Tarjeta(new GridBagLayout());
    private List<Cliente> clientes = List.of();
    private String codigoActual;

    public PanelClientes() {
        super(new BorderLayout(20, 0));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Lista
        Componentes.Tarjeta lista = new Componentes.Tarjeta(new BorderLayout(0, 10));
        JPanel busqueda = new JPanel(new BorderLayout(8, 0));
        busqueda.setOpaque(false);
        busqueda.add(txtBuscar, BorderLayout.CENTER);
        busqueda.add(btnBuscar, BorderLayout.EAST);
        lista.add(busqueda, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createEmptyBorder());
        lista.add(sp, BorderLayout.CENTER);
        tabla.getColumnModel().getColumn(0).setMaxWidth(70);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(170);
        tabla.getColumnModel().getColumn(4).setMaxWidth(90);

        // Formulario
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.gridx = 0;
        g.gridwidth = 2;
        g.gridy = 0;
        g.insets = new Insets(0, 0, 8, 0);
        formulario.add(lblCodigo, g);
        Object[][] campos = {{"Nombres *", txtNombre}, {"Apellido paterno *", txtPaterno}, {"Apellido materno *", txtMaterno},
            {"Cédula *", txtCedula}, {"Ciudad *", txtCiudad}, {"Dirección *", txtDireccion}, {"Teléfono", txtTelefono}, {"Correo", txtEmail}};
        g.gridwidth = 1;
        int fila = 1;
        for (Object[] c : campos) {
            g.gridy = fila++;
            g.gridx = 0;
            g.weightx = 0;
            g.insets = new Insets(3, 0, 3, 10);
            formulario.add(Componentes.etiqueta((String) c[0], Estilo.PEQUENA, Estilo.TENUE), g);
            g.gridx = 1;
            g.weightx = 1;
            g.insets = new Insets(3, 0, 3, 0);
            ((JTextField) c[1]).setColumns(16);
            formulario.add((JTextField) c[1], g);
        }
        JPanel botones = new JPanel(new GridLayout(1, 3, 8, 0));
        botones.setOpaque(false);
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnEliminar);
        btnNuevo.setForeground(Estilo.TEXTO);
        g.gridx = 0;
        g.gridwidth = 2;
        g.gridy = fila++;
        g.insets = new Insets(14, 0, 4, 0);
        formulario.add(botones, g);
        g.gridy = fila++;
        g.insets = new Insets(4, 0, 4, 0);
        formulario.add(btnAbrirCuenta, g);
        g.gridy = fila++;
        g.insets = new Insets(4, 0, 0, 0);
        formulario.add(lblMensaje, g);
        g.gridy = fila;
        g.weighty = 1;
        formulario.add(new JLabel(), g);
        formulario.setPreferredSize(Componentes.dim(350, 0));

        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setOpaque(false);
        derecha.add(formulario, BorderLayout.CENTER);

        add(lista, BorderLayout.CENTER);
        add(derecha, BorderLayout.EAST);
        limpiar();
    }

    public void mostrar(List<Cliente> lista) {
        clientes = lista;
        modelo.setRowCount(0);
        for (Cliente c : lista) {
            modelo.addRow(new Object[]{c.getCodigo(), c.getNombre(), c.getPaterno(), c.getMaterno(), c.getCedula(), c.getCiudad()});
        }
    }

    /** Cliente de la fila seleccionada o null. */
    public Cliente getSeleccionado() {
        int f = tabla.getSelectedRow();
        return f < 0 ? null : clientes.get(tabla.convertRowIndexToModel(f));
    }

    public void cargar(Cliente c) {
        codigoActual = c.getCodigo();
        lblCodigo.setText("Editando cliente " + c.getCodigo());
        txtNombre.setText(c.getNombre());
        txtPaterno.setText(c.getPaterno());
        txtMaterno.setText(c.getMaterno());
        txtCedula.setText(c.getCedula());
        txtCiudad.setText(c.getCiudad());
        txtDireccion.setText(c.getDireccion());
        txtTelefono.setText(c.getTelefono() == null ? "" : c.getTelefono());
        txtEmail.setText(c.getEmail() == null ? "" : c.getEmail());
        btnEliminar.setEnabled(true);
        btnAbrirCuenta.setEnabled(true);
        mensaje(" ", Estilo.TENUE);
    }

    public final void limpiar() {
        codigoActual = null;
        lblCodigo.setText("Nuevo cliente");
        for (JTextField t : new JTextField[]{txtNombre, txtPaterno, txtMaterno, txtCedula, txtCiudad, txtDireccion, txtTelefono, txtEmail}) {
            t.setText("");
        }
        btnEliminar.setEnabled(false);
        btnAbrirCuenta.setEnabled(false);
        tabla.clearSelection();
    }

    /** Arma el cliente con los datos del formulario (código null si es nuevo). */
    public Cliente leerFormulario() {
        Cliente c = new Cliente();
        c.setCodigo(codigoActual);
        c.setNombre(txtNombre.getText().trim());
        c.setPaterno(txtPaterno.getText().trim());
        c.setMaterno(txtMaterno.getText().trim());
        c.setCedula(txtCedula.getText().trim());
        c.setCiudad(txtCiudad.getText().trim());
        c.setDireccion(txtDireccion.getText().trim());
        c.setTelefono(txtTelefono.getText().trim());
        c.setEmail(txtEmail.getText().trim());
        return c;
    }

    public void mensaje(String m, Color c) {
        lblMensaje.setForeground(c);
        lblMensaje.setText("<html>" + m + "</html>");
        if (c == Estilo.ROJO) {
            Componentes.destello(formulario, Estilo.ROJO);
            Componentes.sacudir(formulario);
        } else if (c == Estilo.VERDE) {
            Componentes.destello(formulario, Estilo.VERDE);
        }
    }

    public String getCodigoActual() {
        return codigoActual;
    }

    public String getFiltro() {
        return txtBuscar.getText().trim();
    }

    public JTable getTabla() {
        return tabla;
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    public JTextField getTxtBuscar() {
        return txtBuscar;
    }

    public JButton getBtnNuevo() {
        return btnNuevo;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnAbrirCuenta() {
        return btnAbrirCuenta;
    }

    /** Nombre del cliente cargado en el formulario. */
    public String getNombreActual() {
        return leerFormulario().getNombre() + " " + leerFormulario().getPaterno();
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }
}
