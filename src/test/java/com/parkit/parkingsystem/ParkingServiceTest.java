package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	private void setUpPerTest() {
		try {
			when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(LocalDateTime.now().plusHours(1));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
			when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

			when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Test
	public void processIncomingVehicleTest() {
		parkingService.processIncomingVehicle();
		verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
	}

	@Test
	public void getNextParkingNumberIfAvailableCarTest() {
		ParkingType parkingType = ParkingType.CAR;
		ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSlot(parkingType)).thenReturn(1);

		assertEquals(parkingSpot, parkingService.getNextParkingNumberIfAvailable());
	}

	@Test
	public void getNextParkingNumberIfAvailableBikeTest() {
		ParkingType parkingType = ParkingType.BIKE;
		ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSlot(parkingType)).thenReturn(2);

		assertEquals(parkingSpot, parkingService.getNextParkingNumberIfAvailable());
	}

	@Test
	public void testParkingLotExit() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setParkingSpot(parkingSpot);
		ticket.getVehicleRegNumber();
		ticket.setPrice(0);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusHours(1));
		ticketDAO.saveTicket(ticket);
		Mockito.when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		Mockito.when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		Mockito.when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

		ParkingService parkingServiceOut = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingServiceOut.processExitingVehicle();

		Mockito.verify(parkingSpotDAO).updateParking(Mockito.any(ParkingSpot.class));
		Assertions.assertEquals(1.5, ticket.getPrice());
		// TODO: check that the fare generated and out time are populated correctly in
		// the database
	}

	@Test
	public void processExitingVehicleTest() throws Throwable {
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(45));
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEFG");
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEFG");
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

		parkingService.processExitingVehicle();
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	}

}
