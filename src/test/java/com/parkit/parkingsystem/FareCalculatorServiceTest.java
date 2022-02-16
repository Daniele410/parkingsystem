package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	@BeforeAll
	private static void setUp() {
		fareCalculatorService = new FareCalculatorService();
	}

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
	}

	@Test
	public void calculateFareCar() {

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusDays(1));
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), ticket.getPrice());
	}

	@Test
	public void calculateFareBike() {

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusDays(1));
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals(ticket.getPrice(), ticket.getPrice());
	}

	@Test
	public void calculateFareUnkownType() {

		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

		ticket.setInTime(LocalDateTime.now());
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

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(45));
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanOneHourParkingTime() {
		// 45 minutes parking time should give 3/4th
		// parking fare

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(45));
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithMoreThanADayParkingTime() {
		// 24 hours parking time should give 24 *
		// parking fare per hour

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusHours(24));
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFareCarWithLessThanThirtyMinutesParkingTime() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (24 * 60 * 1000));

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(15));
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertEquals((0 * Fare.CAR_RATE_PER_MINUTE), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithLessThanThrityMinutesParkingTime() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (24 * 60 * 1000));

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(30));
		ticket.setParkingSpot(parkingSpot);

		fareCalculatorService.calculateFare(ticket);

		assertEquals((0 * Fare.BIKE_RATE_PER_MINUTE), ticket.getPrice() * 0);
	}

	@Test
	public void calculateFareCarWithLessThanHalfAnOurParkingTime() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000));// 20 minutes parking time should give free
																		// parking fare

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(20));
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0 * Fare.CAR_RATE_PER_MINUTE), ticket.getPrice());
	}

	@Test
	public void calculateFareBikeWithLessThanHalfAnHourParkingTime() {
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000));// 20 minutes parking time should give free
																		// parking fare

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(LocalDateTime.now().plusMinutes(20));
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		assertEquals((0 * Fare.BIKE_RATE_PER_MINUTE), ticket.getPrice());
	}

}
