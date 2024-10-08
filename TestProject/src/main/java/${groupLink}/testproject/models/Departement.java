package java..departement.models

import jakarta.persistence.*;

@Entity
@Table(name="departement")
public class Departement extends JpaRepository<Departement, >{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="deptid")
	private Long deptid;

	@Column(name="nom")
	private String nom;
	

	public Departement() {
	}

	public Departement(, String nom) {
	   this.nom = nom;
	}

	public Departement(Long deptid, String nom) {
	   this.deptid = deptid;
	   this.nom = nom;
	}

	public Long getDeptid() {
	   return deptid;
	}

	public void setDeptid(Long deptid) {
	   this.deptid = deptid;
	}
	
	public String getNom() {
	   return nom;
	}

	public void setNom(String nom) {
	   this.nom = nom;
	}

}

