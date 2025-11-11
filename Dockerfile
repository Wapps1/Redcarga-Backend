# Imagen ligera de runtime
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache coreutils openssl
WORKDIR /app

# Copiamos tu JAR ya construido
COPY target/redcarga-0.0.1-SNAPSHOT.jar app.jar

COPY entrypoint.sh /entrypoint.sh
RUN sed -i 's/\r$//' /entrypoint.sh && chmod +x /entrypoint.sh

# Perfil prod
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]
