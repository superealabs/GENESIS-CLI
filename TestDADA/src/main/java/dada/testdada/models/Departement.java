package dada.testdada.models;

import jakarta.persistence.*;

@Entity
@Table(name="departement")
public class Departement  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name="nom")
	private String nom;
	

	public Departement() {
	}

	public Departement(String nom) {
	   this.nom = nom;
	}

	public Departement(Long id, String nom) {
	   this.id = id;
	   this.nom = nom;
	}

	public Long getId() {
	   return id;
	}

	public void setId(Long id) {
	   this.id = id;
	}
	
	public String getNom() {
	   return nom;
	}

	public void setNom(String nom) {
	   this.nom = nom;
	}

}

