-- postgresql.sql

--CREATE DATABASE test_db;
--\c test_db;

create sequence departement_deptid_seq
    as integer;

create sequence employe_employeid_seq
    as integer;

create table if not exists departement
(
    dept_id bigint default nextval('departement_deptid_seq'::regclass) not null
        constraint departement_pkey
            primary key,
    nom     varchar(50)                                                not null
);

alter sequence departement_deptid_seq owned by departement.dept_id;

create table if not exists employe
(
    employe_id     bigint default nextval('employe_employeid_seq'::regclass) not null
        constraint employe_pkey
            primary key,
    nom            varchar(50)                                               not null,
    prenom         varchar(50)                                               not null,
    date_naissance date,
    dept_id        bigint
        constraint employe_deptid_fkey
            references departement
);

alter sequence employe_employeid_seq owned by employe.employe_id;



INSERT INTO departement (Nom)
VALUES ('Informatique'),
       ('Ressources Humaines');

INSERT INTO employe (Nom, Prenom, Date_Naissance, Dept_ID)
VALUES ('Dupont', 'Jean', '1985-06-15', 1),
       ('Martin', 'Sophie', '1990-09-23', 2),
       ('Durand', 'Luc', '1978-01-10', 1);
