package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

/**
 * 
 * cette classe calcule le prix payé par l'utilisateur pour sortir du parking
 *
 */
public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		
		@SuppressWarnings("deprecation")
		double inHour = ticket.getInTime().getHours();

		
		@SuppressWarnings("deprecation")
		double outHour = ticket.getOutTime().getHours();

		// TODO: Some tests are failing here. Need to check if this logic is correct
		double duration = outHour - inHour;
		
		// Durée mise à zéro si moins de 30 minutes
		if (duration < 30 * 60) {
			duration = 0;
		}

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR: {
			ticket.setPrice((duration / 1000 / 60 / 60) * Fare.CAR_RATE_PER_HOUR);
			if (duration > 1800) {
				ticket.setPrice(duration / 3600 * Fare.CAR_RATE_PER_HOUR);
			}
			break;
		}
		case BIKE: {
			ticket.setPrice((duration / 1000 / 60 / 60) * Fare.BIKE_RATE_PER_HOUR);
			if (duration > 1800) {
				ticket.setPrice(duration / 3600 * Fare.BIKE_RATE_PER_HOUR);
			}
			break;
		}
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
		
		
	}
}