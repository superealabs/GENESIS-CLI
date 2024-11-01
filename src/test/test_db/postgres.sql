-- postgresql.sql

-- CREATE DATABASE post_db;
-- \c post_db;

CREATE TABLE Departement
(
    DepartementID SERIAL PRIMARY KEY,
    Nom    VARCHAR(50) NOT NULL
);

CREATE TABLE Employe
(
    EmployeID     SERIAL PRIMARY KEY,
    Nom           VARCHAR(50) NOT NULL,
    Prenom        VARCHAR(50) NOT NULL,
    DateNaissance DATE,
    DepartementID        INT,
    FOREIGN KEY (DepartementID) REFERENCES Departement (DepartementID)
);

INSERT INTO Departement (Nom)
VALUES ('Informatique'),
       ('Ressources Humaines');

INSERT INTO Employe (Nom, Prenom, DateNaissance, DepartementID)
VALUES ('Dupont', 'Jean', '1985-06-15', 1),
       ('Martin', 'Sophie', '1990-09-23', 2),
       ('Durand', 'Luc', '1978-01-10', 1);
