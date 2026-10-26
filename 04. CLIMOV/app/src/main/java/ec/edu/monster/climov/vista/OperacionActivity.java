package ec.edu.monster.climov.vista;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.climov.R;
import ec.edu.monster.climov.controlador.ControladorBanco;
import ec.edu.monster.climov.modelo.Cuenta;
import ec.edu.monster.climov.modelo.ExcepcionCliente;
import ec.edu.monster.climov.modelo.ResultadoOperacion;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Vista de operaciones: depósito, retiro y transferencia con comprobante.
 *
 * @author Grupo 1 - NRC 36841
 */
public class OperacionActivity extends AppCompatActivity {

    private final ControladorBanco controlador = new ControladorBanco();
    private final List<Cuenta> activas = new ArrayList<>();
    private String tipo = "deposito";
    private Button btnDeposito, btnRetiro, btnTransferencia, btnProcesar;
    private Spinner spnCuenta, spnDestino;
    private TextView lblDestino, lblSaldoDisponible, lblTitulo, lblNuevoSaldo, lblDetalle;
    private EditText txtImporte;
    private View progreso, tarjetaComprobante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operacion);
        Pantalla.ajustarTeclado(this);
        findViewById(R.id.imgMini).setClipToOutline(true);
        ((TextView) findViewById(R.id.lblTitulo)).setText("Operaciones");
        ((TextView) findViewById(R.id.lblSubtitulo)).setText("Empleado: " + controlador.usuario());
        Button volver = findViewById(R.id.btnCabecera);
        volver.setText("Volver");
        volver.setBackgroundTintList(ColorStateList.valueOf(Colores.PANEL2));
        volver.setTextColor(Colores.TEXTO);
        volver.setOnClickListener(v -> finish());

        btnDeposito = findViewById(R.id.btnTipoDeposito);
        btnRetiro = findViewById(R.id.btnTipoRetiro);
        btnTransferencia = findViewById(R.id.btnTipoTransferencia);
        btnProcesar = findViewById(R.id.btnProcesar);
        spnCuenta = findViewById(R.id.spnCuenta);
        spnDestino = findViewById(R.id.spnDestino);
        lblDestino = findViewById(R.id.lblDestino);
        lblSaldoDisponible = findViewById(R.id.lblSaldoDisponible);
        lblTitulo = findViewById(R.id.lblTituloComprobante);
        lblNuevoSaldo = findViewById(R.id.lblNuevoSaldo);
        lblDetalle = findViewById(R.id.lblDetalle);
        txtImporte = findViewById(R.id.txtImporte);
        progreso = findViewById(R.id.progreso);
        tarjetaComprobante = findViewById(R.id.tarjetaComprobante);

        btnDeposito.setOnClickListener(v -> seleccionar("deposito"));
        btnRetiro.setOnClickListener(v -> seleccionar("retiro"));
        btnTransferencia.setOnClickListener(v -> seleccionar("transferencia"));
        btnProcesar.setOnClickListener(v -> confirmar());
        spnCuenta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                cuentaElegida();
            }

            @Override
            public void onNothingSelected(AdapterView<?> p) {
            }
        });

        String t = getIntent().getStringExtra("tipo");
        seleccionar(t == null ? "deposito" : t);
        Animaciones.aparecer(findViewById(R.id.tarjetaFormulario), 100);
        cargarCuentas(getIntent().getStringExtra("cuenta"));
    }

    private void cargarCuentas(String preseleccion) {
        controlador.listarCuentas(cuentas -> {
            activas.clear();
            for (Cuenta c : cuentas) {
                if (c.activa()) {
                    activas.add(c);
                }
            }
            ArrayAdapter<Cuenta> ad = new ArrayAdapter<>(this, R.layout.item_spinner, activas);
            spnCuenta.setAdapter(ad);
            spnDestino.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner, activas));
            String sel = preseleccion != null ? preseleccion : seleccionada() == null ? null : seleccionada().codigo;
            for (int i = 0; i < activas.size(); i++) {
                if (activas.get(i).codigo.equals(sel)) {
                    spnCuenta.setSelection(i);
                }
            }
            cuentaElegida();
        }, this::fallo);
    }

    private Cuenta seleccionada() {
        return (Cuenta) spnCuenta.getSelectedItem();
    }

    /** Muestra el saldo y propone un destino distinto con la misma moneda. */
    private void cuentaElegida() {
        Cuenta c = seleccionada();
        if (c == null) {
            return;
        }
        lblSaldoDisponible.setText("Saldo disponible: " + Animaciones.formato(c.saldo) + " " + c.moneda);
        Cuenta d = (Cuenta) spnDestino.getSelectedItem();
        if (d == null || d.codigo.equals(c.codigo) || !d.codigoMoneda.equals(c.codigoMoneda)) {
            for (int i = 0; i < activas.size(); i++) {
                Cuenta x = activas.get(i);
                if (!x.codigo.equals(c.codigo) && x.codigoMoneda.equals(c.codigoMoneda)) {
                    spnDestino.setSelection(i);
                    break;
                }
            }
        }
    }

    private void seleccionar(String t) {
        tipo = t;
        int color = Colores.de(t);
        pintar(btnDeposito, t.equals("deposito"), Colores.VERDE);
        pintar(btnRetiro, t.equals("retiro"), Colores.ROJO);
        pintar(btnTransferencia, t.equals("transferencia"), Colores.AZUL);
        boolean transf = t.equals("transferencia");
        lblDestino.setVisibility(transf ? View.VISIBLE : View.GONE);
        spnDestino.setVisibility(transf ? View.VISIBLE : View.GONE);
        btnProcesar.setBackgroundTintList(ColorStateList.valueOf(color));
        btnProcesar.setText(t.equals("deposito") ? "Procesar depósito" : t.equals("retiro") ? "Procesar retiro" : "Procesar transferencia");
    }

    private static void pintar(Button b, boolean activo, int color) {
        b.setBackgroundTintList(ColorStateList.valueOf(activo ? color : Colores.PANEL2));
        b.setTextColor(activo ? Colores.OSCURO : Colores.TEXTO);
    }

    private void confirmar() {
        Cuenta c = seleccionada();
        String texto = txtImporte.getText().toString().trim().replace(',', '.');
        if (c == null) {
            error("Seleccione una cuenta");
            return;
        }
        if (!texto.matches("\\d+(\\.\\d{1,2})?") || Double.parseDouble(texto) <= 0) {
            error("Ingrese un importe mayor que cero con máximo 2 decimales (ej. 150.50)");
            return;
        }
        double importe = Double.parseDouble(texto);
        Cuenta d = tipo.equals("transferencia") ? (Cuenta) spnDestino.getSelectedItem() : null;
        if (tipo.equals("transferencia") && (d == null || d.codigo.equals(c.codigo))) {
            error("Seleccione una cuenta destino diferente de la cuenta origen");
            return;
        }
        String detalle = String.format(Locale.US, "%s de %s %s%s", tipo.equals("deposito") ? "Depósito" : tipo.equals("retiro") ? "Retiro" : "Transferencia",
                Animaciones.formato(importe), c.moneda, d != null ? "\nde " + c.codigo + " a " + d.codigo : "\ncuenta " + c.codigo);
        new AlertDialog.Builder(this)
                .setTitle("Confirmar operación")
                .setMessage(detalle)
                .setPositiveButton("Confirmar", (dlg, w) -> procesar(c.codigo, d == null ? null : d.codigo, importe))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void procesar(String cuenta, String destino, double importe) {
        cargando(true);
        controlador.operar(tipo, cuenta, destino, importe, r -> {
            cargando(false);
            comprobante(r);
            txtImporte.setText("");
            cargarCuentas(cuenta); // refresca los saldos de las listas
        }, e -> {
            cargando(false);
            fallo(e);
        });
    }

    private void comprobante(ResultadoOperacion r) {
        int color = Colores.de(r.operacion);
        lblTitulo.setText(r.operacion + " · Movimiento N.º " + r.numeroMovimiento);
        lblTitulo.setTextColor(color);
        lblNuevoSaldo.setTextColor(color);
        Animaciones.contar(lblNuevoSaldo, r.saldoActual, r.moneda);
        lblDetalle.setTextColor(Colores.TEXTO);
        lblDetalle.setText(r.mensaje + "\n\nCuenta " + r.cuenta + (r.cuentaDestino != null ? " → " + r.cuentaDestino : "")
                + "\nImporte " + Animaciones.formato(r.importe) + " " + r.moneda
                + "\nSaldo anterior " + Animaciones.formato(r.saldoAnterior)
                + "\nEmpleado " + r.empleado + " · " + r.fecha);
        Animaciones.latido(tarjetaComprobante);
    }

    private void error(String m) {
        lblTitulo.setText("No se pudo procesar");
        lblTitulo.setTextColor(Colores.ROJO);
        lblNuevoSaldo.setText("✖");
        lblNuevoSaldo.setTextColor(Colores.ROJO);
        lblDetalle.setTextColor(Colores.ROJO);
        lblDetalle.setText(m);
        Animaciones.sacudir(tarjetaComprobante);
    }

    private void fallo(ExcepcionCliente e) {
        if (e.sesionExpirada()) {
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("aviso", "Su sesión expiró. Inicie sesión nuevamente.");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
            return;
        }
        error(e.getMessage());
    }

    private void cargando(boolean si) {
        btnProcesar.setEnabled(!si);
        progreso.setVisibility(si ? View.VISIBLE : View.GONE);
    }
}
