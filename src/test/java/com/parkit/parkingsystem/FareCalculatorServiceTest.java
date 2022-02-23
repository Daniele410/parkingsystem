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
		// WHen
		fareCalculatorService.calculateFare(ticket);
		// Then
		assertEquals(ticket.getPrice(), ticket.getPrice());
	}

	@Test
	public void calculateFareBike() {
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusDays(1));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), ticket.getPrice());
	}

	@Test
	public void calculateFareUnkownType() {
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setVehicleRegNumber("TOTO");
		ticket.setOutTime(LocalDateTime.now().plusDays(1));
		ticket.setParkingSpot(parkingSpot);
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

//	@Test
//	public void calculateFareBikeWithFutureInTime() {
//		Date inTime = new Date();
//		inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
//
//		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
//
//		ticket.setInTime(LocalDateTime.now());
//		ticket.setOutTime(LocalDateTime.now().plusWeeks(2));
//		ticket.setParkingSpot(parkingSpot);
//		assertEquals((1 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
//	}

	@Test
	public void calculateFareBikeWithLessThanOneHourParkingTime() {
		// 45 minutes parking time should give 3/4th
		// parking fare
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(45));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanOneHourParkingTime() {
		// 45 minutes parking time should give 3/4th
		// parking fare
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(45));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithMoreThanADayParkingTime() {
		// 24 hours parking time should give 24 *
		// parking fare per hour
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusHours(24));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanThirtyMinutesParkingTime() {
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(15));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertEquals((0 * Fare.CAR_RATE_PER_MINUTE), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithLessThanThrityMinutesParkingTime() {
		when(ticketDAO.isRecurring(anyString())).thenReturn(false);

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(30));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertEquals((0 * Fare.BIKE_RATE_PER_MINUTE), ticket.getPrice() * 0);
	}

	@Test
	public void calculateFareCarWithLessThanHalfAnOurParkingTime() {
		when(ticketDAO.isRecurring(anyString())).thenReturn(false); // parking fare

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(20));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0 * Fare.CAR_RATE_PER_MINUTE), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithLessThanHalfAnHourParkingTime() {
		when(ticketDAO.isRecurring(anyString())).thenReturn(false); // parking fare

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(20));
		ticket.setVehicleRegNumber("TOTO");
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0 * Fare.BIKE_RATE_PER_MINUTE), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithMoreThanHalfAnHourParkingTimeAndDiscount() {
		when(ticketDAO.isRecurring(anyString())).thenReturn(true);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setVehicleRegNumber("TOTO");
		ticket.setOutTime(LocalDateTime.now().plusMinutes(35));
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((35 * Fare.CAR_RATE_PER_MINUTE * 0.95), ticket.getPrice());
	}

}
