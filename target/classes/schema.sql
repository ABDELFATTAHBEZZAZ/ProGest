-- Database initialization script for Gestion Commerciale
-- MySQL Database on port 8000

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS gestion_commerciale CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestion_commerciale;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    full_name VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'UTILISATEUR',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    connected BOOLEAN NOT NULL DEFAULT FALSE,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Clients table
CREATE TABLE IF NOT EXISTS clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telephone VARCHAR(20),
    adresse VARCHAR(255),
    ville VARCHAR(100),
    code_postal VARCHAR(10),
    pays VARCHAR(50) DEFAULT 'France',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE IF NOT EXISTS produits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    reference VARCHAR(50),
    categorie VARCHAR(50),
    tva DECIMAL(5,2) DEFAULT 20.00,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Devis (Quotes) table
CREATE TABLE IF NOT EXISTS devis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL,
    created_by_id BIGINT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_validite DATE,
    montant_ht DECIMAL(12,2) DEFAULT 0.00,
    montant_tva DECIMAL(12,2) DEFAULT 0.00,
    montant_ttc DECIMAL(12,2) DEFAULT 0.00,
    statut VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (created_by_id) REFERENCES users(id)
);

-- Devis details (line items) table
CREATE TABLE IF NOT EXISTS devis_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    devis_id BIGINT NOT NULL,
    produit_id BIGINT NOT NULL,
    quantite INT NOT NULL DEFAULT 1,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    tva DECIMAL(5,2) DEFAULT 20.00,
    remise DECIMAL(5,2) DEFAULT 0.00,
    FOREIGN KEY (devis_id) REFERENCES devis(id) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES produits(id)
);

-- Factures (Invoices) table
CREATE TABLE IF NOT EXISTS factures (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) NOT NULL UNIQUE,
    client_id BIGINT NOT NULL,
    devis_id BIGINT,
    created_by_id BIGINT,
    date_facture TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_echeance DATE,
    montant_ht DECIMAL(12,2) DEFAULT 0.00,
    montant_tva DECIMAL(12,2) DEFAULT 0.00,
    montant_ttc DECIMAL(12,2) DEFAULT 0.00,
    montant_paye DECIMAL(12,2) DEFAULT 0.00,
    montant_restant DECIMAL(12,2) DEFAULT 0.00,
    statut VARCHAR(30) NOT NULL DEFAULT 'NON_PAYEE',
    mode_paiement VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (devis_id) REFERENCES devis(id),
    FOREIGN KEY (created_by_id) REFERENCES users(id)
);

-- Messages table for chat
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT,
    recipient_id BIGINT,
    client_id BIGINT,
    devis_id BIGINT,
    facture_id BIGINT,
    type VARCHAR(10) NOT NULL DEFAULT 'TEXT',
    content TEXT,
    audio_path VARCHAR(255),
    image_path VARCHAR(255),
    room_id VARCHAR(50),
    is_read BOOLEAN DEFAULT FALSE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (recipient_id) REFERENCES users(id),
    FOREIGN KEY (client_id) REFERENCES clients(id),
    FOREIGN KEY (devis_id) REFERENCES devis(id),
    FOREIGN KEY (facture_id) REFERENCES factures(id)
);

-- Indexes for performance
CREATE INDEX idx_devis_client ON devis(client_id);
CREATE INDEX idx_devis_status ON devis(statut);
CREATE INDEX idx_factures_client ON factures(client_id);
CREATE INDEX idx_factures_status ON factures(statut);
CREATE INDEX idx_messages_room ON messages(room_id);
CREATE INDEX idx_messages_sender ON messages(sender_id);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, password, email, full_name, role, enabled, connected) 
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.l7mZ3L7rNJQnJqSjqW', 'admin@gestion.com', 'Administrateur', 'ADMIN', TRUE, FALSE)
ON DUPLICATE KEY UPDATE username=username;

-- Insert sample data
INSERT INTO clients (nom, email, telephone, ville) VALUES 
    ('Société ABC', 'contact@abc.fr', '0123456789', 'Paris'),
    ('Entreprise XYZ', 'info@xyz.com', '0987654321', 'Lyon'),
    ('Client Test', 'test@client.fr', '0611223344', 'Marseille')
ON DUPLICATE KEY UPDATE nom=nom;

INSERT INTO produits (nom, description, prix_unitaire, stock, reference, categorie, tva, actif) VALUES
    ('Service Consultation', 'Consultation informatique 1h', 75.00, 999, 'SRV-001', 'Services', 20.00, TRUE),
    ('Développement Web', 'Développement site web', 500.00, 999, 'DEV-001', 'Services', 20.00, TRUE),
    ('Maintenance Annuelle', 'Contrat maintenance annuel', 1200.00, 999, 'MTN-001', 'Services', 20.00, TRUE),
    ('Licence Logiciel', 'Licence logiciel pro', 299.00, 50, 'LIC-001', 'Logiciels', 20.00, TRUE)
ON DUPLICATE KEY UPDATE nom=nom;
