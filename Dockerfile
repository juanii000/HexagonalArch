# Usa una imagen base con Java 17 y Gradle
FROM gradle:8.7.0-jdk17 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de configuración de Gradle
COPY build.gradle settings.gradle ./

# Copia el código fuente
COPY src ./src

# Construye la aplicación
RUN gradle build --no-daemon --refresh-dependencies

# Usa una imagen base más ligera para la ejecución
FROM openjdk:17.0.1-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR construido desde la etapa de construcción
COPY --from=build /app/build/libs/*.jar app.jar

# Expone el puerto en el que se ejecuta la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]