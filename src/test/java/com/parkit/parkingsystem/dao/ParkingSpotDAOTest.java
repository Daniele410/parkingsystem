package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {

	private static final Logger LOGGER = LogManager.getLogger(ParkingSpotDAOTest.class);

	private static DataBasePrepareService dataBasePrepareService;
	
	
	private static DataBaseConfig databaseTestConfig = new DataBaseTestConfig();

	
	private static ParkingSpotDAO parkingSpotDAO;
	
	private static LogCaptor logcaptor;
	
	
	
	DBConstants dBConstants;
	
	Ticket ticket;
	
	ParkingType parkingType;
	
	@Mock
	static Connection con;
	
	@Mock
	private PreparedStatement ps;
	
	@Mock
	private ResultSet rs;

	@BeforeAll
	static void setUpTestEnvironment() throws Exception {
		// Given
		con = databaseTestConfig.getConnection();
		LOGGER.info("Test environment database set up");
		parkingSpotDAO = new ParkingSpotDAO();
		dataBasePrepareService = new DataBasePrepareService();

		// When

		parkingSpotDAO.dataBaseConfig = databaseTestConfig;
		
		logcaptor = LogCaptor.forName("ParkingSpotDAO");
		logcaptor.setLogLevelToInfo();

		// Then
		

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
	public void getNextAvailableSlotShouldReturnFalse() throws Exception {
		// Given
		
		ParkingSpot parkingSpot = new ParkingSpot(0, ParkingType.CAR, true);
//		when(databaseTestConfig.getConnection()).thenReturn(con);
//		when(con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(ps);
//		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(false);
		doThrow(new RuntimeException()).when(parkingSpotDAO.getNextAvailableSlot(parkingType));
		 
		
		// When
		int actualValue = parkingSpotDAO.getNextAvailableSlot(parkingSpot.getParkingType());
		
		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Error fetching next available slot");
		verify(actualValue);
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
	void updateParkingShouldReturnTrueException() throws Exception {
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
	void updateParkingShoulReturnException() throws Exception {
		// Given
		
		ParkingSpot parkingSpot = new ParkingSpot(0, ParkingType.CAR, true);
		parkingSpot.setAvailable(true);
		parkingSpot.setAvailable(false);
		Mockito.when(parkingSpotDAO.updateParking(Mockito.any())).thenReturn(true);
		
		
		doThrow(new RuntimeException()).when(parkingSpotDAO.updateParking(parkingSpot));
		
		

		// When
		boolean actualValue = parkingSpotDAO.updateParking(parkingSpot);

		// Then
		verify(actualValue);
		assertThat(logcaptor.getErrorLogs()).contains("Error updating parking info");
		Mockito.verify(parkingSpotDAO).updateParking(Mockito.any(ParkingSpot.class));
	}
	
}
