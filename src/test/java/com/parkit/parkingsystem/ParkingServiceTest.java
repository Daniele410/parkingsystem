package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
	@Mock
	private Ticket ticket;

	@BeforeEach
	private void setUpPerTest() throws Exception {

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticketTest = new Ticket();
		ticketTest.setInTime(LocalDateTime.now());
		ticketTest.setParkingSpot(parkingSpot);
		ticketTest.setVehicleRegNumber("ABCDEF");
		ticket = ticketTest;

		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		logcaptor = LogCaptor.forName("ParkingService");
		logcaptor.setLogLevelToInfo();

	}

	@Test
	public void processIncomingVehicleTestLogCaptorIllegalArgumentException() throws Exception {
		// Given

		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenThrow(IllegalArgumentException.class);

		// When
		parkingService.processIncomingVehicle();
		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Error parsing user input for type of vehicle");
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
	public void getVehicleTypeTestCar() {

		// Given
		when(inputReaderUtil.readSelection()).thenReturn(1);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(LocalDateTime.now());
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		// Then
		assertEquals(ParkingType.CAR, parkingService.getVehichleType());
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
		// Given
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(0);

		// When
		ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();

		// Then
		verify(parkingSpotDAO, times(1)).getNextAvailableSlot(ParkingType.CAR);
		assertNull(parkingSpot);
	}

	@Test
	public void getVehicleTypeButIllegalArgumentExceptionIsThrown() {

		// Given
		when(inputReaderUtil.readSelection()).thenReturn(3);

		// Assert
		assertThrows(IllegalArgumentException.class, () -> parkingService.getVehichleType());
	}
	
	

	@Test
	public void processExitingVehicleWithGoodTicket() throws Exception {

		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
		when(ticketDAO.isCarInside("ABCDEF")).thenReturn(true);
		when(ticketDAO.updateTicket(ticket)).thenReturn(true);

		// When
		parkingService.processExitingVehicle();

		// Then
		verify(parkingSpotDAO).updateParking(parkingSpot);
	}

	@Test
	public void processExitingVehicleTestLogCaptor() throws Exception {

		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
		when(ticketDAO.isCarInside("ABCDEF")).thenReturn(false);

		// When
		parkingService.processExitingVehicle();

		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Unable to update ticket information. Error occurred");
	}

	@Test
	public void processExitingVehicleTicketNullTestLogCaptor() throws Exception {

		// Given
		String vehicleRegNumber = "ABCDEF";
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);

		// When
		parkingService.processExitingVehicle();

		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Unable to process exiting vehicle");

	}

	@Test
	public void processExitingVehicleIsNotInsideTestLogCaptor() throws Exception {

		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(ticketDAO.getTicket("ABCDEF")).thenReturn(ticket);
		when(ticketDAO.isCarInside("ABCDEF")).thenReturn(true);
		when(ticketDAO.updateTicket(ticket)).thenReturn(false);

		// When
		parkingService.processExitingVehicle();

		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Unable to update ticket information. Error occurred");

	}

	@Test
	public void processIncomingVehicleIsCarInsideIsRecurringTestLogCaptor() throws Exception {
		// Given
		String vehicleRegNumber = "qwerty";
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(LocalDateTime.now());
		ticket.setParkingSpot(parkingSpot);
		ticket.setId(1);
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
		when(ticketDAO.isCarInside(anyString())).thenReturn(false);
		when(ticketDAO.isRecurring(anyString())).thenReturn(true);

		// When
		parkingService.processIncomingVehicle();

		// Then
		assertThat(logcaptor.getInfoLogs())
				.contains("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
	}
	
	@Test
	public void processIncomingVehicleIsCarInsideIsRecurringFalseTestLogCaptor() throws Exception {
		// Given
		String vehicleRegNumber = "qwerty";
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(LocalDateTime.now());
		ticket.setParkingSpot(parkingSpot);
		ticket.setId(1);
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
		when(ticketDAO.isCarInside(anyString())).thenReturn(false);
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);

		// When
		parkingService.processIncomingVehicle();

		// Then
		assertFalse(ticketDAO.isRecurring(vehicleRegNumber));
	}

	@Test
	public void processIncomingVehicleIsCarInsideTrueTestLogCaptor() throws Exception {
		// Given
		String vehicleRegNumber = "qwerty";
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Ticket ticket = new Ticket();
		ticket.setInTime(LocalDateTime.now());
		ticket.setParkingSpot(parkingSpot);
		ticket.setId(1);
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
		when(ticketDAO.isCarInside(anyString())).thenReturn(true);

		// When
		parkingService.processIncomingVehicle();

		// Then
		assertThat(logcaptor.getInfoLogs())
				.contains("Car Already Inside please exit your car or select another vehicule registration number");
	}
}
