-- =====================================================================
-- Jeevadaana - Blood Donation Management System
-- MySQL schema (DDL). Hibernate also auto-creates these tables via
-- spring.jpa.hibernate.ddl-auto=update, but this file documents the
-- canonical schema and can be used to bootstrap the database manually.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS jeevadaana
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE jeevadaana;

-- ---------------------------------------------------------------------
-- Donors
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS donors (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(120) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    phone       VARCHAR(15)  NOT NULL,
    blood_group VARCHAR(12)  NOT NULL,
    gender      VARCHAR(10),
    age         INT,
    district    VARCHAR(80)  NOT NULL,
    address     VARCHAR(255),
    created_at  DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_donors_email (email)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Organizers
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS organizers (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    organization_name VARCHAR(150) NOT NULL,
    contact_person    VARCHAR(100) NOT NULL,
    email             VARCHAR(120) NOT NULL,
    password          VARCHAR(255) NOT NULL,
    phone             VARCHAR(15)  NOT NULL,
    district          VARCHAR(80)  NOT NULL,
    address           VARCHAR(255),
    created_at        DATETIME     NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_organizers_email (email)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Camps (organized by an organizer)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS camps (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(150) NOT NULL,
    organizer_id BIGINT       NOT NULL,
    district     VARCHAR(80)  NOT NULL,
    venue        VARCHAR(255) NOT NULL,
    camp_date    DATE         NOT NULL,
    start_time   TIME,
    end_time     TIME,
    capacity     INT,
    description  VARCHAR(1000),
    status       VARCHAR(12)  NOT NULL DEFAULT 'UPCOMING',
    created_at   DATETIME     NOT NULL,
    PRIMARY KEY (id),
    KEY idx_camps_district (district),
    KEY idx_camps_status (status),
    CONSTRAINT fk_camps_organizer FOREIGN KEY (organizer_id)
        REFERENCES organizers (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Camp registrations (a donor signing up for a camp)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS camp_registrations (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    camp_id       BIGINT      NOT NULL,
    donor_id      BIGINT      NOT NULL,
    status        VARCHAR(12) NOT NULL DEFAULT 'REGISTERED',
    registered_at DATETIME    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_camp_donor (camp_id, donor_id),
    CONSTRAINT fk_reg_camp  FOREIGN KEY (camp_id)  REFERENCES camps (id),
    CONSTRAINT fk_reg_donor FOREIGN KEY (donor_id) REFERENCES donors (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- Donations (recorded during post-camp management)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS donations (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    donor_id      BIGINT      NOT NULL,
    camp_id       BIGINT,
    blood_group   VARCHAR(12) NOT NULL,
    units_ml      INT         NOT NULL DEFAULT 450,
    donation_date DATE        NOT NULL,
    remarks       VARCHAR(255),
    created_at    DATETIME    NOT NULL,
    PRIMARY KEY (id),
    KEY idx_donations_donor (donor_id),
    CONSTRAINT fk_don_donor FOREIGN KEY (donor_id) REFERENCES donors (id),
    CONSTRAINT fk_don_camp  FOREIGN KEY (camp_id)  REFERENCES camps (id)
) ENGINE=InnoDB;
