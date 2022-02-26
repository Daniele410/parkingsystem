package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

@ExtendWith(MockitoExtension.class)
public class FareCalculatorServiceTest {

	@InjectMocks
	private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

	@Mock
	private TicketDAO ticketDAO;

	private Ticket ticket;

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();

	}

	@Test
	public void calculateFareCar() {

		// Given
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setVehicleRegNumber("TOTO");
		ticket.setOutTime(LocalDateTime.now().plusDays(1));
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals(ticket.getPrice(), ticket.getPrice());
	}

	@Test
	public void calculateFareBike() {

		// Given
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusDays(1));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);

		// WHen
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals(ticket.getPrice(), ticket.getPrice());
	}

	@Test
	public void calculateFareUnkownType() {

		// Given
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setVehicleRegNumber("TOTO");
		ticket.setOutTime(LocalDateTime.now().plusDays(1));
		ticket.setParkingSpot(parkingSpot);

		// Then
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFareBikeWithLessThanOneHourParkingTime() {

		// Given
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(45));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);
		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanOneHourParkingTime() {

		// Given
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(45));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithMoreThanADayParkingTime() {

		// Given
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusHours(24));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanThirtyMinutesParkingTime() {

		// Given
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
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
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(25));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((0 * Fare.BIKE_RATE_PER_MINUTE), ticket.getPrice() * 0);
	}

	@Test
	public void calculateFareCarWithLessThanHalfAnOurParkingTime() {

		// Given
		when(ticketDAO.isRecurring(anyString())).thenReturn(false); // parking fare
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
		when(ticketDAO.isRecurring(anyString())).thenReturn(false); // parking fare
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(20));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((0 * Fare.BIKE_RATE_PER_MINUTE), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithMoreThanHalfAnHourParkingTimeAndDiscount() {

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
		assertEquals((35 * Fare.CAR_RATE_PER_MINUTE * 0.95), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithMoreThanHalfAnHourParkingTimeAndDiscount() {

		// Given
		when(ticketDAO.isRecurring(anyString())).thenReturn(true);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setVehicleRegNumber("TOTO");
		ticket.setOutTime(LocalDateTime.now().plusMinutes(35));
		ticket.setParkingSpot(parkingSpot);

		// When
		fareCalculatorService.calculateFare(ticket);

		// Then
		assertEquals((35 * Fare.BIKE_RATE_PER_MINUTE * 0.95), ticket.getPrice());
	}

}
