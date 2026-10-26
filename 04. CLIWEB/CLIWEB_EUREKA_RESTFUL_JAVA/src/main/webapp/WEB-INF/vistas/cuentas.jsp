<%-- Vista: resumen y listado de cuentas --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>EurekaBank Monster - Cuentas</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<%@include file="cabecera.jspf"%>
<main>
    <c:if test="${not empty aviso}"><div class="msg ok">✔ <c:out value="${aviso}"/></div></c:if>
    <c:if test="${not empty error}"><div class="msg error">✖ <c:out value="${error}"/></div></c:if>
    <div class="resumen">
        <div class="tarjeta kpi k-azul"><div class="et">Cuentas registradas</div><div class="num">${cuentas.size()}</div></div>
        <div class="tarjeta kpi k-verde"><div class="et">Saldo total en Dólares (USD)</div><div class="num">$ <span class="contar" data-valor="${totalDolares}">0.00</span></div></div>
        <div class="tarjeta kpi k-cian"><div class="et">Cuentas activas</div><div class="num">${activas}</div></div>
    </div>
    <section class="tarjeta">
        <h2>🏦 Cuentas de EurekaBank</h2>
        <table>
            <thead><tr><th>Cuenta</th><th>Titular</th><th>Moneda</th><th>Sucursal</th><th class="der">Saldo</th><th>Estado</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="k" items="${cuentas}" varStatus="st">
                <tr style="animation-delay:${st.index * 50}ms">
                    <td><b>${k.codigo}</b></td>
                    <td><c:out value="${k.cliente}"/></td>
                    <td>${k.moneda}</td>
                    <td><c:out value="${k.sucursal}"/></td>
                    <td class="der"><fmt:formatNumber value="${k.saldo}" pattern="#,##0.00"/></td>
                    <td class="estado-${k.estado}">${k.estado}</td>
                    <td><a class="chip" href="movimientos?cuenta=${k.codigo}">Ver movimientos</a></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </section>
</main>
<%@include file="pie.jspf"%>
</body>
</html>
