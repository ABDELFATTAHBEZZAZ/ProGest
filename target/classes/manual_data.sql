-- Insertion des utilisateurs
INSERT INTO users (username, password, email, full_name, role, enabled, connected) VALUES
('admin', '$2a$10$wWw...', 'admin@gestion-maroc.com', 'Mohamed Benjelloun', 'ADMIN', 1, 0),
('karim', '$2a$10$wWw...', 'karim.alami@gestion-maroc.com', 'Karim Alami', 'UTILISATEUR', 1, 0),
('salma', '$2a$10$wWw...', 'salma.bennani@gestion-maroc.com', 'Salma Bennani', 'UTILISATEUR', 1, 0);

-- Insertion des clients (Maroc + Données Utilisateur)
INSERT INTO clients (nom, email, telephone, adresse, ville, code_postal, pays, created_at) VALUES
('bezzaz', 'abdelfattahbezzaz@gmail.com', '0676879876', 'Rés Azzahrae 2', 'SALE', '11112', 'MAROC', '2025-12-26 22:42:32'),
('anas', 'anas@gmail.com', '0676879876', 'Rés Azzahrae', 'SALE', '11112', 'Maroc', '2026-01-03 08:12:11'),
('Maroc Telecom', 'contact@iam.ma', '0537719000', 'Avenue Annakhil', 'Rabat', '10000', 'Maroc', NOW()),
('Groupe Addoha', 'info@addoha.ma', '0522345678', 'Ain Sebaa', 'Casablanca', '20250', 'Maroc', NOW());

-- Insertion des produits
INSERT INTO produits (nom, reference, description, prix_unitaire, tva, stock, actif) VALUES
('Développement Site Web Vitrine', 'SRV-DEV-001', 'Site web institutionnel 5 pages', 5000.00, 20.00, 999, 1),
('Maintenance Mensuelle', 'SRV-MAINT-002', 'Maintenance corrective et évolutive', 1500.00, 20.00, 999, 1),
('PC Portable Dell XPS', 'MAT-PC-003', 'i7, 16GB RAM, 512GB SSD', 18000.00, 20.00, 10, 1),
('Licence Office 365', 'LIC-OFFICE-004', 'Abonnement annuel Business', 1200.00, 20.00, 50, 1);
