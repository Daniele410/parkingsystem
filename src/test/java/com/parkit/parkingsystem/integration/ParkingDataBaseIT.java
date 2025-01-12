package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static final String vehiculeRegNumber = "ABCDEF";

	private static DataBaseTestConfig dataBaseTestConfig;

	private static DataBasePrepareService dataBasePrepareService;

	private static ParkingSpotDAO parkingSpotDAO;

	private static TicketDAO ticketDAO;

	/**
	 * Class to be tested
	 */

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	private static LogCaptor logcaptor;

	@BeforeAll
	private static void setUp() throws Exception {
		System.out.println("BEFORE ALL");
		dataBaseTestConfig = new DataBaseTestConfig();
		dataBasePrepareService = new DataBasePrepareService();
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;

		dataBasePrepareService.clearDataBaseEntries();
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		logcaptor = LogCaptor.forName("ParkingService");
		logcaptor.setLogLevelToInfo();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		System.out.println("BEFORE EACH");
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterEach
	private void tearDown() {
		System.out.println("AFTER EACH");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@Test
	public void testParkingCar() throws Exception {

		// Given + When
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
		parkingService.processIncomingVehicle();

		// Then
		Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
		assertNotNull(ticket);
		assertEquals(vehiculeRegNumber, ticket.getVehicleRegNumber());
		assertFalse(ticket.getParkingSpot().isAvailable());

	}

	@Test
	public void testParkingBike() throws Exception {

		// Given + When
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
		parkingService.processIncomingVehicle();

		// Then
		Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
		assertNotNull(ticket);
		assertEquals(vehiculeRegNumber, ticket.getVehicleRegNumber());
		assertFalse(ticket.getParkingSpot().isAvailable());

	}

	@Test
	public void testParkingLotExit() throws Exception {

		// Given
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
		parkingService.processIncomingVehicle();

		// When
		parkingService.processExitingVehicle();
		Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);

		// Then
		assertNotNull(ticket);

	}

	@Test
	public void testParkingLotExitCar() throws Exception {

		// Given
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
		parkingService.processIncomingVehicle();
		Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
		ticket.setInTime(LocalDateTime.now().minusMinutes(35));
		ticketDAO.saveTicket(ticket);
		System.out.println(ticket.getParkingSpot().getParkingType());

		// When
		parkingService.processExitingVehicle();

		// Then
		assertNotNull(ticketDAO.getTicket(vehiculeRegNumber).getPrice());
		assertNotNull(ticketDAO.getTicket(vehiculeRegNumber).getOutTime());

	}

	@Test
	public void testParkingLotExitBike() throws Exception {

		// Given
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
		parkingService.processIncomingVehicle();
		Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
		ticket.setInTime(LocalDateTime.now().minusMinutes(35));
		ticketDAO.saveTicket(ticket);

		// When
		parkingService.processExitingVehicle();

		// Then
		assertNotNull(ticketDAO.getTicket(vehiculeRegNumber).getPrice());
		assertNotNull(ticketDAO.getTicket(vehiculeRegNumber).getOutTime());

	}

	@Test
	public void testParkingLotExitCarWhitDiscountRecurringUser() throws Exception {

		// Given

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
		parkingService.processIncomingVehicle();
		Thread.sleep(1000);
		parkingService.processExitingVehicle();
		parkingService.processIncomingVehicle();
		Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
		ticket.setInTime(LocalDateTime.now().minusMinutes(35));
		ticketDAO.updateTicket(ticket);
		// Le test va trop vite pour le calcul de temps
		Thread.sleep(1000);

		// When
		parkingService.processExitingVehicle();

		// Then
		assertEquals(35 * Fare.CAR_RATE_PER_MINUTE * 0.95, ticketDAO.getTicket(vehiculeRegNumber).getPrice());

	}

	@Test
	public void testParkingLotExitBikeWhitDiscountRecurringUser() throws Exception {

		// Given

		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
		parkingService.processIncomingVehicle();
		Thread.sleep(1000);
		parkingService.processExitingVehicle();
		parkingService.processIncomingVehicle();
		Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
		ticket.setInTime(LocalDateTime.now().minusMinutes(35));
		ticketDAO.updateTicket(ticket);
		// Le test va trop vite pour le calcul de temps
		Thread.sleep(1000);

		// When
		parkingService.processExitingVehicle();

		// Then
		assertEquals(35 * Fare.BIKE_RATE_PER_MINUTE * 0.95, ticketDAO.getTicket(vehiculeRegNumber).getPrice());

	}

	@Test
	public void testParkingLotSameUserInASameParkingLogCaptor() throws Exception {

		// Given

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
		parkingService.processIncomingVehicle();

		// When
		parkingService.processIncomingVehicle();

		// Then
		assertThat(logcaptor.getInfoLogs())
				.contains("Car Already Inside please exit your car or select another vehicule registration number");

	}

	@Test
	public void calculateFareCarWithLessThanOneHourParkingTime() throws Exception {

		// Given
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
		parkingService.processIncomingVehicle();
		Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
		ticket.setInTime(LocalDateTime.now().minusMinutes(40));
		ticketDAO.updateTicket(ticket);
		// Le test va trop vite pour le calcul de temps
		Thread.sleep(1000);
		
		// When
		parkingService.processExitingVehicle();
		
		// Then
		assertEquals(40 * Fare.CAR_RATE_PER_MINUTE, ticketDAO.getTicket(vehiculeRegNumber).getPrice());

	}

	@Test
	public void calculateFareBikeWithLessThanOneHourParkingTime() throws Exception {

		// Given
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
		parkingService.processIncomingVehicle();
		Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
		ticket.setInTime(LocalDateTime.now().minusMinutes(35));
		ticketDAO.updateTicket(ticket);
		// Le test va trop vite pour le calcul de temps
		Thread.sleep(1000);

		// When

		parkingService.processExitingVehicle();
		// Then
		assertEquals(35 * Fare.BIKE_RATE_PER_MINUTE, ticketDAO.getTicket(vehiculeRegNumber).getPrice());

	}

	@Test
	public void testFreeParkingCarLotExitWhitDiscount() throws Exception {

		// Given
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
		parkingService.processIncomingVehicle();
		Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(20));

		// When
		parkingService.processExitingVehicle();

		// Then
		assertEquals(0 * Fare.CAR_RATE_PER_MINUTE, ticket.getPrice());

	}

	@Test
	public void testFreeParkingBikeLotExitWhitDiscount() throws Exception {

		// Given
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehiculeRegNumber);
		parkingService.processIncomingVehicle();
		Ticket ticket = ticketDAO.getTicket(vehiculeRegNumber);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(15));

		// When
		parkingService.processExitingVehicle();

		// Then
		assertEquals(0 * Fare.BIKE_RATE_PER_MINUTE, ticket.getPrice());

	}
}
