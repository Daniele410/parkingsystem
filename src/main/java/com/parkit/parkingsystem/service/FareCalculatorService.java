package com.parkit.parkingsystem.service;

import java.time.Duration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

/**
 * 
 * cette classe calcule le prix payé par l'utilisateur pour sortir du parking
 *
 */
public class FareCalculatorService {

	private TicketDAO ticketDAO;
	


	public void setTicketDAO(TicketDAO ticketDAO) {
		this.ticketDAO = ticketDAO;
		
	}
	
	
	/**
     * Fare Calculator for a specific ticket.
     * @param ticket the ticket.
     */

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString() + " Intime" +  ticket.getInTime());
		}

		double duration = calculateTimeInParking(ticket);
		double discount = calculateDicount(ticket.getVehicleRegNumber());

		// Durée mise à zéro si moins de 30 minutes
		if (duration < 30) {
			duration = 0;
		}

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			ticket.setPrice(duration * Fare.CAR_RATE_PER_MINUTE * discount);
			break;
		}
		case BIKE: {
			ticket.setPrice(duration * Fare.BIKE_RATE_PER_MINUTE * discount);

			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}

	}

	public double calculateDicount(String vehicleRegNumber) {

		// Si utilisateur réccurent alors on retourn 0.95 sinon on retourne 1
		
		if (ticketDAO.isRecurring(vehicleRegNumber)) {
			return 0.95;
		} else
			return 1;

	}

	private double calculateTimeInParking(Ticket ticket) {

		return Duration.between(ticket.getInTime(), ticket.getOutTime()).toMinutes();
	}
}