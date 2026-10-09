# ============================================================
# ms-talleres - Imagen Docker (build multi-etapa)
#   Etapa 1: compila y empaqueta el .jar con Maven (JDK 21)
#   Etapa 2: ejecuta solo el .jar sobre una imagen liviana (JRE 21)
# ============================================================

# ---------- Etapa 1: compilación ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Primero se copian solo los archivos de Maven para cachear las dependencias:
# si el código cambia pero el pom.xml no, Docker reutiliza esta capa.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

# Luego el código fuente y el empaquetado (los tests se ejecutan con "mvnw package" fuera de Docker)
COPY src ./src
RUN ./mvnw -B -q clean package -DskipTests

# ---------- Etapa 2: ejecución ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Se ejecuta con un usuario sin privilegios de administrador
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring

COPY --from=build /app/target/ms-talleres-*.jar app.jar

# Conexión a MySQL: por defecto apunta al MySQL de la máquina anfitriona (XAMPP).
# Se puede sobreescribir con: docker run -e DB_HOST=... -e DB_USERNAME=... -e DB_PASSWORD=...
ENV DB_HOST=host.docker.internal \
    DB_PORT=3306 \
    DB_USERNAME=root \
    DB_PASSWORD=""

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
