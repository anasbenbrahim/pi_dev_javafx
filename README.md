# 🌾 AgriConnect - Agricultural Management System

AgriConnect est une plateforme hybride développée dans le cadre du module **PIDEV 3A** à **Esprit School of Engineering**. Elle combine une application de bureau JavaFX et une interface web basée sur Symfony afin d’assister les agriculteurs dans la gestion de leurs activités : publications, produits, offres d’emploi, événements, commandes et interactions clients.

---

## 📚 Table des matières

- [🌟 Fonctionnalités](#-fonctionnalités)
- [🔐 Gestion des Utilisateurs](#-gestion-des-utilisateurs)
- [💻 Technologies Utilisées](#-technologies-utilisées)
- [🛠️ Installation](#-installation)
- [🚀 Utilisation](#-utilisation)
- [👥 Rôles Utilisateurs](#-rôles-utilisateurs)
- [📬 Notifications](#-notifications)
- [🤝 Contribution](#-contribution)
- [📄 Licence](#-licence)
- [👨‍💻 Crédits](#-crédits)
- [🔖 Topics GitHub](#-topics-github-à-configurer-dans-le-dépôt)
- [☁️ Hébergement](#️-hébergement-facultatif)

---

## 🌟 Fonctionnalités

### 📢 Gestion des Publications
- CRUD complet pour publications, commentaires et réclamations
- Filtrage de propos inappropriés via **PurgoMalum API**
- Traduction automatique des commentaires via **MyMemory Translation API**
- Dashboard administrateur pour approuver ou supprimer des réclamations
- Notification email à l'approbation

### 👨‍🌾 Gestion d’Emploi
- Fermiers publient des offres d’emploi
- Clients postulent via un formulaire + dépôt de CV
- Confirmation par SMS via **Twilio API**
- Export PDF des candidatures avec QR code

### 📅 Gestion d’Événements
- Planification et gestion d’événements
- Affichage calendrier + prédiction de popularité
- Réservation clients + invitation par email et QR code d’entrée

### 🛒 Gestion Produits & Catégories
- Ajout, édition et visualisation des produits
- Classement par catégorie
- Alertes de stock faible ou rupture
- Prédiction de ventes sur 3 mois

### 💳 Gestion Commandes & Paiements
- Panier et gestion des commandes
- Intégration **Stripe** pour le paiement
- Historique des commandes clients

---

## 🔐 Gestion des Utilisateurs

Dans ce projet, la gestion des utilisateurs couvre **quatre types d'espaces** : **Admin, Fermier, Fournisseur et Client**.

- Chaque utilisateur dispose d’un **profil personnel** pour afficher ses informations et modifier son mot de passe.
- L’inscription inclut une **vérification par email**.
- L’administrateur peut **ajouter manuellement des utilisateurs**, avec un mot de passe généré automatiquement, envoyé par **email** ou **SMS (Twilio)**.
- Fonctionnalité **mot de passe oublié** avec envoi d’un **code de réinitialisation par email**.
- Opérations **CRUD complètes** pour la gestion des comptes utilisateurs.
- Possibilité de **bannir** ou **débannir** un compte.
- Une section **statistiques utilisateurs** permet d’afficher les données depuis la base (nombre par rôle, comptes actifs/inactifs, etc.).

---

## 💻 Technologies Utilisées

- **Java 17** (JavaFX Desktop)
- **Symfony 6.2** (Web)
- **PHP 8** (XAMPP)
- **MySQL** (Base de données)
- **JDBC / Doctrine ORM**
- **Apache PDFBox** (PDF export)
- **ZXing** (QR code)
- **Twilio API** (SMS)
- **Cloudinary** (Hébergement d’images)
- **Stripe API** (Paiements)
- **JavaMail / Symfony Mailer** (Emails)
- **PurgoMalum API** (Filtrage de langage inapproprié)
- **MyMemory Translation API** (Traduction de commentaires)

---

## 🛠️ Installation

### Prérequis
- Java 17
- PHP (pour Symfony)
- Composer & Maven
- XAMPP (MySQL)

### Étapes

1. Cloner les dépôts :
```bash
git clone https://github.com/anasbenbrahim/pi_dev_javafx.git
git clone https://github.com/anasbenbrahim/pi_dev_vultres_codex.git
```

2. Créer la base de données via les scripts dans `/sql`.

3. Configurer la connexion à la base :
   - JavaFX : `DataSource.java`
   - Symfony : fichier `.env`

4. Installer les dépendances :
```bash
composer install
mvn clean install
```

5. Lancer l'application JavaFX :
```bash
MainFX.java
```

6. Démarrer le serveur Symfony :
```bash
symfony server:start
```

---

## 🚀 Utilisation

- L'application bureau permet aux fermiers et clients de gérer les fonctionnalités localement.
- L’interface web est principalement destinée à l’administration (modération, consultation globale).
- Les deux communiquent avec la base via JDBC (Java) ou Doctrine (Symfony).

---

## 👥 Rôles Utilisateurs

| Rôle        | Fonctions principales |
|-------------|------------------------|
| **Fermier** | Gère publications, offres, produits, événements |
| **Client**  | Postule, commente, réserve, commande |
| **Fournisseur** | Gère des produits |
| **Admin**   | Gère les utilisateurs & réclamations |

---

## 📬 Notifications

- 📩 Email : Réclamations approuvées, réservation événement, inscription
- 📱 SMS : Confirmation de candidature, mot de passe généré
- 🔔 In-app : Alertes de stock critique

---

## 🤝 Contribution

Les contributions sont encouragées via des **pull requests**. Veuillez :
- Créer une branche claire (`feature/nom`)
- Ajouter des commentaires explicites
- Respecter la structure MVC et la convention de code

---

## 📄 Licence

Ce projet est développé à des **fins pédagogiques** dans le cadre du module **PIDEV 3A** à **Esprit School of Engineering**. Toute utilisation commerciale est interdite.

---

## 👨‍💻 Crédits

Développé par l’équipe **vultres_codex** :
- Mohamed Amine Graja  
- Dhia Mannai  
- Mohamed Aziz Jribi  
- Yassin Hsaoui  
- Mohamed Ben Khebab  
- Anas Ben Brahim

---

## 🔖 Topics GitHub à configurer dans le dépôt

```
java, javafx, symfony, php, mysql, esprit, esprit-school-of-engineering, api-integration, xampp, stripe, twilio, crud, purgomalum, mymemory
```

---

## ☁️ Hébergement (facultatif)

- Base de données locale via **XAMPP**
- Application JavaFX lancée localement
- Interface Symfony hébergée localement via `symfony server:start`
---

## 🧰 Gestion des Équipements & Fournisseur

- En tant que **fournisseur**, je peux **ajouter des équipements** ainsi que leurs **catégories**.
- Côté **front office**, les **fermiers** peuvent consulter les équipements disponibles et **demander un devis** pour acheter l’outil souhaité.
- Le **fournisseur** peut ensuite **accepter ou refuser le devis** en envoyant une réponse personnalisée.
- Un tableau de **statistiques** permet au fournisseur d’avoir une **vue sur les ventes** et le **stock restant**.

### 🤖 Fonctionnalités supplémentaires

- **API de génération d’image (imagegen)** pour insérer des **illustrations expressives et de haute qualité** dans les fiches équipements et publications.
- **Chatbot intégré** pour répondre aux **questions agricoles** courantes et orienter les utilisateurs.

---
