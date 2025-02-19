# Étape 1: Utiliser une image de base avec Java installé
FROM openjdk:23-jdk

# Étape 2: Définir le répertoire de travail à l'intérieur du conteneur
WORKDIR /app

# Étape 3: Copier le fichier .jar de ton application dans le conteneur
COPY target/plumedenfant-0.0.1-SNAPSHOT.jar /app/plumedenfant-0.0.1-SNAPSHOT.jar

# Force rebuild
RUN echo "Force rebuild $(date)"

# Étape 4: Exposer le port sur lequel ton application écoutera (généralement 8080 pour Spring Boot)
EXPOSE 8080

# Étape 5: Lancer l'application Spring Boot
ENTRYPOINT ["java", "-jar", "plumedenfant-0.0.1-SNAPSHOT.jar"]
