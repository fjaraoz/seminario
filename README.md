# Sistema de Gestión de Inventario y Reparaciones de Equipos Informáticos

Proyecto desarrollado como trabajo práctico integrador (TP4) de la materia **Seminario**, Licenciatura en Informática (Universidad Siglo 21).

El sistema permite gestionar:
- Dependencias (oficinas)
- Equipos informáticos (PCs, impresoras, etc.)
- Reparaciones asociadas a cada equipo
- Auditoría básica de operaciones
- Exportación de listados a CSV

La aplicación está desarrollada en **Java** (Swing) con arquitectura por capas (dominio, repositorios, servicios, UI) e integración con **MySQL** para la persistencia de datos.

---

## Estructura del proyecto

- `src/`
  - `app/` → clase principal `Main`, configuración inicial de repositorios y servicios.
  - `domain.model/` → entidades de dominio (Equipo, Reparacion, Dependencia, Usuario, etc.).
  - `domain.repo/` → interfaces de repositorio (contratos de persistencia).
  - `infra.repo.mem/` → repositorios en memoria (modo demo / pruebas).
  - `infra.repo.mysql/` → repositorios conectados a MySQL.
  - `service/` → lógica de negocio y validaciones.
  - `ui/` → interfaz gráfica Swing (formularios, tablas, controladores).
  - `util/` → utilitarios (exportación CSV, configuración, etc.).
- `SQL/`
  - `1- Creacion.sql`
  - `2- DDL.sql`
  - `3- DML.sql`
- `lib/`
  - `mysql-connector-j-xxxx.jar` (driver JDBC de MySQL)
- `ejecutar.bat` → script para compilar y ejecutar la aplicación en Windows.

La clase principal de la aplicación es:

```text
app.Main
```

---

## Requisitos

- **Java Development Kit (JDK) 17 o superior** instalado.
- **MySQL Server** (8.x o 9.x) instalado y en ejecución en `localhost`.
- Cliente SQL (por ejemplo, **MySQL Workbench**) para ejecutar los scripts de creación de base de datos.
- Sistema operativo Windows (para usar el script `ejecutar.bat`).

---

## Configuración de la base de datos MySQL

1. Abrir MySQL Workbench (u otro cliente) y conectarse al servidor local.
2. Ejecutar los scripts de la carpeta `SQL` **en este orden**:

   1. `1- Creacion.sql`  → crea la base `inventario_it`  
   2. `2- DDL.sql`       → crea las tablas  
   3. `3- DML.sql`       → inserta datos de prueba (dependencias, equipos, reparaciones, usuarios, etc.)

La aplicación, por defecto, se conecta a la base `inventario_it` en `localhost:3306`.

---

## Configuración de conexión en el código

El acceso a MySQL se centraliza en la clase:

```text
src/infra/repo/mysql/MySqlConnectionProvider.java
```

Dentro del archivo se deben ajustar estas constantes para que coincidan con un usuario válido en la PC donde se ejecute el proyecto:

```java
private static final String USER = "usuario_mysql";
private static final String PASSWORD = "clave_mysql";
```

Si se cambia el nombre de la base de datos, también habría que ajustar `inventario_it` en la `URL` de conexión.

---

## Java, driver MySQL y ejecución con `ejecutar.bat`

- El conector JDBC está en:

  ```text
  lib/mysql-connector-j-xxxx.jar
  ```

  El batch ya incluye `lib/*` en el classpath, por lo que no hace falta configurar nada extra en el IDE para ejecutarlo mediante el script.

- Verificar la carpeta donde está instalado Java y, si es necesario, ajustar en el archivo:

  ```text
  ejecutar.bat
  ```

  la variable:

  ```bat
  set "JAVA_HOME=C:\Program Files\Java\jdk-17"
  ```

  para que apunte al directorio real del JDK en esa PC (o comentarla si Java ya está en el `PATH`).

### Ejecutar la aplicación

1. Situarse en la carpeta raíz del proyecto (donde está `ejecutar.bat`).  
2. Hacer doble clic en **`ejecutar.bat`** (o ejecutarlo desde una consola).  
   El script compila el código en `bin/` y luego levanta la app con la clase principal `app.Main`.

---

## Notas funcionales

- El sistema permite:
  - Alta, baja y modificación de equipos informáticos.
  - Registro y seguimiento de reparaciones por equipo.
  - Filtrado y búsqueda de equipos por dependencia, tipo y texto libre.
  - Exportación de listados a archivos CSV (codificación UTF-8).
  - Registro de acciones en una auditoría que se persiste en archivo.

- El proyecto incluye repositorios en memoria y repositorios MySQL:
  - Para la entrega del TP4, la aplicación está configurada para trabajar con **MySQL**.
