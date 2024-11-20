package com.labs.webapispring.models;

import jakarta.persistence.*;

@Entity
@Table(name="homes")
public class Home  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;

	@Column(name="address")
	private String address;

	@Column(name="city")
	private String city;

	@Column(name="state")
	private String state;

	@Column(name="zip")
	private Integer zip;

	@Column(name="price")
	private Double price;

	@Column(name="description")
	private String description;

	@Column(name="num_bedrooms")
	private Integer numBedrooms;

	@Column(name="num_bathrooms")
	private Integer numBathrooms;

	@Column(name="square_footage")
	private Double squareFootage;

	@Column(name="year_built")
	private Integer yearBuilt;

	@Column(name="has_pool")
	private Boolean hasPool;

	@Column(name="has_garage")
	private Boolean hasGarage;
	

	public Long getId() {
	   return id;
	}

	public void setId(Long id) {
	   this.id = id;
	}
	
	public String getAddress() {
	   return address;
	}

	public void setAddress(String address) {
	   this.address = address;
	}
	
	public String getCity() {
	   return city;
	}

	public void setCity(String city) {
	   this.city = city;
	}
	
	public String getState() {
	   return state;
	}

	public void setState(String state) {
	   this.state = state;
	}
	
	public Integer getZip() {
	   return zip;
	}

	public void setZip(Integer zip) {
	   this.zip = zip;
	}
	
	public Double getPrice() {
	   return price;
	}

	public void setPrice(Double price) {
	   this.price = price;
	}
	
	public String getDescription() {
	   return description;
	}

	public void setDescription(String description) {
	   this.description = description;
	}
	
	public Integer getNumBedrooms() {
	   return numBedrooms;
	}

	public void setNumBedrooms(Integer numBedrooms) {
	   this.numBedrooms = numBedrooms;
	}
	
	public Integer getNumBathrooms() {
	   return numBathrooms;
	}

	public void setNumBathrooms(Integer numBathrooms) {
	   this.numBathrooms = numBathrooms;
	}
	
	public Double getSquareFootage() {
	   return squareFootage;
	}

	public void setSquareFootage(Double squareFootage) {
	   this.squareFootage = squareFootage;
	}
	
	public Integer getYearBuilt() {
	   return yearBuilt;
	}

	public void setYearBuilt(Integer yearBuilt) {
	   this.yearBuilt = yearBuilt;
	}
	
	public Boolean getHasPool() {
	   return hasPool;
	}

	public void setHasPool(Boolean hasPool) {
	   this.hasPool = hasPool;
	}
	
	public Boolean getHasGarage() {
	   return hasGarage;
	}

	public void setHasGarage(Boolean hasGarage) {
	   this.hasGarage = hasGarage;
	}

}
