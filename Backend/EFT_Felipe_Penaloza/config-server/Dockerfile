# syntax=docker/dockerfile:1
# Imagen Docker del microservicio "config-server" (Banco XYZ) - build multi-stage.
#
# Etapa 1 (build): compila el modulo con Maven y genera el JAR ejecutable.
# Etapa 2 (runtime): imagen ligera solo con el JRE y el JAR, para portabilidad
# y despliegue eficiente.

# ---- Etapa 1: compilacion con Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---- Etapa 2: imagen de ejecucion ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
