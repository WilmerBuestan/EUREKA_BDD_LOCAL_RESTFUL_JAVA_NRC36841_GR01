<%-- Vista: depósito, retiro y transferencia --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>EurekaBank Monster - Operaciones</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body class="op-${tipo}">
<%@include file="cabecera.jspf"%>
<main class="dos-col">
    <section class="tarjeta">
        <nav class="tipos">
            <a href="operaciones?tipo=deposito" class="op-deposito ${tipo == 'deposito' ? 'activa' : ''}">⬇ Depósito</a>
            <a href="operaciones?tipo=retiro" class="op-retiro ${tipo == 'retiro' ? 'activa' : ''}">⬆ Retiro</a>
            <a href="operaciones?tipo=transferencia" class="op-transferencia ${tipo == 'transferencia' ? 'activa' : ''}">⇄ Transferencia</a>
        </nav>
        <form method="post" action="operaciones" style="display:grid;gap:8px"
              onsubmit="return confirm('¿Confirma la operación de ' + this.importe.value + '?')">
            <input type="hidden" name="tipo" value="${tipo}">
            <label for="cuenta">${tipo == 'transferencia' ? 'Cuenta origen' : 'Cuenta'}</label>
            <select id="cuenta" name="cuenta" required>
                <c:forEach var="k" items="${cuentas}">
                    <c:if test="${k.activa}">
                        <option value="${k.codigo}" data-moneda="${k.codigoMoneda}" ${k.codigo == cuentaSel ? 'selected' : ''}>${k.codigo} · <c:out value="${k.cliente}"/> · <fmt:formatNumber value="${k.saldo}" pattern="#,##0.00"/> ${k.moneda}</option>
                    </c:if>
                </c:forEach>
            </select>
            <c:if test="${tipo == 'transferencia'}">
                <label for="destino">Cuenta destino</label>
                <select id="destino" name="destino" required>
                    <c:forEach var="k" items="${cuentas}" varStatus="st">
                        <c:if test="${k.activa}">
                            <option value="${k.codigo}" data-moneda="${k.codigoMoneda}" ${k.codigo == destinoSel ? 'selected' : ''}>${k.codigo} · <c:out value="${k.cliente}"/> (${k.moneda})</option>
                        </c:if>
                    </c:forEach>
                </select>
            </c:if>
            <label for="importe">Importe</label>
            <input id="importe" name="importe" inputmode="decimal" value="<c:out value='${importe}'/>" placeholder="Ej. 150.50" required autofocus>
            <button class="btn" type="submit" style="margin-top:10px">
                <c:choose>
                    <c:when test="${tipo == 'retiro'}">Procesar retiro</c:when>
                    <c:when test="${tipo == 'transferencia'}">Procesar transferencia</c:when>
                    <c:otherwise>Procesar depósito</c:otherwise>
                </c:choose>
            </button>
        </form>
        <c:if test="${not empty error}"><div class="msg error">✖ <c:out value="${error}"/></div></c:if>
    </section>

    <section class="tarjeta">
        <h2>🧾 Comprobante</h2>
        <c:choose>
            <c:when test="${not empty resultado}">
                <div class="comprobante op-${resultado.operacion}">
                    <div style="color:var(--tenue)">${resultado.operacion} · Movimiento N.º ${resultado.numeroMovimiento}</div>
                    <div class="num"><span class="contar" data-valor="${resultado.saldoActual}">0.00</span> ${resultado.moneda}</div>
                    <div>Nuevo saldo de la cuenta ${resultado.cuenta}</div>
                    <dl>
                        <dt>Cuenta</dt><dd>${resultado.cuenta}<c:if test="${not empty resultado.cuentaDestino}"> → ${resultado.cuentaDestino}</c:if></dd>
                        <dt>Importe</dt><dd><b><fmt:formatNumber value="${resultado.importe}" pattern="#,##0.00"/> ${resultado.moneda}</b></dd>
                        <dt>Saldo anterior</dt><dd><fmt:formatNumber value="${resultado.saldoAnterior}" pattern="#,##0.00"/></dd>
                        <dt>Empleado</dt><dd>${resultado.empleado}</dd>
                        <dt>Fecha</dt><dd>${resultado.fecha}</dd>
                    </dl>
                    <c:if test="${not empty aviso}"><div class="msg ok">✔ <c:out value="${aviso}"/></div></c:if>
                </div>
            </c:when>
            <c:otherwise>
                <div class="comprobante vacio">Seleccione la cuenta, ingrese el importe y procese la operación.<br>Aquí aparecerá el comprobante.</div>
            </c:otherwise>
        </c:choose>
    </section>
</main>
<%@include file="pie.jspf"%>
<script>
    // Propone como destino una cuenta distinta y de la misma moneda que la cuenta origen
    const origen = document.getElementById("cuenta"), destino = document.getElementById("destino");
    function proponerDestino() {
        if (!destino) return;
        const o = origen.selectedOptions[0], d = destino.selectedOptions[0];
        if (d && d.value !== o.value && d.dataset.moneda === o.dataset.moneda) return;
        const x = [...destino.options].find(op => op.value !== o.value && op.dataset.moneda === o.dataset.moneda);
        if (x) destino.value = x.value;
    }
    origen.addEventListener("change", proponerDestino);
    proponerDestino();
</script>
</body>
</html>
