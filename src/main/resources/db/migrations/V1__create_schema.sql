-- V1__create_schema.sql
-- Clean schema creation for dev/demo environments.

DROP TABLE IF EXISTS perk_upvotes;
DROP TABLE IF EXISTS perk_downvotes;
DROP TABLE IF EXISTS account_memberships;
DROP TABLE IF EXISTS perks;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS memberships;
DROP TABLE IF EXISTS accounts;

-- ACCOUNTS
CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(100) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL
);

-- MEMBERSHIPS
CREATE TABLE memberships (
                             id BIGSERIAL PRIMARY KEY,
                             type VARCHAR(100),
                             org_name VARCHAR(200),
                             description TEXT
);

-- PRODUCTS
CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(200) NOT NULL,
                          description TEXT,
                          company VARCHAR(200) NOT NULL
);

-- PERKS
CREATE TABLE perks (
                       id BIGSERIAL PRIMARY KEY,
                       benefit TEXT NOT NULL,
                       expiry_date TIMESTAMP,
                       region VARCHAR(200),
                       membership_id BIGINT NOT NULL,
                       product_id BIGINT,
                       creator_id BIGINT,
                       CONSTRAINT fk_perk_membership FOREIGN KEY (membership_id) REFERENCES memberships(id) ON DELETE CASCADE,
                       CONSTRAINT fk_perk_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL,
                       CONSTRAINT fk_perk_creator FOREIGN KEY (creator_id) REFERENCES accounts(id) ON DELETE SET NULL
);

-- ACCOUNT_MEMBERSHIPS (many-to-many)
CREATE TABLE account_memberships (
                                     account_id BIGINT NOT NULL,
                                     membership_id BIGINT NOT NULL,
                                     joined_at TIMESTAMP DEFAULT NOW(),
                                     PRIMARY KEY (account_id, membership_id),
                                     CONSTRAINT fk_am_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
                                     CONSTRAINT fk_am_membership FOREIGN KEY (membership_id) REFERENCES memberships(id) ON DELETE CASCADE
);

-- PERK_UPVOTES (many-to-many)
CREATE TABLE perk_upvotes (
                              perk_id BIGINT NOT NULL,
                              account_id BIGINT NOT NULL,
                              created_at TIMESTAMP DEFAULT NOW(),
                              PRIMARY KEY (perk_id, account_id),
                              CONSTRAINT fk_up_perk FOREIGN KEY (perk_id) REFERENCES perks(id) ON DELETE CASCADE,
                              CONSTRAINT fk_up_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- PERK_DOWNVOTES (many-to-many)
CREATE TABLE perk_downvotes (
                                perk_id BIGINT NOT NULL,
                                account_id BIGINT NOT NULL,
                                created_at TIMESTAMP DEFAULT NOW(),
                                PRIMARY KEY (perk_id, account_id),
                                CONSTRAINT fk_down_perk FOREIGN KEY (perk_id) REFERENCES perks(id) ON DELETE CASCADE,
                                CONSTRAINT fk_down_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

CREATE INDEX idx_perks_membership ON perks (membership_id);
CREATE INDEX idx_perks_product ON perks (product_id);
CREATE INDEX idx_perks_creator ON perks (creator_id);
