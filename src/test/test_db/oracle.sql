-- oracle.sql

/*
 SELECT username FROM dba_users order by USERNAME;
SELECT role FROM dba_roles order by role;


CREATE USER C##TEST_DB IDENTIFIED BY TEST_DB;
ALTER USER C##TEST_DB QUOTA UNLIMITED ON USERS;

GRANT CONNECT, RESOURCE TO C##TEST_DB;

-- Accorder le droit de créer des tables
GRANT CREATE TABLE TO C##TEST_DB;

-- Accorder le droit d'insérer des données dans ses propres tables
GRANT INSERT ANY TABLE TO C##TEST_DB;

-- Accorder le droit de mettre à jour des données dans ses propres tables
GRANT UPDATE ANY TABLE TO C##TEST_DB;

-- Accorder le droit de supprimer des données de ses propres tables
GRANT DELETE ANY TABLE TO C##TEST_DB;
 */

SELECT SYS_CONTEXT('USERENV', 'SESSION_USER')
FROM dual;
SELECT SYS_CONTEXT('USERENV', 'CURRENT_SCHEMA')
FROM dual;

CREATE TABLE Department
(
    DepartmentID NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    Name         VARCHAR2(50) NOT NULL
);

CREATE TABLE Employee
(
    EmployeeID   NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    LastName     VARCHAR2(50) NOT NULL,
    FirstName    VARCHAR2(50) NOT NULL,
    BirthDate    DATE,
    DepartmentID NUMBER,
    FOREIGN KEY (DepartmentID) REFERENCES Department (DepartmentID)
);

INSERT INTO Department (Name)
VALUES ('Information Technology'), ('Human Resources');

INSERT INTO Employee (LastName, FirstName, BirthDate, DepartmentID)
VALUES ('Dupont', 'Jean', TO_DATE('1985-06-15', 'YYYY-MM-DD'), 1);
INSERT INTO Employee (LastName, FirstName, BirthDate, DepartmentID)
VALUES ('Martin', 'Sophie', TO_DATE('1990-09-23', 'YYYY-MM-DD'), 2);
INSERT INTO Employee (LastName, FirstName, BirthDate, DepartmentID)
VALUES ('Durand', 'Luc', TO_DATE('1978-01-10', 'YYYY-MM-DD'), 1);