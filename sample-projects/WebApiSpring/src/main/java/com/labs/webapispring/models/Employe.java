package com.labs.webapispring.models;

import jakarta.persistence.*;

@Entity
@Table(name="employes")
public class Employe  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="employe_id")
	private Long employeId;

	@Column(name="nom")
	private String nom;

	@Column(name="prenom")
	private String prenom;

	@Column(name="date_naissance")
	private java.time.LocalDate dateNaissance;

	@ManyToOne
	@JoinColumn(name="dept_id")
	private Departement deptidDepartements;
	

	public Long getEmployeId() {
	   return employeId;
	}

	public void setEmployeId(Long employeId) {
	   this.employeId = employeId;
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
	
	public Departement getDeptidDepartements() {
	   return deptidDepartements;
	}

	public void setDeptidDepartements(Departement deptidDepartements) {
	   this.deptidDepartements = deptidDepartements;
	}

}
