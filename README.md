<div align="center">

```
 ___  _   _  ___  ___  _  __   _     ___    _    _  _  _  __
| __|| | | || _ \| __|| |/ /  /_\   | _ )  /_\  | \| || |/ /
| _| | |_| ||   /| _| | ' <  / _ \  | _ \ / _ \ | .` || ' <
|___| \___/ |_|_\|___||_|\_\/_/ \_\ |___//_/ \_\|_|\_||_|\_\

   ╔═══════════════════════════════════════════════╗
   ║        E U R E K A B A N K   M O N S T E R     ║
   ║     SOA · RESTful · Java · MariaDB (con BDD)   ║
   ╚═══════════════════════════════════════════════╝
```

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-11-1F3864?style=for-the-badge)
![Payara](https://img.shields.io/badge/Payara-7-0E7EC2?style=for-the-badge)
![MariaDB](https://img.shields.io/badge/MariaDB-3307-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![Android](https://img.shields.io/badge/Android-Java-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![REST](https://img.shields.io/badge/API-REST%20%2B%20JSON-F5B841?style=for-the-badge)

**TG1.3 · PAO202651 · NRC 36841 · Grupo 1**
Arquitectura de Software — Universidad de las Fuerzas Armadas ESPE

</div>

---

## 📌 Descripción

Servidor **RESTful** en Java (Jakarta EE sobre **Payara Server**) con base de datos **MariaDB** que atiende las
operaciones de ventanilla de **EurekaBank**: cuentas, saldos, movimientos, **depósitos, retiros y transferencias
transaccionales**, **apertura de cuentas** y **CRUD de clientes** (cédula ecuatoriana, montos en **USD**). Lo consumen **cuatro clientes**:

```
                         ┌───────────────────────────────────────────────┐
  ┌───────────┐  JSON    │         Payara Server 7 · Jakarta EE 11        │
  │  CONSOLA  │────────▶ │  ┌─────────────────────────────────────────┐   │
  └───────────┘          │  │       WS_EUREKA_RESTFUL_JAVA  /api       │   │
  ┌───────────┐  JSON    │  │  controlador → /auth /cuentas /clientes  │   │
  │ ESCRITORIO│────────▶ │  │                /operaciones /estado      │   │
  └───────────┘          │  │  servicio    → reglas + transacciones    │   │
  ┌───────────┐  JSON    │  │  dao         → insertar · seleccionar…   │   │
  │    WEB    │────────▶ │  │  db          → ConexionBD «Singleton»    │   │
  └───────────┘          │  └───────────────────┬─────────────────────┘   │
  ┌───────────┐  JSON    │       📡 Panel        │ JDBC                    │
  │  MÓVIL 📱 │────────▶ └───────────────────────┼─────────────────────────┘
  └───────────┘                          ┌───────▼────────┐
                                         │ 🐬 MariaDB 3307 │
                                         │   eurekabank    │
                                         └────────────────┘
```

## 🗂️ Estructura

```
TG1.3 PAO202651_EUREKA_BDD_LOCAL_RESTFUL_JAVA_NRC36841_GR01
├── 01. UML ............ ERS · ECUD · diagramas (casos de uso, actividad, secuencia, clases, arquitectura)
├── 02. MER ............ modelo entidad-relación (conceptual · lógico · físico)
├── 03. BDD ............ Crea_BD.sql (DDL) · Carga_Datos.sql · Ajustes_Ecuador.sql · CREDENCIALES.txt
├── 04. SERVIDOR ....... WS_EUREKA_RESTFUL_JAVA      ⚙️  servicio REST + panel
├── 04. CLICON ......... CLICON_EUREKA_RESTFUL_JAVA  🖥️  consola
├── 04. CLIESC ......... CLIESC_EUREKA_RESTFUL_JAVA  🪟  escritorio (Swing + FlatLaf)
├── 04. CLIWEB ......... CLIWEB_EUREKA_RESTFUL_JAVA  🌐  web (Servlets + JSP)
├── 04. CLIMOV ......... app Android                  📱  móvil
├── 05. DOCUMENTACION .. manual técnico (Word) · presentación (PPT)
└── 06. EVIDENCIAS ..... capturas de funcionamiento
```

## 🐬 Base de datos

```powershell
# 03. BDD  (MariaDB en el puerto 3307, root / rootroot)
cmd /c "mariadb -uroot -prootroot -P3307 < Crea_BD.sql"      # DROP · CREATE · ALTER (16 FK)
cmd /c "mariadb -uroot -prootroot -P3307 < Carga_Datos.sql"
cmd /c "mariadb -uroot -prootroot -P3307 < Ajustes_Ecuador.sql"  # cédula 10 dígitos · USD · sucursales EC
```

Conexión del servidor: `src/main/resources/db.properties` → `jdbc:mariadb://localhost:3307/eurekabank`

