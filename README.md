# EniManageBackEnd

## Présentation du projet
EniManageBackEnd est la partie backend du projet EniManage développé en **Spring Boot** pour la gestion des événements, des ressources et des interactions utilisateurs au sein d'**Enicarthage**. Ce système offre une architecture modulaire axée sur la sécurité et les capacités temps réel.

---

## Fonctionnalités principales

### Sécurité & Authentification
- Authentification basée sur **JWT** (JSON Web Tokens)
- Gestion fine des rôles et permissions
- Connexions sécurisées via **WebSocket**
- Protection des endpoints API

### Communication temps réel
- Intégration WebSocket complète
- Chat en direct entre utilisateurs
- Mises à jour instantanées des événements
- Notifications en temps réel

### APIs RESTful
- Endpoints complets pour la gestion des :
  - **Utilisateurs** (inscription, connexion, profils)
  - **Événements** (création, modification, suppression)
  - **Ressources** (affectation, gestion)
  - **Salles** (réservation, disponibilité)

### Tests robustes
- Tests unitaires étendus
- Tests d'intégration
- Vérification de l'intégrité des données
- Assurance qualité du système

---

## Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- **Java** : JDK 11 ou version supérieure
- **Maven** : Version 3.6 ou supérieure
- **Base de données** : Configuration selon votre environnement

---
