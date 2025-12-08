# Dockerfile

# Utilitza una imatge base amb Java 24
FROM eclipse-temurin:24-jdk-alpine

# Directoris de treball
WORKDIR /app

# Copia el jar construït al contenidor
COPY ./target/economyapp-0.0.1-SNAPSHOT.jar myapp.jar

# Comanda per executar l'aplicació
ENTRYPOINT ["java", "-jar", "myapp.jar"]
