package dada.testdada.models;

import jakarta.persistence.*;

@Entity
@Table(name="employe")
public class Employe  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name="nom")
	private String nom;

	@Column(name="prenom")
	private String prenom;

	@Column(name="date_naissance")
	private java.time.LocalDate dateNaissance;

	@ManyToOne
	@JoinColumn(name="dept_id")
	private Departement departement;
	

	public Employe() {
	}

	public Employe(String nom, String prenom, java.time.LocalDate dateNaissance, Departement departement) {
	   this.nom = nom;
	   this.prenom = prenom;
	   this.dateNaissance = dateNaissance;
	   this.departement = departement;
	}

	public Employe(Long id, String nom, String prenom, java.time.LocalDate dateNaissance, Departement departement) {
	   this.id = id;
	   this.nom = nom;
	   this.prenom = prenom;
	   this.dateNaissance = dateNaissance;
	   this.departement = departement;
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
	
	public String getPrenom() {
	   return prenom;
	}

	public void setPrenom(String prenom) {
	   this.prenom = prenom;
	}
	
	public java.time.LocalDate getDateNaissance() {
	   return dateNaissance;
	}

	public void setDateNaissance(java.time.LocalDate dateNaissance) {
	   this.dateNaissance = dateNaissance;
	}
	
	public Departement getDepartement() {
	   return departement;
	}

	public void setDepartement(Departement departement) {
	   this.departement = departement;
	}

}

