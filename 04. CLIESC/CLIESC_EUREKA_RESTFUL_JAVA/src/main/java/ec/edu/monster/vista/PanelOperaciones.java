package ec.edu.monster.vista;

import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.ResultadoOperacion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Vista de operaciones: depósito, retiro y transferencia con comprobante.
 *
 * @author Grupo 1 - NRC 36841
 */
public class PanelOperaciones extends JPanel {

    private final JButton btnDeposito = Componentes.boton("Depósito", Estilo.VERDE);
    private final JButton btnRetiro = Componentes.boton("Retiro", Estilo.PANEL2);
    private final JButton btnTransferencia = Componentes.boton("Transferencia", Estilo.PANEL2);
    private final JComboBox<Cuenta> cmbCuenta = new JComboBox<>();
    private final JComboBox<Cuenta> cmbDestino = new JComboBox<>();
    private final JLabel lblDestino = Componentes.etiqueta("Cuenta destino", Estilo.PEQUENA, Estilo.TENUE);
    private final JLabel lblSaldoActual = Componentes.etiqueta(" ", Estilo.PEQUENA, Estilo.CIAN);
    private final JTextField txtImporte = Componentes.campo("Importe (ej. 150.50)");
    private final JButton btnProcesar = Componentes.boton("Procesar depósito", Estilo.VERDE);
    private final Componentes.Tarjeta comprobante = new Componentes.Tarjeta(new BorderLayout(0, 8));
    private final JLabel lblTituloComp = Componentes.etiqueta("Comprobante", Estilo.SUBTITULO, Estilo.TEXTO);
    private final JLabel lblNuevoSaldo = Componentes.etiqueta("—", Estilo.RESULTADO, Estilo.TEXTO);
    private final JLabel lblDetalle = Componentes.etiqueta("Seleccione la operación, la cuenta y el importe.", Estilo.NORMAL, Estilo.TENUE);
    private String operacion = "DEPOSITO";

