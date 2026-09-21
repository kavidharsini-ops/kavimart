-- Demo accounts are stored with bcrypt hashes only.
MERGE INTO users (name, email, password_hash, role) KEY(email)
VALUES ('Mart Administrator', 'admin@kavimart.local',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');

MERGE INTO users (name, email, password_hash, role) KEY(email)
VALUES ('Demo Buyer', 'buyer@kavimart.local',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'BUYER');

MERGE INTO users (name, email, password_hash, role) KEY(email)
VALUES ('Demo Seller', 'seller@kavimart.local',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'SELLER');