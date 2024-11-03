package labs.layout1.models;

import jakarta.persistence.*;

@Entity
@Table(name = "departement")
public class Departement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "departementid")
    private Long departementid;

    @Column(name = "nom")
    private String nom;


    public Departement() {
    }

    public Departement(String nom) {
        this.nom = nom;
    }

    public Departement(Long departementid, String nom) {
        this.departementid = departementid;
        this.nom = nom;
    }

    public Long getDepartementid() {
        return departementid;
    }

    public void setDepartementid(Long departementid) {
        this.departementid = departementid;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

}

