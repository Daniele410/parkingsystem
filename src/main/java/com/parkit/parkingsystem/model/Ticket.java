package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 
 * cette classe permet de stocker et de récupérer des valeurs dans la table des
 * tickets à partir de la base de données
 *
 */

public class Ticket {
	private int id;
	private ParkingSpot parkingSpot;
	private String vehicleRegNumber;
	private double price;
	private LocalDateTime inTime;
	private LocalDateTime outTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ParkingSpot getParkingSpot() {
		return parkingSpot;
	}

	public void setParkingSpot(ParkingSpot parkingSpot) {
		this.parkingSpot = parkingSpot;
	}

	public String getVehicleRegNumber() {
		return vehicleRegNumber;
	}

	public void setVehicleRegNumber(String vehicleRegNumber) {
		this.vehicleRegNumber = vehicleRegNumber;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public LocalDateTime getInTime() {
		return inTime;
	}

	public void setInTime(LocalDateTime localDateTime) {
		this.inTime = localDateTime;
	}

	public LocalDateTime getOutTime() {
		return outTime;
	}

	public void setOutTime(LocalDateTime outTime2) {
		this.outTime = outTime2;
	}

	public void setInTime(Date inTime2) {
		// TODO Auto-generated method stub
		
	}

	public void setOutTime(Date outTime2) {
		// TODO Auto-generated method stub
		
	}

}
