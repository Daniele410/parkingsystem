package com.parkit.parkingsystem.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FareTest {
	Fare fare = new Fare();

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	public void verifyPriceBikeRatePerHour() {
		assertEquals(Fare.BIKE_RATE_PER_HOUR, 1.0);

	}

	@Test
	public void verifyPriceBikeRatePerMinute() {
		assertEquals(Fare.BIKE_RATE_PER_MINUTE, 1.0 / 60);

	}

	@Test
	public void verifyPriceCarRatePerHour() {
		assertEquals(Fare.CAR_RATE_PER_HOUR, 1.5);

	}

	@Test
	public void verifyPriceCarRatePerMinute() {
		assertEquals(Fare.CAR_RATE_PER_MINUTE, 1.5 / 60);

	}

}
