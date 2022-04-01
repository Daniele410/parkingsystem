package com.parkit.parkingsystem.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static final String VehiculeRegNumber = "ABCDEF";

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

	private static DataBasePrepareService dataBasePrepareService;

	private static ParkingSpotDAO parkingSpotDAO;

	private static TicketDAO ticketDAO;

	private static FareCalculatorService fareCalculatorService;

	// Class to be tested
	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		System.out.println("BEFORE ALL");
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		System.out.println("BEFORE EACH");
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(VehiculeRegNumber);
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {
		System.out.println("AFTER ALL");
	}

	@DisplayName("Parking system save ticket to DB and Update parkingspot with avaibility")
	@Test
	public void testParkingACar() {
		// Given + Act
		parkingService.processIncomingVehicle();
		// Then
		Ticket ticket = ticketDAO.getTicket(VehiculeRegNumber);
		assertNotNull(ticket);
		assertEquals(VehiculeRegNumber, ticket.getVehicleRegNumber());
		assertFalse(ticket.getParkingSpot().isAvailable());

	}

	
	@Test
	public void testParkingLotExit() {

		// Given
		parkingService.processIncomingVehicle();


		// When
		parkingService.processExitingVehicle();
		Ticket ticket = ticketDAO.getTicket(VehiculeRegNumber);
		
		//Then
		assertNotNull(ticket);
		

	}

	@Test
	public void testParkingLotExitBike() {

		// Given
		parkingService.processIncomingVehicle();
		Ticket ticket = ticketDAO.getTicket(VehiculeRegNumber);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(35));
		

		// When
		parkingService.processExitingVehicle();
		assertNotNull(ticket.getPrice());
		assertNotNull(ticket.getOutTime());

	}

	@Test
	public void testParkingLotExitWhitDiscount() {

		// Given

		parkingService.processIncomingVehicle();
		new ParkingSpot(1, ParkingType.BIKE, false);
		Ticket ticket = ticketDAO.getTicket(VehiculeRegNumber);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(20));
		parkingService.processExitingVehicle();

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals(0 * Fare.BIKE_RATE_PER_MINUTE, ticket.getPrice());

		
	}

	@Test
	public void testFreeParkingLotExitWhitDiscount() {

		// Given
		parkingService.processIncomingVehicle();
		new ParkingSpot(1, ParkingType.BIKE, false);
		Ticket ticket = ticketDAO.getTicket(VehiculeRegNumber);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(15));

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals(0 * Fare.BIKE_RATE_PER_MINUTE, ticket.getPrice());

	}
}
