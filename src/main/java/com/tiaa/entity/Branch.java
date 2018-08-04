package com.tiaa.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder({ "location", "totalcollection", "sumoforder", "locationid" })
public class Branch {

	protected String location;
	protected float totalcollection;
	protected float sumoforder;
	protected String locationid;
	protected boolean correctAccounting;

	/**
	 * Gets the value of the location property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the value of the location property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setLocation(String value) {
		this.location = value;
	}

	/**
	 * Gets the value of the totalcollection property.
	 * 
	 */
	public float getTotalcollection() {
		return totalcollection;
	}

	/**
	 * Sets the value of the totalcollection property.
	 * 
	 */
	public void setTotalcollection(float value) {
		this.totalcollection = value;
	}

	public float getSumoforder() {
		return sumoforder;
	}

	public void setSumoforder(float sumoforder) {
		this.sumoforder = sumoforder;
	}

	/**
	 * Gets the value of the locationid property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getLocationid() {
		return locationid;
	}

	/**
	 * Sets the value of the locationid property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setLocationid(String value) {
		this.locationid = value;
	}

	public boolean isCorrectAccounting() {
		return correctAccounting;
	}

	public void setCorrectAccounting(boolean correctAccounting) {
		this.correctAccounting = correctAccounting;
	}

}
