-- postgresql.sql

--CREATE DATABASE test_db;
--\c test_db;

create table departement
(
    id  serial primary key,
    nom varchar(50) not null
);

create table employe
(
    id             serial primary key,
    nom            varchar(50) not null,
    prenom         varchar(50) not null,
    date_naissance date,
    dept_id        integer,
    foreign key (dept_id) references departement
);


INSERT INTO Departement (Nom)
VALUES ('Informatique'),
       ('Ressources Humaines');

INSERT INTO Employe (Nom, Prenom, Date_Naissance, Dept_ID)
VALUES ('Dupont', 'Jean', '1985-06-15', 1),
       ('Martin', 'Sophie', '1990-09-23', 2),
       ('Durand', 'Luc', '1978-01-10', 1);
