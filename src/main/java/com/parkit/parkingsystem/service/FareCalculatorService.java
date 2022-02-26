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

	private TicketDAO ticketDAO = new TicketDAO();

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		// TODO: Some tests are failing here. Need to check if this logic is correct
		// double duration = outHour - inHour;
		// double duration = Duration.between(ticket.getInTime(),
		// ticket.getOutTime()).toMinutes();
		// créer une méthode avec ce qui est écrit ligne 23
		double duration = calculateTimeInParking(ticket);

		// créer une méthode en utilisant ticketDAO.isReccuring pour savoir si la
		// discount est de 1 ou 0.95

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

	private double calculateDicount(String vehicleRegNumber) {
		// Je dois payer 100 € mais j'ai une réduction de 5%
		// Je dois multiplier 100 par quel double pour arriver 95 ? C'est 0.95

		// SI utilisateur réccurent alors on retourn 0.95 sinon on retourne 1

		if (ticketDAO.isRecurring(vehicleRegNumber)) {
			return 0.95;
		} else
			return 1;

	}

	private double calculateTimeInParking(Ticket ticket) {

		return Duration.between(ticket.getInTime(), ticket.getOutTime()).toMinutes();

	}
}