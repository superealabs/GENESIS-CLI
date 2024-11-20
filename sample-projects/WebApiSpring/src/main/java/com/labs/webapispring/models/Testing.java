package com.labs.webapispring.models;

import jakarta.persistence.*;

@Entity
@Table(name="testing")
public class Testing  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name="name")
	private String name;

	@Column(name="email")
	private String email;

	@Column(name="phone")
	private String phone;
	

	public Long getId() {
	   return id;
	}

	public void setId(Long id) {
	   this.id = id;
	}
	
	public String getName() {
	   return name;
	}

	public void setName(String name) {
	   this.name = name;
	}
	
	public String getEmail() {
	   return email;
	}

	public void setEmail(String email) {
	   this.email = email;
	}
	
	public String getPhone() {
	   return phone;
	}

	public void setPhone(String phone) {
	   this.phone = phone;
	}

}
