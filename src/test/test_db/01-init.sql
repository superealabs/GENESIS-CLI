-- Création du schéma de la base de données pour la gestion des "mpiangona" dans la FLM
/*create database if not exists flm_dev;

\c flm_dev;*/
-- ========================================
-- 1. Tables de Référence (Statuts)
-- ========================================

-- Table statut_membre
CREATE TABLE statut_membre
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

-- Table statut_marial
CREATE TABLE statut_marial
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

-- Table statut_batisa
CREATE TABLE statut_batisa
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

-- Table statut_kofirmanda
CREATE TABLE statut_kofirmanda
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

-- Table statut_mariage
CREATE TABLE statut_mariage
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

-- Table statut_sortie
CREATE TABLE statut_sortie
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

-- Table statut_membre_sampana
CREATE TABLE statut_membre_sampana
(
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

-- ========================================
-- 2. Tables de l'Organisation
-- ========================================

-- Table synoda
CREATE TABLE synoda
(
    id                SERIAL PRIMARY KEY,
    libelle           VARCHAR(255) NOT NULL,
    sigle             VARCHAR(50)  NOT NULL UNIQUE,
    logo              VARCHAR(255),
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT
);

-- Table fileovana
CREATE TABLE fileovana
(
    id                SERIAL PRIMARY KEY,
    libelle           VARCHAR(255) NOT NULL,
    sigle             VARCHAR(50)  NOT NULL UNIQUE,
    logo              VARCHAR(255),
    synoda_id         INTEGER      NOT NULL,
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT,
    FOREIGN KEY (synoda_id) REFERENCES synoda (id)
);

-- Table fitandremana
CREATE TABLE fitandremana
(
    id                SERIAL PRIMARY KEY,
    libelle           VARCHAR(255) NOT NULL,
    sigle             VARCHAR(50)  NOT NULL UNIQUE,
    logo              VARCHAR(255),
    fileovana_id      INTEGER      NOT NULL,
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT,
    FOREIGN KEY (fileovana_id) REFERENCES fileovana (id)
);

-- Table fiangonana
CREATE TABLE fiangonana
(
    id                SERIAL PRIMARY KEY,
    libelle           VARCHAR(255) NOT NULL,
    logo              VARCHAR(255),
    fitandremana_id   INTEGER      NOT NULL,
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT,
    FOREIGN KEY (fitandremana_id) REFERENCES fitandremana (id)
);

-- Table faritra
CREATE TABLE faritra
(
    id                SERIAL PRIMARY KEY,
    libelle           VARCHAR(255) NOT NULL,
    numero            INTEGER      NOT NULL,
    sigle             VARCHAR(50)  NOT NULL UNIQUE,
    logo              VARCHAR(255),
    fiangonana_id     INTEGER      NOT NULL,
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT,
    FOREIGN KEY (fiangonana_id) REFERENCES fiangonana (id)
);

-- ========================================
-- 3. Tables des Personnes
-- ========================================

-- Table adresse
CREATE TABLE adresse
(
    id                SERIAL PRIMARY KEY,
    libelle           VARCHAR(255) NOT NULL,
    repere            VARCHAR(255),
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT
);

-- Table mpiangona
CREATE TABLE mpiangona
(
    id                               SERIAL PRIMARY KEY,
    nom                              VARCHAR(100) NOT NULL,
    prenom                           VARCHAR(100) NOT NULL,
    sexe                             VARCHAR(10)  NOT NULL CHECK (sexe IN ('Homme', 'Femme')),
    photo                            VARCHAR(255),
    date_naissance_jour              INTEGER CHECK (date_naissance_jour BETWEEN 1 AND 31),
    date_naissance_mois              INTEGER CHECK (date_naissance_mois BETWEEN 1 AND 12),
    date_naissance_annee             INTEGER      NOT NULL,
    date_naissance_est_approximative BOOLEAN   DEFAULT FALSE,
    lieu_naissance                   VARCHAR(255),
    pere_id                          INTEGER,
    mere_id                          INTEGER,
    conjoint_id                      INTEGER,
    adresse_id                       INTEGER,
    fiangonana_id                    INTEGER      NOT NULL,
    faritra_id                       INTEGER      NOT NULL,
    date_rejoint_fiangonana          DATE,
    date_insertion                   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification                TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    statut_membre_id                 INTEGER      NOT NULL,
    statut_marial_id                 INTEGER      NOT NULL,
    statut_batisa_id                 INTEGER      NOT NULL,
    statut_kofirmanda_id             INTEGER      NOT NULL,
    cin_numero                       VARCHAR(50),
    cin_date_delivrance              DATE,
    cin_lieu_delivrance              VARCHAR(255),
    remarque                         TEXT,
    FOREIGN KEY (pere_id) REFERENCES mpiangona (id),
    FOREIGN KEY (mere_id) REFERENCES mpiangona (id),
    FOREIGN KEY (conjoint_id) REFERENCES mpiangona (id),
    FOREIGN KEY (adresse_id) REFERENCES adresse (id),
    FOREIGN KEY (fiangonana_id) REFERENCES fiangonana (id),
    FOREIGN KEY (faritra_id) REFERENCES faritra (id),
    FOREIGN KEY (statut_membre_id) REFERENCES statut_membre (id),
    FOREIGN KEY (statut_marial_id) REFERENCES statut_marial (id),
    FOREIGN KEY (statut_batisa_id) REFERENCES statut_batisa (id),
    FOREIGN KEY (statut_kofirmanda_id) REFERENCES statut_kofirmanda (id)
);

-- Index pour accélérer les recherches sur le CIN
CREATE UNIQUE INDEX idx_mpiangona_cin_numero ON mpiangona (cin_numero) WHERE cin_numero IS NOT NULL;

-- Table pastora
CREATE TABLE pastora
(
    id                SERIAL PRIMARY KEY,
    nom               VARCHAR(100) NOT NULL,
    prenom            VARCHAR(100) NOT NULL,
    date_naissance    DATE,
    date_ordination   DATE,
    adresse_id        INTEGER,
    telephone         VARCHAR(20),
    email             VARCHAR(100),
    photo             VARCHAR(255),
    fiangonana_id     INTEGER,
    est_interime      BOOLEAN   DEFAULT FALSE,
    ordre_interime    INTEGER   DEFAULT 1,
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT,
    FOREIGN KEY (adresse_id) REFERENCES adresse (id),
    FOREIGN KEY (fiangonana_id) REFERENCES fiangonana (id)
);

-- ========================================
-- 4. Tables des Activités et Événements
-- ========================================

-- Table sampana
CREATE TABLE sampana
(
    id                SERIAL PRIMARY KEY,
    libelle           VARCHAR(255) NOT NULL,
    sigle             VARCHAR(50)  NOT NULL UNIQUE,
    logo              VARCHAR(255),
    fiangonana_id     INTEGER      NOT NULL,
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT,
    FOREIGN KEY (fiangonana_id) REFERENCES fiangonana (id)
);

-- Table membre_sampana
CREATE TABLE membre_sampana
(
    id                SERIAL PRIMARY KEY,
    mpiangona_id      INTEGER NOT NULL,
    sampana_id        INTEGER NOT NULL,
    date_rejoint      DATE,
    statut_id         INTEGER NOT NULL,
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT,
    FOREIGN KEY (mpiangona_id) REFERENCES mpiangona (id),
    FOREIGN KEY (sampana_id) REFERENCES sampana (id),
    FOREIGN KEY (statut_id) REFERENCES statut_membre_sampana (id)
);

-- Contrainte d'unicité pour éviter les doublons
CREATE UNIQUE INDEX idx_membre_sampana_unique ON membre_sampana (mpiangona_id, sampana_id);

-- Table batisa
CREATE TABLE batisa
(
    id                   SERIAL PRIMARY KEY,
    mpiangona_id         INTEGER NOT NULL UNIQUE,
    fiangonana_membre_id INTEGER NOT NULL,
    fiangonana_batisa_id INTEGER NOT NULL,
    vavolombelona_id     INTEGER NOT NULL,
    tuteur_id            INTEGER,
    pastora_id           INTEGER NOT NULL,
    date_batisa          DATE    NOT NULL,
    faritra_id           INTEGER NOT NULL,
    date_insertion       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque             TEXT,
    FOREIGN KEY (mpiangona_id) REFERENCES mpiangona (id),
    FOREIGN KEY (fiangonana_membre_id) REFERENCES fiangonana (id),
    FOREIGN KEY (fiangonana_batisa_id) REFERENCES fiangonana (id),
    FOREIGN KEY (vavolombelona_id) REFERENCES mpiangona (id),
    FOREIGN KEY (tuteur_id) REFERENCES mpiangona (id),
    FOREIGN KEY (pastora_id) REFERENCES pastora (id),
    FOREIGN KEY (faritra_id) REFERENCES faritra (id)
);

-- Table kofirmanda
CREATE TABLE kofirmanda
(
    id                       SERIAL PRIMARY KEY,
    mpiangona_id             INTEGER NOT NULL UNIQUE,
    fiangonana_membre_id     INTEGER NOT NULL,
    fiangonana_kofirmanda_id INTEGER NOT NULL,
    date_kofirmanda          DATE    NOT NULL,
    faritra_id               INTEGER NOT NULL,
    batisa_id                INTEGER NOT NULL,
    date_insertion           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque                 TEXT,
    FOREIGN KEY (mpiangona_id) REFERENCES mpiangona (id),
    FOREIGN KEY (fiangonana_membre_id) REFERENCES fiangonana (id),
    FOREIGN KEY (fiangonana_kofirmanda_id) REFERENCES fiangonana (id),
    FOREIGN KEY (faritra_id) REFERENCES faritra (id),
    FOREIGN KEY (batisa_id) REFERENCES batisa (id)
);

-- Table mariage_civile
CREATE TABLE mariage_civile
(
    id                   SERIAL PRIMARY KEY,
    epoux_id             INTEGER NOT NULL,
    epouse_id            INTEGER NOT NULL,
    date_mariage_civil   DATE    NOT NULL,
    informations_mariage TEXT,
    date_insertion       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque             TEXT,
    FOREIGN KEY (epoux_id) REFERENCES mpiangona (id),
    FOREIGN KEY (epouse_id) REFERENCES mpiangona (id)
);

-- Table mariage_religieux
CREATE TABLE mariage_religieux
(
    id                SERIAL PRIMARY KEY,
    epoux_id          INTEGER NOT NULL,
    epouse_id         INTEGER NOT NULL,
    mariage_civile_id INTEGER NOT NULL UNIQUE,
    date_mariage      DATE    NOT NULL,
    fiangonana_id     INTEGER NOT NULL,
    faritra_id        INTEGER NOT NULL,
    statut_id         INTEGER NOT NULL,
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT,
    FOREIGN KEY (epoux_id) REFERENCES mpiangona (id),
    FOREIGN KEY (epouse_id) REFERENCES mpiangona (id),
    FOREIGN KEY (mariage_civile_id) REFERENCES mariage_civile (id),
    FOREIGN KEY (fiangonana_id) REFERENCES fiangonana (id),
    FOREIGN KEY (faritra_id) REFERENCES faritra (id),
    FOREIGN KEY (statut_id) REFERENCES statut_mariage (id)
);

-- Table fandevenana
CREATE TABLE fandevenana
(
    id                SERIAL PRIMARY KEY,
    mpiangona_id      INTEGER NOT NULL UNIQUE,
    date_deces        DATE    NOT NULL,
    date_enterrement  DATE,
    fiangonana_id     INTEGER NOT NULL,
    pastora_id        INTEGER NOT NULL,
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT,
    FOREIGN KEY (mpiangona_id) REFERENCES mpiangona (id),
    FOREIGN KEY (fiangonana_id) REFERENCES fiangonana (id),
    FOREIGN KEY (pastora_id) REFERENCES pastora (id)
);

-- ========================================
-- 5. Tables de Mouvement et Statut
-- ========================================

-- Table niditra (Entrant FLM)
CREATE TABLE niditra
(
    id                    SERIAL PRIMARY KEY,
    mpiangona_id          INTEGER NOT NULL UNIQUE,
    fiangonana_origine_id INTEGER,
    pastora_id            INTEGER NOT NULL,
    date_reception        DATE    NOT NULL,
    faritra_id            INTEGER NOT NULL,
    statut_id             INTEGER NOT NULL,
    date_insertion        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque              TEXT,
    FOREIGN KEY (mpiangona_id) REFERENCES mpiangona (id),
    FOREIGN KEY (fiangonana_origine_id) REFERENCES fiangonana (id),
    FOREIGN KEY (pastora_id) REFERENCES pastora (id),
    FOREIGN KEY (faritra_id) REFERENCES faritra (id),
    FOREIGN KEY (statut_id) REFERENCES statut_membre (id)
);

-- Table nifindra (Sortant FLM)
CREATE TABLE nifindra
(
    id                        SERIAL PRIMARY KEY,
    mpiangona_id              INTEGER NOT NULL UNIQUE,
    fiangonana_destination_id INTEGER,
    pastora_id                INTEGER NOT NULL,
    date_sortie               DATE    NOT NULL,
    faritra_id                INTEGER NOT NULL,
    statut_sortie_id          INTEGER NOT NULL,
    date_insertion            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque                  TEXT,
    FOREIGN KEY (mpiangona_id) REFERENCES mpiangona (id),
    FOREIGN KEY (fiangonana_destination_id) REFERENCES fiangonana (id),
    FOREIGN KEY (pastora_id) REFERENCES pastora (id),
    FOREIGN KEY (faritra_id) REFERENCES faritra (id),
    FOREIGN KEY (statut_sortie_id) REFERENCES statut_sortie (id)
);

-- Table fanonganana (Déchéance)
CREATE TABLE fanonganana
(
    id                SERIAL PRIMARY KEY,
    mpiangona_id      INTEGER NOT NULL UNIQUE,
    date_fanonganana  DATE    NOT NULL,
    fiangonana_id     INTEGER NOT NULL,
    motif             VARCHAR(255),
    explication       TEXT,
    pastora_id        INTEGER NOT NULL,
    faritra_id        INTEGER NOT NULL,
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT,
    FOREIGN KEY (mpiangona_id) REFERENCES mpiangona (id),
    FOREIGN KEY (fiangonana_id) REFERENCES fiangonana (id),
    FOREIGN KEY (pastora_id) REFERENCES pastora (id),
    FOREIGN KEY (faritra_id) REFERENCES faritra (id)
);

-- Table fandraisana_fanonganana (Réintégration)
CREATE TABLE fandraisana_fanonganana
(
    id                SERIAL PRIMARY KEY,
    mpiangona_id      INTEGER NOT NULL UNIQUE,
    date_fandraisana  DATE    NOT NULL,
    fiangonana_id     INTEGER NOT NULL,
    fanonganana_id    INTEGER NOT NULL UNIQUE,
    motif             VARCHAR(255),
    explication       TEXT,
    pastora_id        INTEGER NOT NULL,
    faritra_id        INTEGER NOT NULL,
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque          TEXT,
    FOREIGN KEY (mpiangona_id) REFERENCES mpiangona (id),
    FOREIGN KEY (fiangonana_id) REFERENCES fiangonana (id),
    FOREIGN KEY (fanonganana_id) REFERENCES fanonganana (id),
    FOREIGN KEY (pastora_id) REFERENCES pastora (id),
    FOREIGN KEY (faritra_id) REFERENCES faritra (id)
);

-- Table niditra_hors_flm (Entrant Hors FLM)
CREATE TABLE niditra_hors_flm
(
    id                 SERIAL PRIMARY KEY,
    mpiangona_id       INTEGER NOT NULL UNIQUE,
    fiangonana_origine VARCHAR(255),
    date_reception     DATE    NOT NULL,
    pastora_id         INTEGER NOT NULL,
    faritra_id         INTEGER NOT NULL,
    statut_id          INTEGER NOT NULL,
    date_insertion     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque           TEXT,
    FOREIGN KEY (mpiangona_id) REFERENCES mpiangona (id),
    FOREIGN KEY (pastora_id) REFERENCES pastora (id),
    FOREIGN KEY (faritra_id) REFERENCES faritra (id),
    FOREIGN KEY (statut_id) REFERENCES statut_membre (id)
);

-- Table nifindra_hors_flm (Sortant Hors FLM)
CREATE TABLE nifindra_hors_flm
(
    id                     SERIAL PRIMARY KEY,
    mpiangona_id           INTEGER NOT NULL UNIQUE,
    fiangonana_destination VARCHAR(255),
    date_sortie            DATE    NOT NULL,
    pastora_id             INTEGER NOT NULL,
    faritra_id             INTEGER NOT NULL,
    statut_sortie_id       INTEGER NOT NULL,
    date_insertion         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarque               TEXT,
    FOREIGN KEY (mpiangona_id) REFERENCES mpiangona (id),
    FOREIGN KEY (pastora_id) REFERENCES pastora (id),
    FOREIGN KEY (faritra_id) REFERENCES faritra (id),
    FOREIGN KEY (statut_sortie_id) REFERENCES statut_sortie (id)
);

-- Table mpiangona_faritra_history
CREATE TABLE mpiangona_faritra_history
(
    id                SERIAL PRIMARY KEY,
    mpiangona_id      INTEGER NOT NULL,
    from_faritra_id   INTEGER,
    to_faritra_id     INTEGER NOT NULL,
    date_changement   DATE    NOT NULL,
    raison            TEXT,
    remarque          TEXT,
    date_insertion    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (mpiangona_id) REFERENCES mpiangona (id),
    FOREIGN KEY (from_faritra_id) REFERENCES faritra (id),
    FOREIGN KEY (to_faritra_id) REFERENCES faritra (id)
);

-- Contrainte pour s'assurer que le to_faritra_id est différent du from_faritra_id
ALTER TABLE mpiangona_faritra_history
    ADD CONSTRAINT chk_faritra_ids CHECK (from_faritra_id IS NULL OR from_faritra_id <> to_faritra_id);

-- ========================================
-- 6. Contraintes Supplémentaires
-- ========================================

-- Contrainte pour s'assurer qu'un mpiangona est baptisé avant la kofirmanda
CREATE FUNCTION check_batisa_before_kofirmanda() RETURNS trigger AS
$$
BEGIN
    IF (SELECT date_batisa FROM batisa WHERE mpiangona_id = NEW.mpiangona_id) > NEW.date_kofirmanda THEN
        RAISE EXCEPTION 'Le mpiangona doit être baptisé avant la kofirmanda.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_batisa_before_kofirmanda
    BEFORE INSERT OR UPDATE
    ON kofirmanda
    FOR EACH ROW
EXECUTE FUNCTION check_batisa_before_kofirmanda();

-- Contrainte pour s'assurer qu'un mpiangona ne peut avoir qu'une seule réintégration
ALTER TABLE fandraisana_fanonganana
    ADD CONSTRAINT unique_fandraisana_mpiangona UNIQUE (mpiangona_id);

-- ========================================
-- 7. Insertion des Données de Référence
-- ========================================

-- Insertion des statuts pour statut_membre
INSERT INTO statut_membre (code, description)
VALUES ('ACTIF', 'Membre actif du Fiangonana'),
       ('VAHINY', 'Personne de passage ou invité'),
       ('DECHU', 'Membre déchu');

-- Insertion des statuts pour statut_marial
INSERT INTO statut_marial (code, description)
VALUES ('CELIBATAIRE', 'Célibataire'),
       ('MARIE', 'Marié'),
       ('DIVORCE', 'Divorcé'),
       ('VEUF', 'Veuf'),
       ('REMARIE', 'Remarié');

-- Insertion des statuts pour statut_batisa
INSERT INTO statut_batisa (code, description)
VALUES ('FAIT', 'Baptême effectué'),
       ('NON_FAIT', 'Baptême non effectué');

-- Insertion des statuts pour statut_kofirmanda
INSERT INTO statut_kofirmanda (code, description)
VALUES ('FAIT', 'Confirmation effectuée'),
       ('NON_FAIT', 'Confirmation non effectuée');

-- Insertion des statuts pour statut_mariage
INSERT INTO statut_mariage (code, description)
VALUES ('MARIAGE', 'Mariage'),
       ('REMARIAGE', 'Remariage');

-- Insertion des statuts pour statut_sortie
INSERT INTO statut_sortie (code, description)
VALUES ('TEMPORAIRE', 'Sortie temporaire'),
       ('DEFINITIF', 'Sortie définitive');

-- Insertion des statuts pour statut_membre_sampana
INSERT INTO statut_membre_sampana (code, description)
VALUES ('ACTIF', 'Membre actif du Sampana'),
       ('QUITTE', 'A quitté le Sampana');

-- ========================================
-- 8. Indexation Supplémentaire
-- ========================================

-- Index sur les tables fréquemment jointes ou filtrées
CREATE INDEX idx_mpiangona_fiangonana_id ON mpiangona (fiangonana_id);
CREATE INDEX idx_mpiangona_faritra_id ON mpiangona (faritra_id);
CREATE INDEX idx_mpiangona_statut_membre_id ON mpiangona (statut_membre_id);

-- Index sur les dates pour optimiser les requêtes temporelles
CREATE INDEX idx_batisa_date ON batisa (date_batisa);
CREATE INDEX idx_kofirmanda_date ON kofirmanda (date_kofirmanda);
CREATE INDEX idx_mariage_civil_date ON mariage_civile (date_mariage_civil);
CREATE INDEX idx_mariage_religieux_date ON mariage_religieux (date_mariage);

-- ========================================
-- 9. Gestion des Permissions (Optionnel)
-- ========================================

-- Création de rôles et attribution de permissions peut être ajouté ici si nécessaire.

-- ========================================
-- 10. Fin du Script
-- ========================================

-- Le script de création de la base de données est terminé.