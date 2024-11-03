package labs.layout1.models;

import jakarta.persistence.*;

@Entity
@Table(name = "employe")
public class Employe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employeid")
    private Long employeid;

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "datenaissance")
    private java.time.LocalDate datenaissance;

    @ManyToOne
    @JoinColumn(name = "departementid")
    private Departement departement;


    public Employe() {
    }

    public Employe(String nom, String prenom, java.time.LocalDate datenaissance, Departement departement) {
        this.nom = nom;
        this.prenom = prenom;
        this.datenaissance = datenaissance;
        this.departement = departement;
    }

    public Employe(Long employeid, String nom, String prenom, java.time.LocalDate datenaissance, Departement departement) {
        this.employeid = employeid;
        this.nom = nom;
        this.prenom = prenom;
        this.datenaissance = datenaissance;
        this.departement = departement;
    }

    public Long getEmployeid() {
        return employeid;
    }

    public void setEmployeid(Long employeid) {
        this.employeid = employeid;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public java.time.LocalDate getDatenaissance() {
        return datenaissance;
    }

    public void setDatenaissance(java.time.LocalDate datenaissance) {
        this.datenaissance = datenaissance;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

}

