# Plateforme VOD — Location de Films

> Projet réalisé dans le cadre du module **Systeme d'information** — M1 TIIL-A, UBO Brest

## Équipe

| Membre | Rôle                            |
|--------|---------------------------------|
| **Sanndo BATENGUE** | API Reservation, Gateway, Docker, Intégration |
| **Olabiyi ELEGBEDE** | API User, Frontend Vue.js |
| **Mouad BRAHMI** | API Review, API Poster, Frontend Vue.js |

---

## Présentation du projet

Ce projet est une plateforme de **location de films en ligne (VOD)** permettant aux utilisateurs de :

- Créer un compte et se connecter
- Consulter le catalogue de films
- Louer et rendre des films (avec contraintes métier)
- Évaluer les films qu'ils ont loués
- Consulter et gérer les affiches (posters) des films

Le projet suit une **architecture microservices** : chaque fonctionnalité est une API indépendante, orchestrée par un Gateway unique et déployée via Docker.

---

## Architecture technique

```
┌─────────────────────────────────────────────────────┐
│                   FRONTEND (Vue.js)                 │
│                  http://localhost:5173               │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│              GATEWAY (Spring Cloud Gateway)          │
│                    Port 11080                        │
│                                                     │
│   /comptes/**      → API User        (11081)        │
│   /reservations/** → API Reservation (11082)        │
│   /reviews/**      → API Review      (11083)        │
│   /posters/**      → API Poster      (11084)        │
└──────────────────────┬──────────────────────────────┘
                       │
          ┌────────────┼────────────┐
          ▼            ▼            ▼
    ┌──────────┐ ┌──────────┐ ┌──────────┐
    │ MariaDB  │ │ MongoDB  │ │ MongoDB  │
    │ (SQL)    │ │(reviews) │ │(posters) │
    │ mps_db   │ │          │ │          │
    │ Port     │ │ obiwan   │ │ obiwan   │
    │ 11085    │ │ :27017   │ │ :27017   │
    └──────────┘ └──────────┘ └──────────┘
```

### Modules du projet

```
M1-UBO-Spring-Boot-Testing-App/
├── common/              ← Entités JPA partagées (Compte, Film, Location, Artiste, Genre)
├── api-user/            ← CRUD des comptes utilisateurs
├── api-reservation/     ← Gestion des locations de films
├── api-review/          ← Évaluations des films (Servlet + MongoDB)
├── api-poster/          ← Affiches des films (Servlet + MongoDB)
├── gateway/             ← Point d'entrée unique (Spring Cloud Gateway)
├── frontend/            ← Interface web (Vue.js 3 + Vite)
├── docker-compose.yml   ← Gestion des conteneurs
└── settings.gradle      ← Déclaration des modules Gradle
```

### Technologies utilisées

| Composant | Technologie |
|-----------|-------------|
| Backend | Java 17, Spring Boot 3.4.2 |
| API REST | Spring Web (Controllers) |
| API Servlet | Jakarta Servlet (Review, Poster) |
| Gateway | Spring Cloud Gateway |
| ORM | Spring Data JPA + Hibernate |
| Base SQL | MariaDB |
| Base NoSQL | MongoDB (driver sync 5.2.1) |
| Build | Gradle 8.12 (multi-module) |
| Frontend | Vue.js 3, Vite, Pinia, Axios |
| Conteneurisation | Docker, Docker Compose |
| Documentation API | OpenAPI / Swagger UI |

---

## Endpoints des APIs

### API User — Port 11081

Gestion des comptes utilisateurs (CRUD).

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/comptes` | Liste tous les comptes |
| `GET` | `/comptes/{id}` | Récupère un compte par ID |
| `POST` | `/comptes` | Crée un nouveau compte |
| `DELETE` | `/comptes/{id}` | Supprime un compte |

### API Reservation — Port 11082

Gestion des locations de films. Nécessite le header `X-User-Pseudo`.

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/reservations/me` | Locations en cours de l'utilisateur |
| `POST` | `/reservations/{titreFilm}` | Réserver un film |
| `DELETE` | `/reservations/{titreFilm}` | Terminer une location |

