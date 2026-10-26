package ec.edu.monster.climov.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.climov.R;
import ec.edu.monster.climov.controlador.ControladorBanco;
import ec.edu.monster.climov.modelo.Cuenta;
import ec.edu.monster.climov.modelo.ExcepcionCliente;
import java.util.List;

/**
 * Vista principal: saldo total y listado de cuentas.
 *
 * @author Grupo 1 - NRC 36841
 */
public class MenuActivity extends AppCompatActivity {

    private final ControladorBanco controlador = new ControladorBanco();
    private LinearLayout listaCuentas;
    private TextView lblTotalUsd, lblOtrasMonedas, lblTotalCuentas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Pantalla.ajustarTeclado(this);
        findViewById(R.id.imgMini).setClipToOutline(true);
        ((TextView) findViewById(R.id.lblSubtitulo)).setText("Empleado: " + controlador.usuario());
        listaCuentas = findViewById(R.id.listaCuentas);
        lblTotalUsd = findViewById(R.id.lblTotalUsd);
        lblOtrasMonedas = findViewById(R.id.lblOtrasMonedas);
        lblTotalCuentas = findViewById(R.id.lblTotalCuentas);
        ((TextView) findViewById(R.id.lblServidor)).setText("Servidor: " + controlador.urlServidor());

        Button salir = findViewById(R.id.btnCabecera);
        salir.setOnClickListener(v -> confirmarSalida());
        findViewById(R.id.btnOperar).setOnClickListener(v ->
                startActivity(new Intent(this, OperacionActivity.class)));

        String bienvenida = getIntent().getStringExtra("bienvenida");
        if (bienvenida != null) {
            Toast.makeText(this, bienvenida, Toast.LENGTH_SHORT).show();
        }
        Animaciones.aparecer(findViewById(R.id.tarjetaResumen), 100);
    }

    /** Cada vez que se vuelve a esta pantalla se recargan los saldos. */
    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    private void cargar() {
        controlador.listarCuentas(this::mostrar, this::fallo);
    }

    private void mostrar(List<Cuenta> cuentas) {
        double dolares = 0;
        double otras = 0;
        int activas = 0;
        listaCuentas.removeAllViews();
        LayoutInflater inf = LayoutInflater.from(this);
        int i = 0;
        for (Cuenta c : cuentas) {
            if (c.activa()) {
                activas++;
                if (c.moneda != null && c.moneda.startsWith("D")) {
                    dolares += c.saldo;
                } else {
                    otras += c.saldo;
                }
            }
            View item = inf.inflate(R.layout.item_cuenta, listaCuentas, false);
            ((TextView) item.findViewById(R.id.lblCodigo)).setText(c.codigo);
            ((TextView) item.findViewById(R.id.lblCliente)).setText(c.cliente + " · " + c.sucursal);
            TextView saldo = item.findViewById(R.id.lblSaldo);
            saldo.setText(Animaciones.formato(c.saldo) + " " + c.moneda);
            TextView estado = item.findViewById(R.id.lblEstado);
            estado.setText(c.estado);
            int color = c.activa() ? Colores.VERDE : Colores.ROJO;
            estado.setTextColor(color);
            saldo.setTextColor(c.activa() ? Colores.TEXTO : Colores.TENUE);
            item.findViewById(R.id.barra).setBackgroundColor(color);
            item.setOnClickListener(v -> {
                Intent in = new Intent(this, CuentaActivity.class);
                in.putExtra("cuenta", c.codigo);
                startActivity(in);
            });
            listaCuentas.addView(item);
            Animaciones.aparecer(item, 60L * i++);
        }
        Animaciones.contar(lblTotalUsd, dolares, "USD");
        lblOtrasMonedas.setVisibility(otras > 0 ? View.VISIBLE : View.GONE);
        if (otras > 0) {
            Animaciones.contar(lblOtrasMonedas, otras, "en otras monedas");
        }
        lblTotalCuentas.setText(cuentas.size() + " cuentas · " + activas + " activas");
    }

    private void fallo(ExcepcionCliente e) {
        if (e.sesionExpirada()) {
            volverAlLogin("Su sesión expiró. Inicie sesión nuevamente.");
            return;
        }
        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void confirmarSalida() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Desea cerrar la sesión?")
                .setPositiveButton("Sí", (d, w) -> controlador.cerrarSesion(ok -> volverAlLogin("Sesión cerrada correctamente")))
                .setNegativeButton("No", null)
                .show();
    }

    private void volverAlLogin(String aviso) {
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("aviso", aviso);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        confirmarSalida();
    }
}
