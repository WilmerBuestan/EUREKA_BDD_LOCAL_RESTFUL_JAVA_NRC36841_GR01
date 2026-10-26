<%-- Vista: CRUD de clientes --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>EurekaBank Monster - Clientes</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<%@include file="cabecera.jspf"%>
<main class="col-form">
    <section class="tarjeta">
        <form method="get" action="clientes" class="barra-busqueda">
            <input name="buscar" value="<c:out value='${buscar}'/>" placeholder="Buscar por nombre, apellido, cédula o código">
            <button class="btn" type="submit">Buscar</button>
        </form>
        <table>
            <thead><tr><th>Código</th><th>Nombre</th><th>Apellidos</th><th>Cédula</th><th>Ciudad</th></tr></thead>
            <tbody>
            <c:forEach var="k" items="${clientes}" varStatus="st">
                <tr class="${k.codigo == cliente.codigo ? 'sel' : ''}" style="animation-delay:${st.index < 15 ? st.index * 35 : 0}ms;cursor:pointer"
                    onclick="location.href='clientes?codigo=${k.codigo}&buscar=<c:out value='${buscar}'/>'">
                    <td><b>${k.codigo}</b></td>
                    <td><c:out value="${k.nombre}"/></td>
                    <td><c:out value="${k.paterno} ${k.materno}"/></td>
                    <td>${k.cedula}</td>
                    <td><c:out value="${k.ciudad}"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty clientes}"><tr><td colspan="5" class="vacio-hist">No se encontraron clientes</td></tr></c:if>
            </tbody>
        </table>
    </section>

    <section class="tarjeta ${not empty error ? 'error' : ''}">
        <h2>${empty cliente.codigo ? '➕ Nuevo cliente' : '✏️ Editar cliente '.concat(cliente.codigo)}</h2>
        <form method="post" action="clientes" class="form-cli" id="formCli">
            <input type="hidden" name="codigo" value="${cliente.codigo}">
            <input type="hidden" name="accion" value="guardar" id="accion">
            <div class="full"><label>Nombres *</label><input name="nombre" value="<c:out value='${cliente.nombre}'/>" required maxlength="30"></div>
            <div><label>Apellido paterno *</label><input name="paterno" value="<c:out value='${cliente.paterno}'/>" required maxlength="25"></div>
            <div><label>Apellido materno *</label><input name="materno" value="<c:out value='${cliente.materno}'/>" required maxlength="25"></div>
            <div><label>Cédula *</label><input name="cedula" value="<c:out value='${cliente.cedula}'/>" required pattern="\d{10}" title="Cédula de 10 dígitos" maxlength="10" inputmode="numeric"></div>
            <div><label>Ciudad *</label><input name="ciudad" value="<c:out value='${cliente.ciudad}'/>" required maxlength="30"></div>
            <div class="full"><label>Dirección *</label><input name="direccion" value="<c:out value='${cliente.direccion}'/>" required maxlength="50"></div>
            <div><label>Teléfono</label><input name="telefono" value="<c:out value='${cliente.telefono}'/>" maxlength="20"></div>
            <div><label>Correo</label><input name="email" type="email" value="<c:out value='${cliente.email}'/>" maxlength="50"></div>
            <div class="full acciones">
                <a class="btn sec" href="clientes" style="text-align:center">Nuevo</a>
                <button class="btn" type="submit" style="--c:var(--verde)">Guardar</button>
                <c:if test="${not empty cliente.codigo}">
                    <button class="btn rojo" type="submit" formnovalidate
                            onclick="if(!confirm('¿Eliminar el cliente ${cliente.codigo}?')) return false; document.getElementById('accion').value='eliminar';">Eliminar</button>
                </c:if>
            </div>
        </form>
        <c:if test="${not empty error}"><div class="msg error">✖ <c:out value="${error}"/></div></c:if>
        <c:if test="${not empty aviso}"><div class="msg ok">✔ <c:out value="${aviso}"/></div></c:if>

        <c:if test="${not empty cliente.codigo and not empty catalogos}">
            <h2 style="margin-top:22px">🏦 Cuentas del cliente</h2>
            <c:choose>
                <c:when test="${empty cuentasCliente}"><div class="vacio-hist" style="padding:10px">Este cliente aún no tiene cuentas</div></c:when>
                <c:otherwise>
                    <table>
                        <c:forEach var="k" items="${cuentasCliente}">
                            <tr><td><a class="chip" href="movimientos?cuenta=${k.codigo}">${k.codigo}</a></td><td>${k.moneda}</td>
                                <td class="der"><fmt:formatNumber value="${k.saldo}" pattern="#,##0.00"/></td><td class="estado-${k.estado}">${k.estado}</td></tr>
                        </c:forEach>
                    </table>
                </c:otherwise>
            </c:choose>
            <form method="post" action="clientes" class="form-cli" style="margin-top:12px">
                <input type="hidden" name="accion" value="abrirCuenta">
                <input type="hidden" name="codigo" value="${cliente.codigo}">
                <div><label>Moneda</label>
                    <select name="moneda"><c:forEach var="o" items="${catalogos.monedas}"><option value="${o.codigo}">${o.nombre}</option></c:forEach></select></div>
                <div><label>Sucursal</label>
                    <select name="sucursal"><c:forEach var="o" items="${catalogos.sucursales}"><option value="${o.codigo}"><c:out value="${o.nombre}"/></option></c:forEach></select></div>
                <div><label>Depósito inicial</label><input name="importe" inputmode="decimal" placeholder="Ej. 500.00" required></div>
                <div><label>PIN de la cuenta (6 dígitos)</label><input name="clave" type="password" pattern="\d{6}" maxlength="6" required></div>
                <div class="full" style="color:var(--tenue);font-size:12px">El número de cuenta se genera automáticamente (sucursal + correlativo).</div>
                <div class="full"><button class="btn" type="submit">Abrir cuenta</button></div>
            </form>
            <c:if test="${not empty errorCuenta}"><div class="msg error">✖ <c:out value="${errorCuenta}"/></div></c:if>
        </c:if>
    </section>
</main>
<%@include file="pie.jspf"%>
</body>
</html>
