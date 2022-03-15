package com.parkit.parkingsystem.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.Connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {

	private static final Logger LOGGER = LogManager.getLogger(ParkingSpotDAOTest.class);

	private static DataBasePrepareService dataBasePrepareService;

	private static DataBaseConfig databaseTestConfig = new DataBaseTestConfig();

	private static ParkingSpotDAO parkingSpotDAO;

	static Connection con;

	@BeforeAll
	static void setUpTestEnvironment() throws Exception {
		// Given
		con = databaseTestConfig.getConnection();
		LOGGER.info("Test environment database set up");

		// When
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = databaseTestConfig;

		// Then
		dataBasePrepareService = new DataBasePrepareService();
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	static void tearDownTestEnvironment() throws Exception {
		con.close();
	}

	@Test
	public void getNextAvailableSlotShouldReturnIntegerSlotNumberOne() throws Exception {
		// Given
		ParkingSpot parkingSpot = new ParkingSpot(0, ParkingType.CAR, true);
		int expectedValue = 1;

		// When
		int actualValue = parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());

		// Then
		assertEquals(expectedValue, actualValue);
	}

	@Test
	void updateParkingShouldReturnTrue() throws Exception {
		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		parkingSpot.setAvailable(true);
		boolean expectedValue = true;

		// When
		boolean actualValue = parkingSpotDAO.updateParking(parkingSpot);

		// Then
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void trueAssumption() {
		assumeTrue(5 > 1);
		assertEquals(5 + 2, 7);
	}

}
