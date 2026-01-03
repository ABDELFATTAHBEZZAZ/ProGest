# 🏢 ProGest - Application de Gestion Commerciale

Application web complète de gestion commerciale avec chat en temps réel, développée avec Spring Boot.

## ✨ Fonctionnalités

### 📊 Gestion Commerciale
- **Clients** : CRUD complet avec informations détaillées
- **Produits** : Catalogue avec gestion de stock et TVA
- **Devis** : Création, validation, export PDF
- **Factures** : Génération automatique depuis les devis, suivi des paiements

### 💬 Communication
- **Chat temps réel** : Messagerie instantanée via WebSocket
- **Messages contextuels** : Lier les discussions aux clients/devis/factures
- **Support multimédia** : Texte, audio et images

### 📈 Tableau de Bord
- Statistiques de ventes
- Chiffre d'affaires mensuel
- Devis en attente

## 🛠️ Technologies

| Catégorie | Technologies |
|-----------|-------------|
| **Backend** | Spring Boot 3.2, Spring Security, Spring Data JPA |
| **Base de données** | MySQL 8 |
| **Sécurité** | JWT (JSON Web Tokens), BCrypt |
| **Temps réel** | WebSocket (STOMP) |
| **PDF** | iText 7 |
| **Frontend** | HTML5, CSS3, JavaScript, Bootstrap 5 |

## 🚀 Installation

### Prérequis
- Java 17+
- Maven 3.8+
- MySQL 8+

### Configuration

1. **Cloner le projet**
```bash
git clone https://github.com/ABDELFATTAHBEZZAZ/ProGest.git
cd ProGest
```

2. **Configurer la base de données** (`src/main/resources/application.properties`)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/gestion_commerciale
spring.datasource.username=root
spring.datasource.password=root
```

3. **Lancer l'application**
```bash
mvn spring-boot:run
```

4. **Accéder à l'application**
```
http://localhost:8081
```

## 👤 Comptes par défaut

| Rôle | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Vendeur | karim | karim123 |
| Vendeur | salma | salma123 |

## 📁 Structure du Projet

```
src/main/java/com/gestion/
├── config/          # Configuration (DataInitializer)
├── controller/      # Contrôleurs REST
├── dto/             # Objets de transfert
├── entity/          # Entités JPA
├── repository/      # Repositories Spring Data
├── security/        # JWT & Spring Security
├── service/         # Logique métier
└── websocket/       # Configuration WebSocket

src/main/resources/
├── static/          # Frontend (HTML, CSS, JS)
└── application.properties
```

## 🔐 API Endpoints

### Authentification
- `POST /auth/login` - Connexion
- `POST /auth/register` - Inscription

### Clients
- `GET /api/clients` - Liste des clients
- `POST /api/clients` - Créer un client

### Devis
- `GET /api/devis` - Liste des devis
- `POST /api/devis` - Créer un devis
- `GET /api/devis/{id}/pdf` - Export PDF

### Factures
- `GET /api/factures` - Liste des factures
- `POST /api/factures/from-devis/{id}` - Convertir devis en facture

## 📊 Diagramme de Classes

```
Client (1) ──o (*) Devis ──o (*) DevisDetail ──> (1) Produit
   │                │
   │                └── (0..1) Facture
   │
   └──o (*) Facture

User (1) ──o (*) Devis/Facture (créateur)
User (1) ──o (*) Message (sender/recipient)
```

## 👨‍💻 Auteur

**ABDELFATTAH BEZZAZ**

---

⭐ N'hésitez pas à mettre une étoile si ce projet vous a été utile !
