-- sqlserver.sql

CREATE DATABASE test_db;
USE test_db;

CREATE TABLE Departement
(
    DeptID INT PRIMARY KEY IDENTITY(1,1),
    Nom NVARCHAR(50) NOT NULL
);

CREATE TABLE Employe
(
    EmployeID INT PRIMARY KEY IDENTITY(1,1),
    Nom NVARCHAR(50) NOT NULL,
    Prenom NVARCHAR(50) NOT NULL,
    DateNaissance DATE NULL,
    DeptID INT NULL,
    CONSTRAINT FK_Employe_Departement FOREIGN KEY (DeptID) REFERENCES Departement(DeptID)
);

INSERT INTO Departement (Nom)
VALUES ('Informatique'),
       ('Ressources Humaines');

INSERT INTO Employe (Nom, Prenom, DateNaissance, DeptID)
VALUES ('Dupont', 'Jean', '1985-06-15', 1),
       ('Martin', 'Sophie', '1990-09-23', 2),
       ('Durand', 'Luc', '1978-01-10', 1);
