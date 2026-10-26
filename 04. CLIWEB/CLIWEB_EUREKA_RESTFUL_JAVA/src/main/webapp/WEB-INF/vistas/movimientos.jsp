<%-- Vista: estado de cuenta (saldo y movimientos) --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>EurekaBank Monster - Movimientos</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<%@include file="cabecera.jspf"%>
<main>
    <section class="tarjeta">
        <form method="get" action="movimientos" class="barra-busqueda">
            <select name="cuenta" onchange="this.form.submit()">
                <option value="">— Seleccione una cuenta —</option>
                <c:forEach var="k" items="${cuentas}">
                    <option value="${k.codigo}" ${k.codigo == cuentaSel ? 'selected' : ''}>${k.codigo} · <c:out value="${k.cliente}"/> (${k.moneda})</option>
                </c:forEach>
            </select>
            <button class="btn" type="submit">Consultar</button>
        </form>
        <c:if test="${not empty error}"><div class="msg error">✖ <c:out value="${error}"/></div></c:if>
        <c:if test="${not empty cuenta}">
            <div style="margin-top:8px">
                <div style="color:var(--tenue)">Cuenta <b style="color:var(--texto)">${cuenta.codigo}</b> · <c:out value="${cuenta.cliente}"/> · Sucursal <c:out value="${cuenta.sucursal}"/> · <span class="estado-${cuenta.estado}">${cuenta.estado}</span></div>
                <div class="saldo-grande"><span class="contar" data-valor="${cuenta.saldo}">0.00</span> ${cuenta.moneda}</div>
                <div style="color:var(--tenue);font-size:13px">Apertura ${cuenta.fechaCreacion} · ${movimientos.size()} movimiento(s)</div>
            </div>
        </c:if>
    </section>
    <c:if test="${not empty cuenta}">
        <section class="tarjeta">
            <h2>📄 Movimientos (del más reciente al más antiguo)</h2>
            <c:choose>
                <c:when test="${empty movimientos}"><div class="vacio-hist">La cuenta no tiene movimientos</div></c:when>
                <c:otherwise>
                    <table>
                        <thead><tr><th>N.º</th><th>Fecha</th><th>Tipo</th><th class="der">Importe</th><th>Empleado</th><th>Referencia</th></tr></thead>
                        <tbody>
                        <c:forEach var="m" items="${movimientos}" varStatus="st">
                            <tr style="animation-delay:${st.index < 15 ? st.index * 40 : 0}ms">
                                <td>${m.numero}</td>
                                <td>${m.fecha}</td>
                                <td><c:out value="${m.tipo}"/></td>
                                <td class="der ${m.ingreso ? 'ing' : 'sal'}">${m.ingreso ? '+' : '−'}<fmt:formatNumber value="${m.importe}" pattern="#,##0.00"/></td>
                                <td>${m.empleado}</td>
                                <td>${m.referencia}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </section>
    </c:if>
</main>
<%@include file="pie.jspf"%>
</body>
</html>
