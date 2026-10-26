package ec.edu.monster.climov.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.climov.R;
import ec.edu.monster.climov.controlador.ControladorBanco;
import ec.edu.monster.climov.modelo.Cuenta;
import ec.edu.monster.climov.modelo.ExcepcionCliente;
import ec.edu.monster.climov.modelo.Movimiento;
import java.util.List;

/**
 * Vista de estado de cuenta: saldo, accesos rápidos y movimientos.
 *
 * @author Grupo 1 - NRC 36841
 */
public class CuentaActivity extends AppCompatActivity {

    private final ControladorBanco controlador = new ControladorBanco();
    private String cuenta;
    private TextView lblCliente, lblSaldo, lblInfo;
    private LinearLayout lista;
    private View progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);
        Pantalla.ajustarTeclado(this);
        cuenta = getIntent().getStringExtra("cuenta");
        findViewById(R.id.imgMini).setClipToOutline(true);
        ((TextView) findViewById(R.id.lblTitulo)).setText("Cuenta " + cuenta);
        ((TextView) findViewById(R.id.lblSubtitulo)).setText("Estado de cuenta");
        Button volver = findViewById(R.id.btnCabecera);
        volver.setText("Volver");
        volver.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Colores.PANEL2));
        volver.setTextColor(Colores.TEXTO);
        volver.setOnClickListener(v -> finish());

        lblCliente = findViewById(R.id.lblCliente);
        lblSaldo = findViewById(R.id.lblSaldo);
        lblInfo = findViewById(R.id.lblInfo);
        lista = findViewById(R.id.listaMovimientos);
        progreso = findViewById(R.id.progreso);

        findViewById(R.id.btnDeposito).setOnClickListener(v -> operar("deposito"));
        findViewById(R.id.btnRetiro).setOnClickListener(v -> operar("retiro"));
        findViewById(R.id.btnTransferencia).setOnClickListener(v -> operar("transferencia"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        progreso.setVisibility(View.VISIBLE);
        controlador.estadoDeCuenta(cuenta, r -> mostrar((Cuenta) r[0], ControladorBanco.movimientos(r)), this::fallo);
    }

    private void operar(String tipo) {
        Intent i = new Intent(this, OperacionActivity.class);
        i.putExtra("tipo", tipo);
        i.putExtra("cuenta", cuenta);
        startActivity(i);
    }

    private void mostrar(Cuenta c, List<Movimiento> movimientos) {
        progreso.setVisibility(View.GONE);
        lblCliente.setText(c.cliente);
        lblSaldo.setTextColor(c.activa() ? Colores.VERDE : Colores.ROJO);
        Animaciones.contar(lblSaldo, c.saldo, c.moneda);
        Animaciones.latido(findViewById(R.id.tarjetaSaldo));
        lblInfo.setText("Sucursal " + c.sucursal + " · Apertura " + c.fechaCreacion + "\n" + c.estado + " · "
                + movimientos.size() + " movimiento(s)");
        boolean activa = c.activa();
        findViewById(R.id.btnDeposito).setEnabled(activa);
        findViewById(R.id.btnRetiro).setEnabled(activa);
        findViewById(R.id.btnTransferencia).setEnabled(activa);

        lista.removeAllViews();
        LayoutInflater inf = LayoutInflater.from(this);
        int i = 0;
        for (Movimiento m : movimientos) {
            View item = inf.inflate(R.layout.item_movimiento, lista, false);
            ((TextView) item.findViewById(R.id.lblNumero)).setText(String.valueOf(m.numero));
            ((TextView) item.findViewById(R.id.lblTipo)).setText(m.tipo);
            String info = m.fecha + " · Empl. " + m.empleado + (m.referencia != null ? " · Ref. " + m.referencia : "");
            ((TextView) item.findViewById(R.id.lblInfo)).setText(info);
            TextView imp = item.findViewById(R.id.lblImporte);
            imp.setText((m.ingreso() ? "+" : "−") + Animaciones.formato(m.importe));
            imp.setTextColor(m.ingreso() ? Colores.VERDE : Colores.ROJO);
            lista.addView(item);
            if (i < 12) {
                Animaciones.aparecer(item, 40L * i);
            }
            i++;
        }
        if (movimientos.isEmpty()) {
            TextView vacio = new TextView(this);
            vacio.setText("La cuenta no tiene movimientos");
            vacio.setTextColor(Colores.TENUE);
            lista.addView(vacio);
        }
    }

    private void fallo(ExcepcionCliente e) {
        progreso.setVisibility(View.GONE);
        if (e.sesionExpirada()) {
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("aviso", "Su sesión expiró. Inicie sesión nuevamente.");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return;
        }
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
