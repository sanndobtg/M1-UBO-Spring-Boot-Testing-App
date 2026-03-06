#!/bin/bash

#  ÉTAPE 1 : Générer les JARs
echo "=== Génération des JARs ==="
./gradlew :api-user:bootJar
./gradlew :api-reservation:bootJar
./gradlew :api-review:bootJar
./gradlew :gateway:bootJar

#  ÉTAPE 2 : Construire les images Docker
echo "=== Construction des images Docker ==="
docker build -t sanndobtg/vod-api-user:latest ./api-user
docker build -t sanndobtg/vod-api-reservation:latest ./api-reservation
docker build -t sanndobtg/vod-api-review:latest ./api-review
docker build -t sanndobtg/vod-gateway:latest ./gateway

#  ÉTAPE 3 : Pousser les images sur Docker Hub
echo "=== Push sur Docker Hub ==="
docker login
docker push sanndobtg/vod-api-user:latest
docker push sanndobtg/vod-api-reservation:latest
docker push sanndobtg/vod-api-review:latest
docker push sanndobtg/vod-gateway:latest


#  ÉTAPE : Déployer sur le serveur
echo "=== Voila, c'est fait !==="


