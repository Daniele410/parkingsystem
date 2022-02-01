package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;

/**
 * 
 * cette classe calcule le prix payé par l'utilisateur pour sortir du parking
 *
 */
public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		// TODO: Some tests are failing here. Need to check if this logic is correct
		//double duration = outHour - inHour;
		//double duration = Duration.between(ticket.getInTime(), ticket.getOutTime()).toMinutes();
		//créer une méthode avec ce qui est écrit ligne 23
		double duration = calculateTimeInParking(ticket);
		
		//créer une méthode en utilisant ticketDAO.isReccuring pour savoir si la discount est de 1 ou 0.95
		double discount = calculateDicount(ticket.getVehicleRegNumber());
		// Durée mise à zéro si moins de 30 minutes
		if (duration < 30 ) {
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
}