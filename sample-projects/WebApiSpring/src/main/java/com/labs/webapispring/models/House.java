package com.labs.webapispring.models;

import jakarta.persistence.*;

@Entity
@Table(name="houses")
public class House  {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="house_id")
	private Long houseId;

	@Column(name="address")
	private String address;

	@Column(name="city")
	private String city;

	@Column(name="state")
	private String state;

	@Column(name="zip_code")
	private String zipCode;

	@Column(name="num_bedrooms")
	private Integer numBedrooms;

	@Column(name="num_bathrooms")
	private Integer numBathrooms;

	@Column(name="square_footage")
	private Integer squareFootage;

	@Column(name="price")
	private Double price;

	@Column(name="year_built")
	private Integer yearBuilt;
	

	public Long getHouseId() {
	   return houseId;
	}

	public void setHouseId(Long houseId) {
	   this.houseId = houseId;
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
	
	public String getZipCode() {
	   return zipCode;
	}

	public void setZipCode(String zipCode) {
	   this.zipCode = zipCode;
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
	
	public Integer getSquareFootage() {
	   return squareFootage;
	}

	public void setSquareFootage(Integer squareFootage) {
	   this.squareFootage = squareFootage;
	}
	
	public Double getPrice() {
	   return price;
	}

	public void setPrice(Double price) {
	   this.price = price;
	}
	
	public Integer getYearBuilt() {
	   return yearBuilt;
	}

	public void setYearBuilt(Integer yearBuilt) {
	   this.yearBuilt = yearBuilt;
	}

}