## 🔐 Credenciales

| Usuario | Clave | Empleado |
|:-:|:-:|:-:|
| `monster` | `monster69` | 9001 |

> Se valida contra la tabla **usuario** (clave en **SHA-1**). El token dura **30 minutos** y cada movimiento guarda el empleado.

## 🔌 API REST

Base: `http://<IP>:8080/WS_EUREKA_RESTFUL_JAVA/api`

| Método | Ruta | Descripción |
|:-:|---|---|
| `POST` | `/auth/login` · `/auth/logout` | Sesión |
| `GET` | `/cuentas` | Cuentas con titular, moneda y saldo |
| `POST` | `/cuentas` | Abrir cuenta (número automático + movimiento 001) |
| `GET` | `/cuentas/catalogos` | Monedas y sucursales |
| `GET` | `/cuentas/{cuenta}` | Saldo |
| `GET` | `/cuentas/{cuenta}/movimientos` | Estado de cuenta |
| `POST` | `/operaciones/deposito` · `/retiro` | `{cuenta, importe}` |
| `POST` | `/operaciones/transferencia` | `{cuenta, cuentaDestino, importe}` |
| `GET` `POST` | `/clientes` | Listar (`?buscar=`) · insertar |
| `GET` `PUT` `DELETE` | `/clientes/{codigo}` | Seleccionar · actualizar · eliminar |
| `GET` | `/estado` | Servidor + MariaDB (panel) |

Encabezados: `Authorization: Bearer <token>` · `X-Cliente: CONSOLA | ESCRITORIO | WEB | MOVIL`

## 💸 Operaciones seguras

```
validar → BEGIN → SELECT … FOR UPDATE (menor código primero) → reglas → UPDATE cuenta
        → INSERT movimiento (003 depósito · 004 retiro · 009/008 transferencia) → COMMIT
                                     ↘ cualquier error → ROLLBACK
```

✔ 40 transferencias simultáneas en sentidos opuestos → 0 errores, la suma de saldos cuadra.

## 🚀 Cómo ejecutar

```
1) MariaDB :3307  →  cargar Crea_BD.sql y Carga_Datos.sql
2) NetBeans → WS_EUREKA_RESTFUL_JAVA → PruebasConexion → Run File   (13 OK)
                                    → Run      Panel: http://localhost:8080/WS_EUREKA_RESTFUL_JAVA/
3) Clientes
      Consola     →  Clean and Build  →  ejecutar_consola.bat
      Escritorio  →  Clean and Build  →  ejecutar_escritorio.bat
      Web         →  Run  →  http://localhost:8080/CLIWEB_EUREKA_RESTFUL_JAVA/
      Móvil       →  Android Studio  →  Run  →  http://<IP>:8080/WS_EUREKA_RESTFUL_JAVA/api
```

## 🎬 Demostración (roles rotativos)

| Rol | Muestra |
|---|---|
| 1 | Servidor + panel + MariaDB + consola |
| 2 | Web + escritorio (transferencia y CRUD de clientes) |
| 3 | Móvil en el celular |

## 👥 Grupo 1

| Integrante | Rol |
|---|---|
| Capt. Jefferson Santiago Masapanta Guilcatoma | Líder |
| Moisés Sebastián Benalcázar Farinango | Integrante (base de datos) |
| Wilmer Omar Buestán Guaillas | Integrante |

**Tutor:** Ing. Eduardo Mauricio Campaña Ortega

<div align="center">

```
  ┌─────────────────────────────────────────┐
  │  Hecho con ☕ y 💙 por el Grupo 1 · ESPE  │
  └─────────────────────────────────────────┘
```

</div>
