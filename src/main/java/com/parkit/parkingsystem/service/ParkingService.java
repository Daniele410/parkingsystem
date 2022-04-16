package com.parkit.parkingsystem.service;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

/**
 * 
 * Cette classe traite à la fois l'entrée et la sortie du véhicule pour se garer
 *
 */
public class ParkingService {

	private static final Logger logger = LogManager.getLogger("ParkingService");

	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	private InputReaderUtil inputReaderUtil;
	private ParkingSpotDAO parkingSpotDAO;
	private TicketDAO ticketDAO;

	public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
		this.inputReaderUtil = inputReaderUtil;
		this.parkingSpotDAO = parkingSpotDAO;
		this.ticketDAO = ticketDAO;
	}

	public void processIncomingVehicle() {
		try {
			ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
			if (parkingSpot != null && parkingSpot.getId() > 0) {
				String vehicleRegNumber = getVehichleRegNumber();

				// Vérifier si la voiture est déjà présente dans le parking

				boolean isCarInside = ticketDAO.isCarInside(vehicleRegNumber);
				if (!isCarInside) {
					// allot this parking space and mark it's availability as
					// false
					// SI elle n'est pas présente on continue

					parkingSpot.setAvailable(false);
					parkingSpotDAO.updateParking(parkingSpot);
					if (ticketDAO.isRecurring(vehicleRegNumber)) {
						System.out.println("");
						logger.info(
								"Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
					}

					LocalDateTime inTime = LocalDateTime.now();

					Ticket ticket = new Ticket();
					// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
//					 ticket.setId(ticketID);
					ticket.setParkingSpot(parkingSpot);
					ticket.setVehicleRegNumber(vehicleRegNumber);
					ticket.setPrice(0);
					ticket.setInTime(LocalDateTime.now());
					ticket.setOutTime(null);

					ticketDAO.saveTicket(ticket);
					System.out.println("Generated Ticket and saved in DB");
					System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
					System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
				} else {
					logger.info(
							"Car Already Inside please exit your car or select another vehicule registration number");
				}
			}
		} catch (Exception e) {
			logger.error("Unable to process incoming vehicle", e);
		}
	}

	private String getVehichleRegNumber() throws Exception {
		System.out.println("Please type the vehicle registration number and press enter key");
		return inputReaderUtil.readVehicleRegistrationNumber();
	}

	public ParkingSpot getNextParkingNumberIfAvailable() {
		int parkingNumber = 0;
		ParkingSpot parkingSpot = null;
		try {
			ParkingType parkingType = getVehichleType();
			parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
			if (parkingNumber > 0) {
				parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
			} else {
				throw new Exception("Error fetching parking number from DB. Parking slots might be full");
			}
		} catch (IllegalArgumentException ie) {
			logger.error("Error parsing user input for type of vehicle", ie);
		} catch (Exception e) {
			logger.error("Error fetching next available parking slot", e);
		}
		return parkingSpot;
	}

	public ParkingType getVehichleType() {
		System.out.println("Please select vehicle type from menu");
		System.out.println("1 CAR");
		System.out.println("2 BIKE");
		int input = inputReaderUtil.readSelection();
		switch (input) {
		case 1: {
			return ParkingType.CAR;
		}
		case 2: {
			return ParkingType.BIKE;
		}
		default: {
			System.out.println("Incorrect input provided");
			throw new IllegalArgumentException("Entered input is invalid");
		}
		}
	}

	public void processExitingVehicle() {
		try {
			
			String vehicleRegNumber = getVehichleRegNumber();
			Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
			LocalDateTime outTime = LocalDateTime.now();
			ticket.setOutTime(outTime);
			fareCalculatorService.setTicketDAO(ticketDAO);
			fareCalculatorService.calculateFare(ticket);
			
			
			if (ticketDAO.isCarInside(vehicleRegNumber) && ticketDAO.updateTicket(ticket)) {
				ParkingSpot parkingSpot = ticket.getParkingSpot();
				parkingSpot.setAvailable(true);
				parkingSpotDAO.updateParking(parkingSpot);
				System.out.println("Please pay the parking fare:" + ticket.getPrice());
				System.out.println(
						"Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is: " + outTime);

			} else {
				logger.error("Unable to update ticket information. Error occurred");
			}
		} catch (Exception e) {
			logger.error("Unable to process exiting vehicle", e);
		}
	}
}
