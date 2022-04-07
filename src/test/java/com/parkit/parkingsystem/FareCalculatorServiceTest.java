package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;

	@Mock
	private TicketDAO ticketDAO;
	@Mock
	private Ticket ticket;

	@Mock
	private DataBaseConfig dataBaseConfig;
	
	@Mock
	private static InputReaderUtil inputReaderUtil;

	@Mock
	private Connection con;

	@Mock
	private PreparedStatement ps;

	@Mock
	private ResultSet rs;

	String vehicleRegNumber = "TOTO";

	Logger logger = LogManager.getLogger(TicketDAOTest.class);

	private static LogCaptor logcaptor;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();

	}

	@BeforeEach
	private void setUpPerTest() {
		
		ticket = new Ticket();
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseConfig;

		ticket.setId(3);
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
		ticket.setPrice(1.5);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(null);

		logcaptor = LogCaptor.forName("FareCalculatorService");
		logcaptor.setLogLevelToInfo();
		
	}

	@Test
	public void calculateFareCar() {
		// GIVEN
		
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setOutTime(LocalDateTime.now().plusDays(1));
		ticket.setParkingSpot(parkingSpot);

		// WHEN
		fareCalculatorService.calculateFare(ticket);

		// THEN
		assertEquals(ticket.getPrice(), 60 *  Fare.CAR_RATE_PER_MINUTE * 24);
	}

	@Test
	public void calculateFareBike() {

		// Given

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusDays(1));
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setParkingSpot(parkingSpot);

		// WHen
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals(60 * Fare.BIKE_RATE_PER_MINUTE * 24 , ticket.getPrice());
	}
	
	
	
	@Test
	public void calculateFareShouldExceptionTestGetOutTimeIsNull() throws IllegalArgumentException {

		//Given 
		
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().minusMinutes(30));
		ticket.setVehicleRegNumber(vehicleRegNumber);

		ticket.setParkingSpot(parkingSpot);



		// Then

		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareUnkownType() {

		// Given
		
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(LocalDateTime.now());
		
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setParkingSpot(parkingSpot);

		// When
		ticket.setParkingSpot(parkingSpot);

		// Then
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareBikeWithLessThanOneHourParkingTime() {

		// Given

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(45));
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setParkingSpot(parkingSpot);
		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanOneHourParkingTime() {

		// Given

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(45));
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithMoreThanADayParkingTime() {

		// Given

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusHours(24));
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanThirtyMinutesParkingTime() {

		// Given

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(15));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((0 * Fare.CAR_RATE_PER_MINUTE), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithLessThanThrityMinutesParkingTime() {

		// Given

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(25));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((0 * Fare.BIKE_RATE_PER_MINUTE), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanHalfAnOurParkingTime() {

		// Given

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(20));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((0 * Fare.CAR_RATE_PER_MINUTE), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithLessThanHalfAnHourParkingTime() {

		// Given

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(20));
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((0 * Fare.BIKE_RATE_PER_MINUTE), ticket.getPrice());
	}

	@Disabled
	@Test
	public void calculateFareCarWithMoreThanHalfAnHourParkingTimeAndDiscount() throws Exception {

		// Given

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setVehicleRegNumber(vehicleRegNumber);
		
		when(con.prepareStatement(DBConstants.CYCLIC_USER)).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(true);
		ticket.setInTime(LocalDateTime.now());
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setParkingSpot(parkingSpot);
		ticket.setOutTime(LocalDateTime.now().plusMinutes(35));
		
//		ticketDAO.isRecurring(vehicleRegNumber);
//		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
//		when(con.prepareStatement(DBConstants.CYCLIC_USER)).thenReturn(ps);
//		when(ps.executeQuery()).thenReturn(rs);
//		when(ticketDAO.isRecurring(vehicleRegNumber)).thenReturn(true);
//		when(ticketDAO.getTicket(vehicleRegNumber)).thenReturn(ticket);
		// When
//		fareCalculatorService.calculateDicount(vehicleRegNumber);
		fareCalculatorService.calculateFare(ticket);

		// Then
//		assertEquals((35 * Fare.CAR_RATE_PER_MINUTE * 0.95), ticket.getPrice());
		assertEquals(35 * Fare.CAR_RATE_PER_MINUTE * 0.95, ticketDAO.getTicket(vehicleRegNumber).getPrice());
	}

	
	@Test
	public void calculateFareBikeWithMoreThanHalfAnHourParkingTime() {

		// Given
		
		
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setOutTime(LocalDateTime.now().plusMinutes(35));

		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((35 * Fare.BIKE_RATE_PER_MINUTE ), ticket.getPrice());
	}
	
	@Test
	public void calculateFareBikeWithMoreThanHalfAnHourParkingTimeAndDiscount() {

		// Given
		
		
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(LocalDateTime.now());
		ticketDAO.isRecurring(vehicleRegNumber);
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setOutTime(LocalDateTime.now().plusMinutes(35));
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateDicount(vehicleRegNumber);
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((35 * Fare.BIKE_RATE_PER_MINUTE ), ticket.getPrice());
	}

	@Test
	public void calculateFareCarGotOutTimeWhenIsNull() throws Exception {

		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
	
		ticket.setInTime(LocalDateTime.now());
		ticket.setVehicleRegNumber("TOTO");
		ticket.setOutTime(null);
		ticket.setParkingSpot(parkingSpot);

		// When

		boolean result = ticketDAO.isRecurring(anyString());

		// Then
		assertEquals(false, result);
	}

}
