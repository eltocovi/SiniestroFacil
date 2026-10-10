# SiniestroFácil

Plataforma para la gestión de siniestros automotrices, construida con una **arquitectura de microservicios** en Java 21 y Spring Boot. Permite administrar usuarios, pólizas y asegurados, registrar denuncias de siniestros y coordinar la asignación de vehículos a talleres. Cada microservicio tiene su propia base de datos MySQL, y todo el sistema se levanta con Docker Compose.

> Proyecto académico – **JVY0101 Java: Diseño y Construcción de Soluciones Nativas en Nube** – Evaluación Parcial N°2 – Duoc UC.

---

## Tabla de contenidos

1. [Arquitectura](#1-arquitectura)
2. [Microservicios](#2-microservicios)
3. [Modelo de datos y relaciones JPA](#3-modelo-de-datos-y-relaciones-jpa)
4. [Endpoints REST](#4-endpoints-rest)
5. [Tecnologías y dependencias (pom.xml)](#5-tecnologías-y-dependencias-pomxml)
6. [Estructura del repositorio](#6-estructura-del-repositorio)
7. [Requisitos previos](#7-requisitos-previos)
8. [Instalación y ejecución](#8-instalación-y-ejecución)
9. [Compilación y empaquetado con Maven](#9-compilación-y-empaquetado-con-maven)
10. [Pruebas](#10-pruebas)
11. [Verificación en la base de datos](#11-verificación-en-la-base-de-datos)
12. [Flujo de trabajo con Git](#12-flujo-de-trabajo-con-git)
13. [Solución de problemas](#13-solución-de-problemas)
14. [Equipo](#14-equipo)

---

## 1. Arquitectura

Cada dominio del negocio vive en su propio microservicio Maven independiente, con su propio `pom.xml`, `Dockerfile`, configuración y base de datos aislada. Los servicios se comunican dentro de la red interna de Docker `siniestro-network`, y la autenticación se centraliza en `ms-usuarios` mediante tokens **JWT**.

```text
                         ┌──────────────────────┐
     Postman / Swagger ─►│  ms-usuarios  :8082  │──► mysql-usuarios  :3306
            │            │  (emite JWT)         │
            │            └──────────────────────┘
            │  Authorization: Bearer <token>
            ├───────────►  ms-denuncias  :8081   ──► mysql-denuncias :3307
            ├───────────►  ms-talleres   :8083   ──► mysql-talleres  :3308
            └───────────►  ms-poliza     :8084   ──► mysql-poliza    :3309
```

### Arquitectura interna en capas

Todos los microservicios siguen la misma organización en capas, usando inyección de dependencias de Spring:

```text
controller  →  service  →  repository  →  MySQL
    ↑             ↑
   dto      model / mapper
```

| Capa | Responsabilidad |
|---|---|
| `controller` | Expone los endpoints REST (`@RestController`), valida la entrada (`@Valid`) y devuelve `ResponseEntity` con el código HTTP adecuado. |
| `dto` | Objetos de transferencia de datos: `*Request` para la entrada y `*Response` para la salida. Las entidades no se exponen directamente. |
| `service` | Lógica de negocio. Interfaces (`*Service`) y sus implementaciones (`*ServiceImpl`), anotadas con `@Service`. |
| `model` | Entidades JPA (`@Entity`) y enumeraciones de estado. |
| `repository` | Acceso a datos con Spring Data JPA (`JpaRepository`). |
| `exception` | Manejo centralizado de errores con `@RestControllerAdvice` (`GlobalExceptionHandler`), que devuelve respuestas `ErrorResponse` uniformes. |
| `config` | Configuración de seguridad (Spring Security + JWT). |

---

## 2. Microservicios

| Microservicio | Descripción | Puerto API | Base de datos | Puerto MySQL (host) |
|---|---|---|---|---|
| `ms-usuarios` | Registro, autenticación y emisión de tokens JWT. | `8082` | `mysql-usuarios` | `3306` |
| `ms-denuncias` | Registro y seguimiento de denuncias de siniestros. | `8081` | `mysql-denuncias` | `3307` |
| `ms-talleres` | Red de talleres y asignación de vehículos. | `8083` | `mysql-talleres` | `3308` |
| `ms-poliza` | Gestión de pólizas, asegurados y coberturas. | `8084` | `mysql-poliza` | `3309` |

### ms-usuarios

Gestiona las cuentas de usuario y provee la seguridad centralizada del ecosistema.

- **Entidad:** `Usuario`
- **Seguridad:** Spring Security, contraseñas cifradas con `PasswordEncoder` (BCrypt) y generación de tokens JWT.
- **Validaciones de negocio:** formato de RUT chileno (`12345678-9`) y formato de correo electrónico, tanto al registrar como al actualizar.
- **Funcionalidades:** registro, login, CRUD de usuarios y activación o desactivación de cuentas.

### ms-denuncias

Permite registrar y hacer seguimiento a las denuncias de siniestros automotrices.

- **Entidades:** `Denuncia`, `Fotografia` (relación 1:N en cascada).
- **Seguridad:** valida los tokens JWT emitidos por `ms-usuarios` y protege los endpoints con `@PreAuthorize`.
- **Documentación:** Swagger UI (springdoc-openapi).
- **Funcionalidades:** CRUD de denuncias y almacenamiento de varias fotografías por evento.

### ms-talleres

Administra la red de talleres y la logística de asignación de vehículos siniestrados.

- **Entidades:** `Taller`, `Asignacion`.
- **Enumeraciones:** `EstadoAsignacion`.
- **Controladores:** `TallerController`, `AsignacionController`.
- **Funcionalidades:**
  - CRUD de talleres.
  - Actualización de la disponibilidad de un taller (`DisponibilidadRequest`).
  - Asignación de un siniestro a un taller (`AsignacionRequest`).
  - Registro de la recepción del vehículo (`RecepcionRequest`).
  - Cambio de estado de una asignación (`CambioEstadoRequest`).

### ms-poliza

Administra las pólizas de seguro, los asegurados y sus coberturas.

- **Entidades:** `Poliza`, `Asegurado`, `Cobertura`.
- **Enumeraciones:** `EstadoPoliza`, `TipoCobertura`.
- **Controladores:** `PolizaController`, `AseguradoController`.
- **Funcionalidades:**
  - CRUD de pólizas y asegurados.
  - Gestión de coberturas asociadas a una póliza.
  - Cambio de estado de una póliza (`CambioEstadoRequest`).
  - Consulta de vigencia de una póliza (`VigenciaResponse`).
- **Documentación:** Swagger UI en `http://localhost:8084/swagger-ui.html`.
- **Pruebas:** tests unitarios de controlador, servicios y repositorio.

---

## 3. Modelo de datos y relaciones JPA

Las entidades se mapean a tablas con anotaciones JPA/Hibernate (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@Column`, `@Enumerated`). Hibernate crea y actualiza el esquema automáticamente al iniciar cada servicio (`spring.jpa.hibernate.ddl-auto=update`).

| Microservicio | Relación | Tipo | Justificación |
|---|---|---|---|
| `ms-denuncias` | `Denuncia` → `Fotografia` | `@OneToMany` / `@ManyToOne` (cascada) | Una denuncia puede tener varias fotografías del siniestro; al eliminar la denuncia se eliminan sus fotos. |
| `ms-poliza` | `Asegurado` → `Poliza` | `@OneToMany` / `@ManyToOne` | Un asegurado puede contratar varias pólizas; cada póliza pertenece a un solo asegurado. |
| `ms-poliza` | `Poliza` → `Cobertura` | `@OneToMany` / `@ManyToOne` | Una póliza agrupa varias coberturas (por ejemplo, daños propios o responsabilidad civil). |
| `ms-talleres` | `Taller` → `Asignacion` | `@OneToMany` / `@ManyToOne` | Un taller recibe varias asignaciones de vehículos a lo largo del tiempo. |

Las relaciones **entre microservicios** (por ejemplo, la denuncia que origina una asignación en un taller) no usan claves foráneas entre bases de datos. Se guarda el identificador de la entidad externa, respetando el principio de base de datos por servicio.

---

## 4. Endpoints REST

Todos los endpoints, excepto registro y login, requieren el encabezado:

```http
Authorization: Bearer <token>
```

Respuestas HTTP utilizadas en toda la API:

| Código | Significado |
|---|---|
| `200 OK` | Consulta o actualización exitosa. |
| `201 Created` | Recurso creado. |
| `204 No Content` | Recurso eliminado. |
| `400 Bad Request` | Datos inválidos (por ejemplo, RUT o correo con formato incorrecto). |
| `401 Unauthorized` | Falta el token o no es válido. |
| `403 Forbidden` | El usuario no tiene permisos para la operación. |
| `404 Not Found` | El recurso no existe. |

### ms-usuarios – `http://localhost:8082`

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/login` | Inicia sesión y devuelve un token JWT. |
| `POST` | `/api/usuarios` | Registra un usuario. |
| `GET` | `/api/usuarios` | Lista todos los usuarios. |
| `GET` | `/api/usuarios/{id}` | Obtiene un usuario por ID. |
| `GET` | `/api/correo/{correo}` | Obtiene usuario por correo. |
| `PUT` | `/api/usuarios/{id}` | Actualiza un usuario. |
| `PATCH` | `/api/usuarios/{id}/estado` | Activa o desactiva un usuario. |
| `DELETE` | `/api/usuarios/{id}` | Elimina un usuario. |

### ms-denuncias – `http://localhost:8081`

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/denuncias` | Registra una denuncia con sus fotografías. |
| `GET` | `/api/denuncias` | Lista todas las denuncias. |
| `GET` | `/api/denuncias/{id}` | Obtiene una denuncia por ID. |
| `PUT` | `/api/denuncias/{id}` | Actualiza una denuncia. |
| `DELETE` | `/api/denuncias/{id}` | Elimina una denuncia y sus fotografías. |

### ms-talleres – `http://localhost:8083`

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/talleres` | Registra un taller. |
| `GET` | `/api/talleres` | Lista todos los talleres. |
| `GET` | `/api/talleres/{id}` | Obtiene un taller por ID. |
| `PUT` | `/api/talleres/{id}` | Actualiza un taller. |
| `PATCH` | `/api/talleres/{id}/disponibilidad` | Actualiza la disponibilidad de un taller. |
| `DELETE` | `/api/talleres/{id}` | Elimina un taller. |
| `POST` | `/api/asignaciones` | Asigna un siniestro a un taller. |
| `GET` | `/api/asignaciones` | Lista todas las asignaciones. |
| `GET` | `/api/asignaciones/{id}` | Obtiene una asignación por ID. |
| `PATCH` | `/api/asignaciones/{id}/recepcion` | Registra la recepción del vehículo. |
| `PATCH` | `/api/asignaciones/{id}/estado` | Cambia el estado de una asignación. |

### ms-poliza – `http://localhost:8084`

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/polizas` | Crea una póliza con sus coberturas. |
| `GET` | `/api/polizas` | Lista todas las pólizas. |
| `GET` | `/api/polizas/{id}` | Obtiene una póliza por ID. |
| `GET` | `/api/polizas/{id}/vigencia` | Consulta si una póliza está vigente. |
| `PUT` | `/api/polizas/{id}` | Actualiza una póliza. |
| `PATCH` | `/api/polizas/{id}/estado` | Cambia el estado de una póliza. |
| `DELETE` | `/api/polizas/{id}` | Elimina una póliza. |
| `POST` | `/api/asegurados` | Registra un asegurado. |
| `GET` | `/api/asegurados` | Lista todos los asegurados. |
| `GET` | `/api/asegurados/{id}` | Obtiene un asegurado por ID. |
| `PUT` | `/api/asegurados/{id}` | Actualiza un asegurado. |
| `DELETE` | `/api/asegurados/{id}` | Elimina un asegurado. |

### Documentación interactiva (Swagger UI)

Con los servicios en ejecución:

- `ms-denuncias`: http://localhost:8081/swagger-ui/index.html
- `ms-poliza`: http://localhost:8084/swagger-ui.html

---

## 5. Tecnologías y dependencias (pom.xml)

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje de programación |
| Spring Boot 4 | Framework base de los microservicios |
| Spring Web | Controladores REST |
| Spring Data JPA / Hibernate | Persistencia y generación automática del esquema |
| Spring Validation | Validación de los DTO (`@Valid`, `@NotNull`, `@NotBlank`, etc.) |
| Spring Security + JWT (jjwt) | Autenticación y autorización |
| MySQL 8.0 + MySQL Connector/J | Base de datos relacional |
| Lombok | Reducción de código repetitivo (getters, setters, constructores) |
| springdoc-openapi | Documentación Swagger UI |
| JUnit 5 / Mockito | Pruebas unitarias |
| H2 (scope `test`) | Base de datos en memoria para las pruebas |
| Maven + Maven Wrapper (`mvnw`) | Gestión de dependencias, compilación y empaquetado |
| Docker / Docker Compose | Contenedores y orquestación local |

Cada `pom.xml` hereda de `spring-boot-starter-parent` e incluye el plugin `spring-boot-maven-plugin`, que genera un `.jar` ejecutable con todas sus dependencias.

---

## 6. Estructura del repositorio

El proyecto es un **monorepo**: cada carpeta principal es un microservicio Maven independiente, y en la raíz está el orquestador de la infraestructura.

```text
SiniestroFacil/
├── ms-usuarios/
│   ├── Dockerfile
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd
│   └── src/
│       ├── main/java/cl/duoc/ms_usuarios/
│       │   ├── config/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── model/
│       │   ├── repository/
│       │   └── service/
│       └── main/resources/application.properties
├── ms-denuncias/          # Misma estructura
├── ms-talleres/
│   └── src/main/java/cl/duoc/siniestrofacil/ms_talleres/
│       ├── controller/
│       ├── dto/
│       ├── exception/
│       ├── model/
│       ├── repository/
│       └── service/
├── ms-poliza/
│   └── src/
│       ├── main/java/cl/duoc/siniestrofacil/ms_poliza/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── exception/
│       │   ├── model/
│       │   ├── repository/
│       │   └── service/
│       └── test/          # Pruebas unitarias
├── docker-compose.yml     # Orquestación de los 8 contenedores
└── README.md
```

---

## 7. Requisitos previos

| Herramienta | Versión | Uso |
|---|---|---|
| Git | 2.x | Clonar el repositorio |
| Docker Desktop | 4.x o superior | Levantar los contenedores |
| JDK | 21 | Compilar y empaquetar con Maven |
| Maven | 3.9+ (opcional) | Cada servicio incluye el wrapper `mvnw`, que lo descarga automáticamente |
| Postman | Cualquiera | Probar los endpoints |

> **Importante:** si tienes MySQL local (por ejemplo, XAMPP) ocupando el puerto `3306`, detenlo antes de levantar Docker. Ver [Solución de problemas](#13-solución-de-problemas).

---

## 8. Instalación y ejecución

### Opción A – Con Docker (recomendada)

Levanta los 4 microservicios y sus 4 bases de datos con un solo comando.

**1. Clonar el repositorio**

```bash
git clone https://github.com/eltocovi/SiniestroFacil.git
cd SiniestroFacil

```

**2. Empaquetar `ms-usuarios`**

El Dockerfile de `ms-usuarios` copia un `.jar` ya compilado, así que debe empaquetarse antes. Los demás microservicios se compilan automáticamente dentro de Docker.

```bash
cd ms-usuarios
./mvnw clean package -DskipTests      # Windows: .\mvnw.cmd clean package -DskipTests
cd ..
```

**3. Levantar la infraestructura**

Desde la raíz del proyecto, donde está `docker-compose.yml`:

```bash
docker compose up -d --build
```

Este comando descarga la imagen de MySQL 8.0, construye las imágenes de los 4 microservicios con Java 21 y levanta los 8 contenedores en la red `siniestro-network`. Hibernate crea las tablas automáticamente al iniciar.

**4. Verificar que todo esté arriba**

```bash
docker compose ps
```

Los 8 contenedores deben aparecer con estado `Up`. Para revisar el log de un servicio:

```bash
docker compose logs -f ms-poliza
```

**5. Detener todo**

```bash
docker compose down        # Detiene los contenedores y conserva los datos
docker compose down -v     # Detiene los contenedores y borra las bases de datos
```

### Opción B – Ejecución local sin Docker

Requiere MySQL 8 instalado localmente. Cada servicio lee la conexión desde variables de entorno, con valores por defecto para desarrollo:

| Variable | Valor por defecto |
|---|---|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3306` |
| `DB_USERNAME` | `root` |
| `DB_PASSWORD` | `root` |

```bash
cd ms-poliza
./mvnw spring-boot:run
```

Repite el proceso en una terminal distinta para cada microservicio. Las bases de datos se crean automáticamente gracias a `createDatabaseIfNotExist=true`.

> **Credenciales:** con fines académicos, todas las bases de datos usan el usuario `root` y la contraseña `root`. En un entorno productivo, estas credenciales y la clave JWT deben entregarse mediante variables de entorno o un gestor de secretos.

---

## 9. Compilación y empaquetado con Maven

Desde la carpeta de cualquier microservicio:

| Comando | Qué hace |
|---|---|
| `./mvnw clean` | Elimina la carpeta `target/` con compilaciones anteriores. |
| `./mvnw compile` | Compila el código fuente. |
| `./mvnw test` | Ejecuta las pruebas unitarias. |
| `./mvnw package` | Compila, ejecuta las pruebas y genera el `.jar` en `target/`. |
| `./mvnw install` | Igual que `package`, y además instala el artefacto en el repositorio local de Maven (`~/.m2`). |
| `./mvnw clean package -DskipTests` | Genera el `.jar` sin ejecutar las pruebas. |

En Windows se usa `.\mvnw.cmd` en lugar de `./mvnw`.

El resultado es un `.jar` ejecutable, por ejemplo `target/ms-poliza-0.0.1-SNAPSHOT.jar`, que se puede ejecutar directamente:

```bash
java -jar target/ms-poliza-0.0.1-SNAPSHOT.jar
```

---

## 10. Pruebas

### 10.1 Pruebas unitarias

Las pruebas aíslan la base de datos simulando dependencias con Mockito y usan una configuración propia en `src/test/resources/application.properties`.

```bash
cd ms-poliza
./mvnw test
```

### 10.2 Pruebas de endpoints con Postman

**Paso 1 – Registrar un usuario**

```http
POST http://localhost:8082/api/usuarios
Content-Type: application/json

{
  "rutUsuario": "12345678-9",
  "nombreUsuario": "Felipe",
  "apellidoUsuario": "Palma",
  "edad": 25,
  "correo": "felipe@duocuc.cl",
  "telefono": "+56912345678",
  "direccion": "Av. Siempre Viva 123",
  "contrasena": "Clave123"
}
```

**Paso 2 – Iniciar sesión y copiar el token**

```http
POST http://localhost:8082/api/auth/login
Content-Type: application/json

{
  "correo": "felipe@duocuc.cl",
  "contrasena": "Clave123"
}
```

**Paso 3 – Usar el token en los demás servicios**

En Postman, pestaña **Authorization** → tipo **Bearer Token** → pega el token. Con eso se pueden probar las operaciones CRUD de los demás microservicios.

**Casos de error recomendados**

| Caso | Petición | Respuesta esperada |
|---|---|---|
| RUT con formato inválido | `POST /api/usuarios` con `"rutUsuario": "123"` | `400 Bad Request` |
| Correo con formato inválido | `POST /api/usuarios` con `"correo": "felipe@"` | `400 Bad Request` |
| Petición sin token | `GET /api/polizas` sin encabezado `Authorization` | `401 Unauthorized` |
| Recurso inexistente | `GET /api/talleres/9999` | `404 Not Found` |
| Campos obligatorios vacíos | `POST /api/denuncias` con cuerpo `{}` | `400 Bad Request` |

---

## 11. Verificación en la base de datos

Para comprobar que las operaciones CRUD persisten los datos, puedes conectarte a cada base desde **MySQL Workbench**, **DBeaver** o **HeidiSQL**:

| Base | Host | Puerto | Usuario | Contraseña |
|---|---|---|---|---|
| Usuarios | `localhost` | `3306` | `root` | `root` |
| Denuncias | `localhost` | `3307` | `root` | `root` |
| Talleres | `localhost` | `3308` | `root` | `root` |
| Pólizas | `localhost` | `3309` | `root` | `root` |

También puedes hacerlo desde la terminal, sin instalar nada:

```bash
docker exec -it mysql-poliza mysql -u root -proot
```

```sql
SHOW DATABASES;
USE db_poliza;
SHOW TABLES;
SELECT * FROM poliza;
```

---

## 12. Flujo de trabajo con Git

El proyecto sigue un flujo basado en **Git Flow**:

```text
feature/*  ──►  develop  ──►  main
```

| Rama | Propósito |
|---|---|
| `main` | Versión estable; refleja las entregas formales. Recibe cambios solo desde `develop` mediante Pull Request. |
| `develop` | Rama de integración donde se unen todos los microservicios. |
| `feature/ms-usuarios`, `feature/usuarios-auth` | Desarrollo de `ms-usuarios` y su seguridad JWT. |
| `feature/ms-denuncias` | Desarrollo de `ms-denuncias`. |
| `feature/ms-talleres` | Desarrollo de `ms-talleres`. |
| `feature/ms-poliza` | Desarrollo de `ms-poliza`. |

### Trabajar en una nueva funcionalidad

```bash
git checkout develop
git pull origin develop
git checkout -b feature/nombre-funcionalidad

# ... cambios ...
git add .
git commit -m "feat(ms-poliza): agrega endpoint de vigencia"
git push origin feature/nombre-funcionalidad
```

Luego se abre un **Pull Request** hacia `develop` para su revisión.

### Convención de commits

Se usa el formato [Conventional Commits](https://www.conventionalcommits.org/es/): `tipo(alcance): descripción`.

| Prefijo | Uso | Ejemplo |
|---|---|---|
| `feat` | Nueva funcionalidad | `feat(ms-talleres): agrega asignación de vehículos` |
| `fix` | Corrección de errores | `fix(ms-usuarios): corrige validación de RUT` |
| `test` | Pruebas | `test(ms-poliza): agrega tests de PolizaService` |
| `refactor` | Mejora de código sin cambiar comportamiento | `refactor(ms-talleres): renombra paquete Service a service` |
| `docs` | Documentación | `docs: actualiza README` |
| `chore` | Infraestructura y mantenimiento | `chore: configura docker-compose` |

---

## 13. Solución de problemas

| Problema | Causa | Solución |
|---|---|---|
| `ports are not available: ... 3306` | Otro MySQL (por ejemplo, XAMPP) usa el puerto `3306`. | Detén MySQL en el panel de XAMPP, o cambia el puerto del host de `mysql-usuarios` en `docker-compose.yml`. |
| `"/target/ms-usuarios-0.0.1-SNAPSHOT.jar": not found` | No se empaquetó `ms-usuarios` antes de construir. | Ejecuta `./mvnw clean package -DskipTests` dentro de `ms-usuarios`. |
| `UnsupportedClassVersionError` o `release version 21 not supported` | El Dockerfile usa una imagen de Java 17. | Usa `eclipse-temurin:21-jdk` para compilar y `eclipse-temurin:21-jre` para ejecutar. |
| Un servicio termina con `Exited (1)` al iniciar | Arrancó antes de que MySQL estuviera listo. | `docker compose restart <servicio>` |
| `request returned 500 Internal Server Error ... dockerDesktopLinuxEngine` | El motor de Docker no está corriendo. | Reinicia Docker Desktop y espera el estado **Engine running**. |
| `... cannot be resolved to a type` en Linux o Docker, pero no en Windows | Una carpeta de paquete con mayúsculas distintas (por ejemplo, `Service` en vez de `service`). | Los paquetes Java van siempre en minúscula. |

---

## 14. Equipo

| Integrante | Responsabilidad | GitHub |
|---|---|---|
| Felipe Palma | Desarrollo de `ms-talleres` y `ms-poliza` | [@Felipedge](https://github.com/Felipedge) |
| Víctor Valenzuela | Desarrollo de `ms-denuncias` | [@eltocovi](https://github.com/eltocovi) |
| Manuel Vargas | Desarrollo de `ms-usuarios` (autenticación y JWT) | [@manuvl-dev](https://github.com/manuvl-dev) |

La **infraestructura** (Dockerfiles, `docker-compose.yml`, configuración de redes y bases de datos) y la **integración en `develop`** se desarrollaron en conjunto por todo el equipo.

---

## Licencia

Proyecto académico desarrollado con fines educativos para Duoc UC.