    public PanelOperaciones() {
        super(new GridLayout(1, 2, 20, 0));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Componentes.Tarjeta form = new Componentes.Tarjeta(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.insets = new Insets(5, 0, 5, 0);
        g.gridy = 0;
        form.add(Componentes.etiqueta("Nueva operación", Estilo.SUBTITULO, Estilo.TEXTO), g);
        JPanel tipos = new JPanel(new GridLayout(1, 3, 8, 0));
        tipos.setOpaque(false);
        for (JButton b : new JButton[]{btnDeposito, btnRetiro, btnTransferencia}) {
            b.setFont(Estilo.NORMAL.deriveFont(java.awt.Font.BOLD));
            b.setBorder(BorderFactory.createEmptyBorder(10, 6, 10, 6));
        }
        tipos.add(btnDeposito);
        tipos.add(btnRetiro);
        tipos.add(btnTransferencia);
        g.gridy = 1;
        form.add(tipos, g);
        g.gridy = 2;
        g.insets = new Insets(14, 0, 2, 0);
        form.add(Componentes.etiqueta("Cuenta", Estilo.PEQUENA, Estilo.TENUE), g);
        g.gridy = 3;
        g.insets = new Insets(0, 0, 2, 0);
        form.add(cmbCuenta, g);
        g.gridy = 4;
        form.add(lblSaldoActual, g);
        g.gridy = 5;
        g.insets = new Insets(10, 0, 2, 0);
        form.add(lblDestino, g);
        g.gridy = 6;
        g.insets = new Insets(0, 0, 2, 0);
        form.add(cmbDestino, g);
        g.gridy = 7;
        g.insets = new Insets(10, 0, 2, 0);
        form.add(Componentes.etiqueta("Importe", Estilo.PEQUENA, Estilo.TENUE), g);
        g.gridy = 8;
        g.insets = new Insets(0, 0, 2, 0);
        form.add(txtImporte, g);
        g.gridy = 9;
        g.insets = new Insets(18, 0, 0, 0);
        form.add(btnProcesar, g);
        g.gridy = 10;
        g.weighty = 1;
        form.add(new JLabel(), g);
        cmbCuenta.setFont(Estilo.NORMAL);
        cmbDestino.setFont(Estilo.NORMAL);

        comprobante.add(lblTituloComp, BorderLayout.NORTH);
        JPanel centro = new JPanel(new GridLayout(2, 1));
        centro.setOpaque(false);
        centro.add(lblNuevoSaldo);
        centro.add(lblDetalle);
        comprobante.add(centro, BorderLayout.CENTER);

        add(form);
        add(comprobante);

        btnDeposito.addActionListener(e -> seleccionar("DEPOSITO"));
        btnRetiro.addActionListener(e -> seleccionar("RETIRO"));
        btnTransferencia.addActionListener(e -> seleccionar("TRANSFERENCIA"));
        seleccionar("DEPOSITO");
    }

    /** Cambia el tipo de operación y ajusta colores y campos visibles. */
    public final void seleccionar(String op) {
        operacion = op;
        Color color = Estilo.colorOperacion(op);
        btnDeposito.setBackground(op.equals("DEPOSITO") ? Estilo.VERDE : Estilo.PANEL2);
        btnRetiro.setBackground(op.equals("RETIRO") ? Estilo.ROJO : Estilo.PANEL2);
        btnTransferencia.setBackground(op.equals("TRANSFERENCIA") ? Estilo.AZUL : Estilo.PANEL2);
        for (JButton b : new JButton[]{btnDeposito, btnRetiro, btnTransferencia}) {
            b.setForeground(b.getBackground() == Estilo.PANEL2 ? Estilo.TEXTO : Estilo.FONDO);
        }
        boolean transf = op.equals("TRANSFERENCIA");
        lblDestino.setVisible(transf);
        cmbDestino.setVisible(transf);
        btnProcesar.setBackground(color);
        btnProcesar.setText(switch (op) {
            case "DEPOSITO" -> "Procesar depósito";
            case "RETIRO" -> "Procesar retiro";
            default -> "Procesar transferencia";
        });
        revalidate();
    }

    public void setCuentas(List<Cuenta> cuentas) {
        Object sel = cmbCuenta.getSelectedItem();
        cmbCuenta.removeAllItems();
        cmbDestino.removeAllItems();
        for (Cuenta c : cuentas) {
            if (c.isActiva()) {
                cmbCuenta.addItem(c);
                cmbDestino.addItem(c);
            }
        }
        if (sel instanceof Cuenta s) {
            for (int i = 0; i < cmbCuenta.getItemCount(); i++) {
                if (cmbCuenta.getItemAt(i).getCodigo().equals(s.getCodigo())) {
                    cmbCuenta.setSelectedIndex(i);
                }
            }
        }
        actualizarSaldoSeleccion();
    }

    /** Muestra el saldo de la cuenta elegida y propone un destino de la misma moneda. */
    public void actualizarSaldoSeleccion() {
        Cuenta c = getCuenta();
        lblSaldoActual.setText(c == null ? " " : "Saldo disponible: " + Estilo.dinero(c.getSaldo()) + " " + c.getMoneda());
        Cuenta d = getDestino();
        if (c != null && (d == null || d.getCodigo().equals(c.getCodigo()) || !d.getCodigoMoneda().equals(c.getCodigoMoneda()))) {
            for (int i = 0; i < cmbDestino.getItemCount(); i++) {
                Cuenta x = cmbDestino.getItemAt(i);
                if (!x.getCodigo().equals(c.getCodigo()) && x.getCodigoMoneda().equals(c.getCodigoMoneda())) {
                    cmbDestino.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public void mostrarComprobante(ResultadoOperacion r, String mensaje) {
        Color color = Estilo.colorOperacion(r.getOperacion());
        lblTituloComp.setText("Comprobante · " + r.getOperacion() + " · Mov. N.º " + r.getNumeroMovimiento());
        lblNuevoSaldo.setForeground(color);
        Componentes.contar(lblNuevoSaldo, r.getSaldoActual(), r.getMoneda());
        lblDetalle.setForeground(Estilo.TEXTO);
        lblDetalle.setText("<html>" + mensaje + "<br><br>"
                + "Cuenta: <b>" + r.getCuenta() + "</b>" + (r.getCuentaDestino() != null ? " → <b>" + r.getCuentaDestino() + "</b>" : "")
                + "<br>Importe: <b>" + Estilo.dinero(r.getImporte()) + " " + r.getMoneda() + "</b>"
                + "<br>Saldo anterior: " + Estilo.dinero(r.getSaldoAnterior())
                + "<br>Empleado: " + r.getEmpleado() + " · Fecha: " + r.getFecha() + "</html>");
        Componentes.destello(comprobante, color);
        txtImporte.setText("");
    }

    public void mostrarError(String m) {
        lblTituloComp.setText("No se pudo procesar");
        lblNuevoSaldo.setForeground(Estilo.ROJO);
        lblNuevoSaldo.setText("Operación rechazada");
        lblDetalle.setForeground(Estilo.ROJO);
        lblDetalle.setText("<html>" + m + "</html>");
        Componentes.destello(comprobante, Estilo.ROJO);
        Componentes.sacudir(comprobante);
    }

    public void cargando(boolean si) {
        btnProcesar.setEnabled(!si);
        if (si) {
            btnProcesar.setText("Procesando…");
        } else {
            seleccionar(operacion);
        }
    }

    public String getOperacion() {
        return operacion;
    }

    public Cuenta getCuenta() {
        return (Cuenta) cmbCuenta.getSelectedItem();
    }

    public Cuenta getDestino() {
        return (Cuenta) cmbDestino.getSelectedItem();
    }

    public String getImporteTexto() {
        return txtImporte.getText().trim().replace(',', '.');
    }

    public JButton getBtnProcesar() {
        return btnProcesar;
    }

    public JComboBox<Cuenta> getCmbCuenta() {
        return cmbCuenta;
    }
}
