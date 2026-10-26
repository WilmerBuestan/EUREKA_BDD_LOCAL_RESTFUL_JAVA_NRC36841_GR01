package ec.edu.monster.servicio;

import ec.edu.monster.modelo.EventoServidor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Registro de eventos y contadores para el panel de monitoreo del servidor
 * (qué cliente pidió qué y si tuvo éxito). Se guarda en memoria. Singleton.
 *
 * @author Grupo 1 - NRC 36841
 */
public final class ServicioMonitoreo {

    private static final ServicioMonitoreo INSTANCIA = new ServicioMonitoreo();
    private static final int MAX_EVENTOS = 40;
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Deque<EventoServidor> eventos = new ConcurrentLinkedDeque<>();
    private final Map<String, AtomicLong> porOperacion = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> porCliente = new ConcurrentHashMap<>();
    private final AtomicLong totalOperaciones = new AtomicLong();
    private final AtomicLong totalPeticiones = new AtomicLong();
    private final LocalDateTime inicio = LocalDateTime.now();

    private ServicioMonitoreo() {
    }

    public static ServicioMonitoreo getInstancia() {
        return INSTANCIA;
    }

    public void registrarEvento(String tipo, String cliente, String detalle, boolean exito) {
        EventoServidor e = new EventoServidor();
        e.setHora(LocalDateTime.now().format(HORA));
        e.setTipo(tipo);
        e.setCliente(ServicioAutenticacion.normalizarCliente(cliente));
        e.setDetalle(detalle);
        e.setExito(exito);
        eventos.addFirst(e);
        while (eventos.size() > MAX_EVENTOS) {
            eventos.pollLast();
        }
        totalPeticiones.incrementAndGet();
        porCliente.computeIfAbsent(e.getCliente(), k -> new AtomicLong()).incrementAndGet();
    }

    /** Cuenta una operación bancaria exitosa (DEPOSITO, RETIRO, TRANSFERENCIA). */
    public void registrarOperacion(String operacion) {
        totalOperaciones.incrementAndGet();
        porOperacion.computeIfAbsent(operacion, k -> new AtomicLong()).incrementAndGet();
    }

    public List<EventoServidor> ultimosEventos(int n) {
        List<EventoServidor> lista = new ArrayList<>();
        Iterator<EventoServidor> it = eventos.iterator();
        while (it.hasNext() && lista.size() < n) {
            lista.add(it.next());
        }
        return lista;
    }

    public Map<String, Long> operacionesPorTipo() {
        Map<String, Long> m = new LinkedHashMap<>();
        for (String k : List.of("DEPOSITO", "RETIRO", "TRANSFERENCIA")) {
            AtomicLong v = porOperacion.get(k);
            m.put(k, v == null ? 0L : v.get());
        }
        return m;
    }

    public Map<String, Long> peticionesPorCliente() {
        Map<String, Long> m = new LinkedHashMap<>();
        porCliente.forEach((k, v) -> m.put(k, v.get()));
        return m;
    }

    public long getTotalOperaciones() {
        return totalOperaciones.get();
    }

    public long getTotalPeticiones() {
        return totalPeticiones.get();
    }

    public LocalDateTime getInicio() {
        return inicio;
    }
}
