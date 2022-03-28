package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
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

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;
	
	private static LogCaptor logcaptor;

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
		logcaptor = LogCaptor.forName("TicketDAO");
		logcaptor.setLogLevelToInfo();

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
		verify(parkingSpotDAO, Mockito.times(2)).updateParking(any(ParkingSpot.class));
	}

	@Test
	public void processIncomingVehicleTestWhenInvokePrintln() {

		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setVehicleRegNumber("ABCDEF");
		if (parkingSpot != null && parkingSpot.getId() > 0) {
			String vehicleRegNumber = anyString();
			// Vérifier si la voiture est déjà présente dans le parking
			// SI elle n'est pas présente on continue
			boolean isCarInside = ticketDAO.isCarInside(vehicleRegNumber);
			if (!isCarInside) {
				// Sinon on sort de la méthode
				if (ticketDAO.isRecurring(vehicleRegNumber)) {
					System.out.println(
							"Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
					// Then
					assertEquals(
							"Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.",
							outputStreamCaptor);
				}
			}
		}
	}

	@Test
	public void getNextParkingNumberIfAvailableCarTest() {

		// Given
		ParkingType parkingType = ParkingType.CAR;
		ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
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
	public void processExitingVehicleTicketNullTest() {
		when(ticketDAO.getTicket(toString())).thenReturn(null);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
		
		assertThrows(Exception.class, () -> parkingService.processExitingVehicle());
		
		assertThat(logcaptor.getErrorLogs()).contains("Unable to process exiting vehicle");
		
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

		// Then
		assertEquals(ParkingType.BIKE, parkingService.getVehichleType());
	}

	@Test
	public void getNextParkingButParkingIsFull() throws Exception {
		// Arrange
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);

		// Act
		ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

		// Assert
		verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
		assertNull(parkingSpot);
	}

	@Test
	public void getVehicleTypeButIllegalArgumentExceptionIsThrown() {

		// Arrange
		when(inputReaderUtil.readSelection()).thenReturn(3);

		// Assert
		assertThrows(IllegalArgumentException.class, () -> parkingService.getVehichleType());
	}

	@Test
	public void processExitingVehicleWithGoodTicket() throws Exception {

		// Arrange
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
		when(ticketDAO.updateTicket(ticket)).thenReturn(true);

		// Act
		parkingService.processExitingVehicle();

		// Assert
		verify(parkingSpotDAO).updateParking(parkingSpot);
	}
	
	@Test
	public void processIncomingVehicleTestLogCaptor() throws Exception {
		// Given
		when(inputReaderUtil.readSelection()).thenReturn(1);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(LocalDateTime.now());
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		when(ticketDAO.getTicket("ABCDEF")).thenReturn(null);
	

		// When
		parkingService.processIncomingVehicle();
		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Error fetching next available parking slot");
	}
	
	@Test
	public void processIncomingVehicleTestLogCaptorFalse() throws Exception {
		// Given
		when(inputReaderUtil.readSelection()).thenReturn(1);
		Ticket ticket = new Ticket();
		ticket.setInTime(LocalDateTime.now());
		ticket.setParkingSpot(null);
		ticket.setId(1);
		ticket.setVehicleRegNumber(null);
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(false);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		when(ticketDAO.getTicket("ABCDEF")).thenReturn(null);
	

		// When
		parkingService.processIncomingVehicle();
		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Error fetching next available parking slot");
	}
	
	@Test
	public void getVehichleRegNumberLogCaptorReturn() throws Exception {
		// Given
		when(inputReaderUtil.readSelection()).thenReturn(1);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(LocalDateTime.now());
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);
	

		// When
		parkingService.processIncomingVehicle();
		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Error fetching next available parking slot");
	}
	
	@Test
	public void processExitingVehicleTestLogCaptor() throws Exception {

		// Arrange
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
		when(ticketDAO.updateTicket(ticket)).thenReturn(false);
		
		// Act
		parkingService.processExitingVehicle();

		// Assert
		if (ticketDAO.updateTicket(ticket)) {
			System.out.println(
					"Unable to update ticket information. Error occurred");
			// Then
			assertEquals(
					"Unable to update ticket information. Error occurred",
					outputStreamCaptor);
		}
	}
	
	public void processExitingVehicleTicketNullTestLogCaptor() throws Exception {

		// Arrange
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
		when(ticketDAO.updateTicket(ticket)).thenReturn(false);
		
		// Act
		parkingService.processExitingVehicle();

		// Assert
		if (ticket == null) {
			System.out.println("Error any vehicle found with number : " );
			// Then
			assertEquals(
					"Unable to update ticket information. Error occurred",
					outputStreamCaptor);
		}
	}

}
