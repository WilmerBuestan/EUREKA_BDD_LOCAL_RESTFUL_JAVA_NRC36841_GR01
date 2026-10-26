package ec.edu.monster.climov.modelo;

/**
 * Movimiento de una cuenta.
 *
 * @author Grupo 1 - NRC 36841
 */
public class Movimiento {

    public int numero;
    public String fecha;
    public String empleado;
    public String tipo;
    public String accion;
    public double importe;
    public String referencia;

    public boolean ingreso() {
        return "INGRESO".equalsIgnoreCase(accion);
    }
}
