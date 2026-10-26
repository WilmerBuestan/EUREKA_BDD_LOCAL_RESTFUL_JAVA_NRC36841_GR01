package ec.edu.monster.modelo;

/**
 * Respuesta de un inicio de sesión exitoso.
 *
 * @author Grupo 1 - NRC 36841
 */
public class RespuestaLogin {

    /** Token Bearer de la sesión */
    private String token;
    /** Usuario autenticado */
    private String usuario;
    /** Nombre del empleado */
    private String nombreCompleto;
    /** Código del empleado */
    private String codigoEmpleado;
    /** Fecha y hora de expiración */
    private String expira;
    /** Minutos de vigencia del token */
    private int minutosVigencia;

    public RespuestaLogin() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }

    public String getExpira() {
        return expira;
    }

    public void setExpira(String expira) {
        this.expira = expira;
    }

    public int getMinutosVigencia() {
        return minutosVigencia;
    }

    public void setMinutosVigencia(int minutosVigencia) {
        this.minutosVigencia = minutosVigencia;
    }

}
