package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

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
import com.parkit.parkingsystem.service.FareCalculatorService;
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
	@Mock
	FareCalculatorService fareCalculatorService = new FareCalculatorService();

	private Ticket ticket;

	private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

	@BeforeEach
	private void setUpPerTest() throws Exception {

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticketTest = new Ticket();
		ticketTest.setInTime(LocalDateTime.now());
		ticketTest.setParkingSpot(parkingSpot);
		ticketTest.setVehicleRegNumber("ABCDEF");
		ticket = ticketTest;

		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

	}

	@Test
	public void processIncomingVehicleTest() throws Exception {
		// Given
		when(inputReaderUtil.readSelection()).thenReturn(1);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(LocalDateTime.now());
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");

		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		ParkingType car = ParkingType.CAR;
		when(parkingSpotDAO.getNextAvailableSlot(car)).thenReturn(2);

		// When
		parkingService.processIncomingVehicle();
		// Then
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	}

	@Test
	public void processIncomingVehicleTestWhenInvokePrintln() {
		// Given
		when(ticketDAO.isRecurring(anyString())).thenReturn(true);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setVehicleRegNumber("TOTO");
		ticket.setOutTime(LocalDateTime.now().plusMinutes(35));
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);
		// Then
		assertEquals("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.",
				outputStreamCaptor.toString()
						.trim());
	}

	@Test
	public void getNextParkingNumberIfAvailableCarTest() {

		// Given
		ParkingType parkingType = ParkingType.CAR;
		ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// When
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(parkingType)).thenReturn(1);

		// Then
		assertEquals(parkingSpot, parkingService.getNextParkingNumberIfAvailable());
	}

	@Test
	public void getNextParkingNumberIfAvailableBikeTest() {

		// Given
		ParkingType parkingType = ParkingType.BIKE;
		ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		when(inputReaderUtil.readSelection()).thenReturn(2);
		when(parkingSpotDAO.getNextAvailableSlot(parkingType)).thenReturn(1);

		// Then
		assertEquals(parkingSpot, parkingService.getNextParkingNumberIfAvailable());
	}

	@Test
	public void processExitingVehicleTest() {
		when(ticketDAO.getTicket(toString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

		parkingService.processExitingVehicle();

	}

	@Test
	public void getVehicleTypeTestBike() {

		// Given

		when(inputReaderUtil.readSelection()).thenReturn(2);

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(LocalDateTime.now());
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		assertEquals(ParkingType.BIKE, parkingService.getVehichleType());
	}

}
