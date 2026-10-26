package ec.edu.monster.modelo;

import java.time.LocalDateTime;

/**
 * Sesión activa de un empleado, identificada por su token.
 *
 * @author Grupo 1 - NRC 36841
 */
public class Sesion {

    private final String token;
    private final Usuario usuario;
    private final String cliente;
    private final LocalDateTime inicio;
    private final LocalDateTime expira;

    public Sesion(String token, Usuario usuario, String cliente, LocalDateTime inicio, LocalDateTime expira) {
        this.token = token;
        this.usuario = usuario;
        this.cliente = cliente;
        this.inicio = inicio;
        this.expira = expira;
    }

    public boolean estaVigente() {
        return LocalDateTime.now().isBefore(expira);
    }

    public String getToken() {
        return token;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    /** Código del empleado que registra los movimientos de esta sesión. */
    public String getCodigoEmpleado() {
        return usuario.getCodigoEmpleado();
    }

    public String getCliente() {
        return cliente;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getExpira() {
        return expira;
    }
}
