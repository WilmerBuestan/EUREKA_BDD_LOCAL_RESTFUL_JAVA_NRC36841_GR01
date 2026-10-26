package ec.edu.monster.modelo;

/**
 * Estado del servidor para el panel de monitoreo.
 *
 * @author Grupo 1 - NRC 36841
 */
public class EstadoServidor {

    /** ACTIVO */
    private String estado;
    /** CONECTADA / SIN CONEXION */
    private String baseDatos;
    /** Hora de arranque */
    private String inicio;
    /** Tiempo activo */
    private long segundosActivo;
    /** Sesiones vigentes */
    private int sesionesActivas;
    /** Depósitos, retiros y transferencias */
    private long totalOperaciones;
    /** Peticiones registradas */
    private long totalPeticiones;
    /** Conteo por tipo de operación */
    private java.util.Map<String, Long> operacionesPorTipo;
    /** Conteo por cliente */
    private java.util.Map<String, Long> peticionesPorCliente;
    /** Últimos eventos */
    private java.util.List<EventoServidor> ultimosEventos;

    /** URLs de la API con las IP de red del equipo (para configurar el celular) */
    private java.util.List<String> urlsRed;

    public java.util.List<String> getUrlsRed() {
        return urlsRed;
    }

    public void setUrlsRed(java.util.List<String> urlsRed) {
        this.urlsRed = urlsRed;
    }

    public EstadoServidor() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getBaseDatos() {
        return baseDatos;
    }

    public void setBaseDatos(String baseDatos) {
        this.baseDatos = baseDatos;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public long getSegundosActivo() {
        return segundosActivo;
    }

    public void setSegundosActivo(long segundosActivo) {
        this.segundosActivo = segundosActivo;
    }

    public int getSesionesActivas() {
        return sesionesActivas;
    }

    public void setSesionesActivas(int sesionesActivas) {
        this.sesionesActivas = sesionesActivas;
    }

    public long getTotalOperaciones() {
        return totalOperaciones;
    }

    public void setTotalOperaciones(long totalOperaciones) {
        this.totalOperaciones = totalOperaciones;
    }

    public long getTotalPeticiones() {
        return totalPeticiones;
    }

    public void setTotalPeticiones(long totalPeticiones) {
        this.totalPeticiones = totalPeticiones;
    }

    public java.util.Map<String, Long> getOperacionesPorTipo() {
        return operacionesPorTipo;
    }

    public void setOperacionesPorTipo(java.util.Map<String, Long> operacionesPorTipo) {
        this.operacionesPorTipo = operacionesPorTipo;
    }

    public java.util.Map<String, Long> getPeticionesPorCliente() {
        return peticionesPorCliente;
    }

    public void setPeticionesPorCliente(java.util.Map<String, Long> peticionesPorCliente) {
        this.peticionesPorCliente = peticionesPorCliente;
    }

    public java.util.List<EventoServidor> getUltimosEventos() {
        return ultimosEventos;
    }

    public void setUltimosEventos(java.util.List<EventoServidor> ultimosEventos) {
        this.ultimosEventos = ultimosEventos;
    }

}
