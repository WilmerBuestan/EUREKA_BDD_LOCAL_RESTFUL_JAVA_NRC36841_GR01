package ec.edu.monster.vista;

import ec.edu.monster.modelo.Cuenta;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Vista de cuentas: tarjetas de resumen animadas y tabla con todas las cuentas.
 *
 * @author Grupo 1 - NRC 36841
 */
public class PanelCuentas extends JPanel {

    private final JLabel lblTotal = Componentes.etiqueta("0", Estilo.DINERO, Estilo.TEXTO);
    private final JLabel lblDolares = Componentes.etiqueta("0.00", Estilo.DINERO, Estilo.VERDE);
    private final JLabel lblActivas = Componentes.etiqueta("0", Estilo.DINERO, Estilo.CIAN);
    private final DefaultTableModel modelo = Componentes.modelo("Cuenta", "Titular", "Moneda", "Sucursal", "Saldo", "Estado", "Apertura");
    private final JTable tabla = Componentes.tabla(modelo);
    private final JButton btnActualizar = Componentes.boton("Actualizar", Estilo.CIAN);

    public PanelCuentas() {
        super(new BorderLayout(0, 16));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel resumen = new JPanel(new GridLayout(1, 3, 16, 0));
        resumen.setOpaque(false);
        resumen.add(tarjeta("Cuentas registradas", lblTotal, Estilo.AZUL));
        resumen.add(tarjeta("Saldo total en Dólares (USD)", lblDolares, Estilo.VERDE));
        resumen.add(tarjeta("Cuentas activas", lblActivas, Estilo.CIAN));

        Componentes.Tarjeta caja = new Componentes.Tarjeta(new BorderLayout(0, 10));
        JPanel titulo = new JPanel(new BorderLayout());
        titulo.setOpaque(false);
        titulo.add(Componentes.etiqueta("Cuentas de EurekaBank", Estilo.SUBTITULO, Estilo.TEXTO), BorderLayout.WEST);
        titulo.add(btnActualizar, BorderLayout.EAST);
        caja.add(titulo, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createEmptyBorder());
        caja.add(sp, BorderLayout.CENTER);

        // Saldo alineado a la derecha y estado en color
        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        tabla.getColumnModel().getColumn(4).setCellRenderer(derecha);
        tabla.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foco, int f, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foco, f, c);
                comp.setForeground("ACTIVO".equals(v) ? Estilo.VERDE : Estilo.ROJO);
                return comp;
            }
        });
        tabla.getColumnModel().getColumn(1).setPreferredWidth(260);

        add(resumen, BorderLayout.NORTH);
        add(caja, BorderLayout.CENTER);
    }

    private static Componentes.Tarjeta tarjeta(String titulo, JLabel valor, Color color) {
        Componentes.Tarjeta t = new Componentes.Tarjeta(new GridLayout(2, 1));
        t.setColorBorde(color);
        t.add(Componentes.etiqueta(titulo, Estilo.PEQUENA, Estilo.TENUE));
        t.add(valor);
        return t;
    }

    public void mostrar(List<Cuenta> cuentas) {
        modelo.setRowCount(0);
        double dolares = 0;
        int activas = 0;
        for (Cuenta c : cuentas) {
            modelo.addRow(new Object[]{c.getCodigo(), c.getCliente(), c.getMoneda(), c.getSucursal(),
                Estilo.dinero(c.getSaldo()), c.getEstado(), c.getFechaCreacion()});
            if (c.isActiva()) {
                activas++;
                if (c.getMoneda() != null && c.getMoneda().startsWith("D")) {
                    dolares += c.getSaldo();
                }
            }
        }
        lblTotal.setText(String.valueOf(cuentas.size()));
        lblActivas.setText(String.valueOf(activas));
        Componentes.contar(lblDolares, dolares, "USD");
    }

    public JButton getBtnActualizar() {
        return btnActualizar;
    }
}
