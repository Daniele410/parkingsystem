package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;

/**
 * 
 * Cette classe permet de stocker et de récupérer des valeurs dans la table des
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

	public void setInTime(LocalDateTime inTime) {
		this.inTime = inTime;
	}

	public LocalDateTime getOutTime() {
		return outTime;
	}

	public void setOutTime(LocalDateTime outTime) {
		this.outTime = outTime;
	}

	public ParkingSpot getParkingSpot() {
		return new ParkingSpot(parkingSpot.getId(), parkingSpot.getParkingType(),parkingSpot.isAvailable() );
	}
	
	public void setParkingSpot(ParkingSpot parkingSpot) {
		this.parkingSpot = new ParkingSpot (parkingSpot.getId(), parkingSpot.getParkingType(),parkingSpot.isAvailable());
	}

	


}
