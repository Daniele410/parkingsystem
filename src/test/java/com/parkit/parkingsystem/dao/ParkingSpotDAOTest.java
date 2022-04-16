package com.parkit.parkingsystem.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
public class ParkingSpotDAOTest {

//	private static DataBasePrepareService dataBasePrepareService;

	private ParkingSpotDAO parkingSpotDAO;
	private static LogCaptor logCaptor;

	@Mock
	private static DataBaseTestConfig dataBaseTestConfig;

	@Mock
	private static PreparedStatement ps;
	@Mock
	private static ResultSet rs;
	@Mock
	private static Connection con;

	@BeforeEach
	public void setUp() throws ClassNotFoundException, SQLException, IOException {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		logCaptor = LogCaptor.forName("ParkingSpotDAO");
		logCaptor.setLogLevelToInfo();
		when(dataBaseTestConfig.getConnection()).thenReturn(con);
	}

	@AfterAll
	static void tearDownTestEnvironment() throws Exception {
		con.close();
	}

	@Test
	public void getNextAvailableSlotShouldReturnIntegerSlotNumberOne() throws Exception {
		// Given
		when(con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(true);
		when(rs.getInt(1)).thenReturn(2);

		int expectedValue = 2;

		// When
		int actualValue = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		// Then
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void getNextAvailableSlotShouldReturnFalse() throws SQLException {
		// Given

		when(con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(ps);
		when(ps.executeQuery()).thenThrow(SQLException.class);

		// When
		parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		// Then
		assertThat(logCaptor.getErrorLogs()).contains("Error fetching next available slot");

	}

	@Test
	void updateParkingShouldReturnTrue() throws Exception {
		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		when(con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(ps);
		when(ps.executeUpdate()).thenReturn(1);
		boolean expectedValue = true;

		// When
		boolean actualValue = parkingSpotDAO.updateParking(parkingSpot);

		// Then
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void getNextAvailableSlotForFullParkingShouldReturnMinusOne() throws SQLException {
		// Given

//		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

		when(con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT)).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(false);

		// When

		int expectedValue = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

		// Then
		assertThat(expectedValue).isEqualTo(-1);

	}

	@Test
	void updateParkingShouldReturnTrueException() throws Exception {
		// Given
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		when(con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(ps);
		when(ps.executeUpdate()).thenThrow(SQLException.class);

		boolean expectedValue = false;

		// When
		boolean actualValue = parkingSpotDAO.updateParking(parkingSpot);

		// Then
		assertEquals(expectedValue, actualValue);
		assertThat(logCaptor.getErrorLogs()).contains("Error updating parking info");
	}

	@Test
	void updateParkingShouldReturnFalseForInvalidParkingNumber() throws Exception {
		// Given
		ParkingSpot parkingSpot = new ParkingSpot(4, ParkingType.CAR, false);
		when(con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT)).thenReturn(ps);
		when(ps.executeUpdate()).thenReturn(0);

		boolean expectedValue = false;

		// When
		boolean actualValue = parkingSpotDAO.updateParking(parkingSpot);

		// Then
		assertEquals(expectedValue, actualValue);
	}

	

	    @Test
	    public void updateParkingNotFoundTest() {
	    	 ParkingSpot parkingSpotNotFound = new ParkingSpot(100, ParkingType.CAR, false);
	        assertFalse(parkingSpotDAO.updateParking(parkingSpotNotFound));

	    }
	
	
}
