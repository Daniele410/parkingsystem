package com.parkit.parkingsystem.integration;

import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.sql.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static final String VehiculeRegNumber = "ABCDEFG";
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static ParkingSpotDAO parkingSpotDAO;

	@Mock
	private static TicketDAO ticketDAO;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	
	@Mock
	private static Ticket ticket;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testParkingACar() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability
		parkingService.processIncomingVehicle();

		Mockito.verify(ticketDAO).saveTicket(Mockito.any(Ticket.class));
		Mockito.verify(parkingSpotDAO).updateParking(Mockito.any(ParkingSpot.class));

	}

	@Test
	public void testParkingLotExit() {
		testParkingACar();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		// TODO: check that the fare generated and out time are populated correctly in
		// the database
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber(VehiculeRegNumber);
		ticket.setPrice(0);
		ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
		ticket.setOutTime(null);
		ticketDAO.saveTicket(ticket);
		Mockito.when(ticketDAO.getTicket(toString())).thenReturn(ticket);
		Mockito.when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		Mockito.when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

		ParkingService parkingServiceOut = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingServiceOut.processExitingVehicle();

		Mockito.verify(parkingSpotDAO).updateParking(Mockito.any(ParkingSpot.class));
		Assertions.assertEquals(1.5, ticket.getPrice());
	}

	private ParkingSpot any(Serializable class1) {
		return null;
	}

	private Ticket any(Class<Ticket> class1) {
		return null;
	}


	}


