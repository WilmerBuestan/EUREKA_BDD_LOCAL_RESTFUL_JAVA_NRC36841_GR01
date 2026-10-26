package ec.edu.monster.vista;

import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.Movimiento;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Vista de estado de cuenta: saldo y movimientos de la cuenta elegida.
 *
 * @author Grupo 1 - NRC 36841
 */
public class PanelMovimientos extends JPanel {

    private final JComboBox<Cuenta> cmbCuenta = new JComboBox<>();
    private final JButton btnConsultar = Componentes.boton("Consultar", Estilo.DORADO);
    private final JLabel lblTitular = Componentes.etiqueta("Seleccione una cuenta", Estilo.SUBTITULO, Estilo.TEXTO);
    private final JLabel lblSaldo = Componentes.etiqueta("—", Estilo.DINERO, Estilo.VERDE);
    private final JLabel lblInfo = Componentes.etiqueta(" ", Estilo.PEQUENA, Estilo.TENUE);
    private final DefaultTableModel modelo = Componentes.modelo("N.º", "Fecha", "Tipo", "Importe", "Empleado", "Referencia");
    private final JTable tabla = Componentes.tabla(modelo);

    public PanelMovimientos() {
        super(new BorderLayout(0, 16));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        barra.setOpaque(false);
        barra.add(Componentes.etiqueta("Cuenta:", Estilo.NORMAL, Estilo.TENUE));
        cmbCuenta.setFont(Estilo.NORMAL);
        cmbCuenta.setPreferredSize(Componentes.dim(420, 34));
        barra.add(cmbCuenta);
        barra.add(btnConsultar);

        Componentes.Tarjeta saldo = new Componentes.Tarjeta(new GridLayout(3, 1));
        saldo.setColorBorde(Estilo.DORADO);
        saldo.add(lblTitular);
        saldo.add(lblSaldo);
        saldo.add(lblInfo);

        JPanel norte = new JPanel(new BorderLayout(0, 14));
        norte.setOpaque(false);
        norte.add(barra, BorderLayout.NORTH);
        norte.add(saldo, BorderLayout.CENTER);

        Componentes.Tarjeta caja = new Componentes.Tarjeta(new BorderLayout(0, 10));
        caja.add(Componentes.etiqueta("Movimientos (del más reciente al más antiguo)", Estilo.SUBTITULO, Estilo.TEXTO), BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createEmptyBorder());
        caja.add(sp, BorderLayout.CENTER);
        tabla.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int f, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foco, f, c);
                setHorizontalAlignment(SwingConstants.RIGHT);
                comp.setForeground(String.valueOf(v).startsWith("+") ? Estilo.VERDE : Estilo.ROJO);
                return comp;
            }
        });
        tabla.getColumnModel().getColumn(0).setMaxWidth(60);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(200);

        add(norte, BorderLayout.NORTH);
        add(caja, BorderLayout.CENTER);
    }

    public void setCuentas(List<Cuenta> cuentas) {
        Object sel = cmbCuenta.getSelectedItem();
        cmbCuenta.removeAllItems();
        cuentas.forEach(cmbCuenta::addItem);
        if (sel instanceof Cuenta s) {
            for (int i = 0; i < cmbCuenta.getItemCount(); i++) {
                if (cmbCuenta.getItemAt(i).getCodigo().equals(s.getCodigo())) {
                    cmbCuenta.setSelectedIndex(i);
                }
            }
        }
    }

    public void mostrar(Cuenta c, List<Movimiento> movimientos) {
        lblTitular.setText(c.getCodigo() + " · " + c.getCliente());
        lblSaldo.setForeground(c.isActiva() ? Estilo.VERDE : Estilo.ROJO);
        Componentes.contar(lblSaldo, c.getSaldo(), c.getMoneda());
        lblInfo.setText("Sucursal " + c.getSucursal() + " · Apertura " + c.getFechaCreacion() + " · Estado "
                + c.getEstado() + " · " + movimientos.size() + " movimiento(s)");
        modelo.setRowCount(0);
        for (Movimiento m : movimientos) {
            modelo.addRow(new Object[]{m.getNumero(), m.getFecha(), m.getTipo(),
                (m.isIngreso() ? "+" : "-") + Estilo.dinero(m.getImporte()), m.getEmpleado(),
                m.getReferencia() == null ? "" : m.getReferencia()});
        }
    }

    public Cuenta getCuenta() {
        return (Cuenta) cmbCuenta.getSelectedItem();
    }

    public JButton getBtnConsultar() {
        return btnConsultar;
    }
}
