package com.parkit.parkingsystem.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParkingTypeTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	public void parkingTypeCarError() {
		assertNotEquals(ParkingType.CAR, ParkingType.BIKE);

	};

	@Test
	public void parkingTypeCar() {
		assertEquals(ParkingType.CAR, ParkingType.CAR);

	};

	@Test
	public void parkingTypeBikeError() {
		assertNotEquals(ParkingType.BIKE, ParkingType.CAR);

	};

	@Test
	public void Bike() {
		assertEquals(ParkingType.BIKE, ParkingType.BIKE);

	};
}
