# Imagen ligera de runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos tu JAR ya construido
COPY target/redcarga-0.0.1-SNAPSHOT.jar app.jar

# Perfil prod
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