**Contraintes métier :**
- Le film doit être louable (`louable = true`)
- Maximum 3 locations en cours par utilisateur
- L'âge de l'utilisateur doit être ≥ à l'âge minimum du film

### API Review — Port 11083

Évaluations des films (Servlet + MongoDB).

| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| `GET` | `/reviews/{titreFilm}` | Liste les avis d'un film | Non |
| `GET` | `/reviews/{titreFilm}/average` | Moyenne des notes | Non |
| `POST` | `/reviews/{titreFilm}` | Ajouter un avis | `X-User-Pseudo` |

**Contraintes métier :**
- Note strictement entre 0 et 5 (valeurs acceptées : 1, 2, 3, 4)
- Un utilisateur ne peut évaluer un film qu'une seule fois
- Le pseudo est injecté depuis le header HTTP

### API Poster — Port 11084

Gestion des affiches de films (Servlet + MongoDB).

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/posters/{titreFilm}` | Liste les posters d'un film |
| `GET` | `/posters/{posterId}` | Récupère un poster par ID (24 hex) |
| `POST` | `/posters/{titreFilm}` | Ajouter un poster |
| `PUT` | `/posters/{posterId}` | Modifier un poster |
| `DELETE` | `/posters/{posterId}` | Supprimer un poster |

### Gateway — Port 11080

Point d'entrée unique. Toutes les requêtes passent par le gateway :

| Chemin | Service cible |
|--------|---------------|
| `/comptes/**` | API User (11081) |
| `/reservations/**` | API Reservation (11082) |
| `/reviews/**` | API Review (11083) |
| `/posters/**` | API Poster (11084) |

---

## Déploiement Docker

### Prérequis

- Docker et Docker Compose installés
- Accès au Docker Hub (images : `sanndobtg/*`)

### Images Docker

| Image | Service | Port |
|-------|---------|------|
| `sanndobtg/vod-gateway` | Gateway | 11080 |
| `sanndobtg/vod-api-user` | API User | 11081 |
| `sanndobtg/vod-api-reservation` | API Reservation | 11082 |
| `sanndobtg/vod-api-review` | API Review | 11083 |
| `sanndobtg/vod-api-poster` | API Poster | 11084 |
| `mariadb:latest` | MariaDB | 11085 |

### Déployer sur le serveur

```bash
# 1. Se connecter au serveur
ssh eXXXXXXXX@info-tpsi.univ-brest.fr

# 2. Créer le répertoire et le docker-compose.yml
mkdir -p mps-project && cd mps-project
vi docker-compose.yml   # coller le contenu du fichier

# 3. Lancer tous les services
docker compose up -d

# 4. Vérifier que tout tourne
docker compose ps

# 5. Consulter les logs en cas de problème
docker compose logs api-user
```

### Mettre à jour une API

Depuis la machine de développement :

```bash
# Rebuild le JAR
./gradlew :api-reservation:bootJar

# Rebuild l'image Docker
docker build -t sanndobtg/vod-api-reservation:latest ./api-reservation

# Pousser sur Docker Hub
docker push sanndobtg/vod-api-reservation:latest
```

Sur le serveur :

```bash
docker compose pull api-reservation
docker compose up -d api-reservation
```

### Commandes utiles

| Commande | Description |
|----------|-------------|
| `docker compose ps` | État des services |
| `docker compose logs <service>` | Logs d'un service |
| `docker compose restart <service>` | Redémarrer un service |
| `docker compose pull && docker compose up -d` | Mettre à jour tous les services |
| `docker compose down` | Tout arrêter (dernier recours) |

---

## Installation locale (développement)

### Prérequis

- Java 17+
- Gradle 8.12
- Node.js 18+
- Docker (pour MariaDB et MongoDB en local)

### Backend

```bash
# 1. Cloner le projet
git clone https://github.com/sanndobtg/M1-UBO-Spring-Boot-Testing-App
cd M1-UBO-Spring-Boot-Testing-App

# 2. Lancer le script de déploiement (build, images Docker, push)
chmod +x deploy.sh
./deploy.sh

# 3. Lancer les bases de données + tout via Docker Compose
docker compose up -d

```
> Le script `deploy.sh` à la racine du projet automatise les étapes : génération des JARs, construction des images Docker, push sur Docker Hub, et instructions de déploiement serveur.

### Frontend

```bash
cd frontend

# 1. Installer les dépendances
npm install

# 2. Configurer le proxy dans vite.config.js
#    - En local : GATEWAY = 'http://localhost:11080'
#    - Sur le réseau UBO : GATEWAY = 'http://info-tpsi.univ-brest.fr:11080'

# 3. Lancer le serveur de développement
npm run dev

# 4. Ouvrir http://localhost:5173
```

---

## Accès via le Frontend

Le frontend Vue.js est accessible à l'adresse suivante :

- **En local :** `http://localhost:5173`
- **Sur le réseau UBO :** `http://info-tpsi.univ-brest.fr:5173` (si Vite est lancé avec `--host`)

### Pages disponibles

| Page | URL | Description |
|------|-----|-------------|
| Accueil | `/` | Page d'accueil |
| Connexion | `/login` | Connexion utilisateur |
| Inscription | `/register` | Création de compte |
| Catalogue | `/films` | Liste des films |
| Détail film | `/films/:id` | Détail, location, avis, posters |
| Comptes | `/comptes` | Gestion des comptes (CRUD) |
| Mes locations | `/reservations` | Locations en cours (auth requise) |

### Fonctionnalités du frontend

- **Catalogue de films** : navigation, filtrage, affichage des posters
- **Location de films** : réservation avec modal de paiement, terminer une location
- **Évaluations** : consulter les avis, noter un film après l'avoir loué (note 1-4, commentaire)
- **Gestion des posters** : ajouter, modifier, supprimer les affiches depuis la page détail
- **Gestion des comptes** : création, modification, suppression via un tableau interactif
- **Authentification** : connexion par pseudo, header `X-User-Pseudo` envoyé automatiquement

---

## Bases de données

### MariaDB (SQL) — mps_db

| Table | Description |
|-------|-------------|
| `compte` | Comptes utilisateurs (pseudo, nom, prénom, date de naissance, rôle) |
| `film` | Catalogue de films (titre, année, âge minimum, louable) |
| `location` | Locations de films (compte_id, film_id, date_debut, date_fin) |

### MongoDB (NoSQL) — mps_db_sanndobtg

| Collection | Description |
|------------|-------------|
| `reviews` | Évaluations : titreFilm, pseudo, note, commentaire |
| `posters` | Affiches : titreFilm, nom (URL de l'image) |

---

## Tester les APIs

```bash
# Créer un compte
curl -X POST http://localhost:11080/comptes \
  -H "Content-Type: application/json" \
  -d '{"pseudo":"testUser","nom":"tonNom","prenom":"tonPrenom","dateDeNaissance":"1995-06-15","adresse":"Brest","mdp":"1234","role":"CLIENT"}'

# Réserver un film
curl -X POST http://localhost:11080/reservations/Inception \
  -H "X-User-Pseudo: testUser"

# Voir mes locations
curl http://localhost:11080/reservations/me \
  -H "X-User-Pseudo: testUser"

# Ajouter un avis
curl -X POST http://localhost:11080/reviews/Inception \
  -H "Content-Type: application/json" \
  -H "X-User-Pseudo: testUser" \
  -d '{"note":4,"commentaire":"Super film"}'

# Voir les avis
curl http://localhost:11080/reviews/Inception

# Voir la moyenne
curl http://localhost:11080/reviews/Inception/average

# Ajouter un poster
curl -X POST http://localhost:11080/posters/Inception \
  -H "Content-Type: application/json" \
  -d '{"nom":"https://image.tmdb.org/inception.jpg"}'

# Lister les posters
curl http://localhost:11080/posters/Inception
```
