package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

	TicketDAO ticketDAO;

	Ticket ticket;

	Logger logger = LogManager.getLogger(TicketDAOTest.class);

	private static LogCaptor logcaptor;

	@Mock
	private Connection con;

	@Mock
	private PreparedStatement ps;

	@Mock
	private ResultSet rs;

	@Mock
	private DataBaseTestConfig databaseConfig;

	String vehicleRegNumber = "qwerty";
	String vehicleNotRegNumber = "ABCDEF";

	@BeforeEach
	void setUp() throws SQLException, ClassNotFoundException {
		ticket = new Ticket();
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = databaseConfig;

		ticket.setId(3);
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
		ticket.setPrice(1.5);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(null);

		logcaptor = LogCaptor.forName("TicketDAO");
		logcaptor.setLogLevelToInfo();

		when(databaseConfig.getConnection()).thenReturn(con);
	}

	@Test
	public void saveTicket_withExceptionShouldReturnFalse() throws Exception {
		// Given
		ticket.setVehicleRegNumber(null);
		when(con.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(ps);
		doThrow(SQLException.class).when(ps).setString(2, ticket.getVehicleRegNumber());

		// When
		boolean result = ticketDAO.saveTicket(ticket);

		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Error fetching next available slot");
		assertFalse(result);
	}

	@Test
	public void saveTicket_withExceptionShouldReturnTrue() throws Exception {

		// Given
		ticket.setVehicleRegNumber(vehicleRegNumber);
		when(con.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(true);
		when(rs.getInt(1)).thenReturn(1);
		when(rs.getString(6)).thenReturn("CAR");
		when(rs.getDouble(3)).thenReturn(1.5);
		when(rs.getTimestamp(anyInt())).thenReturn(new Timestamp(0));
		
		
		// When

		boolean result = ticketDAO.saveTicket(ticket);

		// Then
		assertFalse(result);
	}
	
	
	

	@Test
	public void checkIfVehicleIsRegularShouldReturnFalse() throws Exception {
		// Given
		boolean expectedValue = false;

		// When
		boolean actualValue = ticketDAO.isRecurring(vehicleRegNumber);

		// Then
		assertEquals(expectedValue, actualValue);
	}

	@Test
	public void checkIfVehicleIsRecurringFalse() throws Exception {
		// Given
		boolean expectedValue = false;

		// When
		boolean actualValue = ticketDAO.isRecurring(vehicleRegNumber);

		// Then
		assertEquals(expectedValue, actualValue);
	}
	
	

	

	@Test
	public void getTicketCar_withExceptionShouldReturnTrue() throws Exception {

		// Given
		ticket.setVehicleRegNumber(vehicleRegNumber);
		when(con.prepareStatement(DBConstants.GET_TICKET)).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(true);
		when(rs.getInt(1)).thenReturn(1);
		when(rs.getString(anyInt())).thenReturn("CAR");
		when(rs.getDouble(3)).thenReturn(1.5);
		when(rs.getTimestamp(anyInt())).thenReturn(new Timestamp(0));
		
		
		// When

		Ticket result = ticketDAO.getTicket(vehicleRegNumber);

		// Then
		assertThat(result);
	}
	
	
	
	@Test
	public void getTicketBike_withExceptionShouldReturnTrue() throws Exception {

		// Given
		Ticket ticket = new Ticket();
		ticket.setVehicleRegNumber(vehicleRegNumber);
		when(con.prepareStatement(DBConstants.GET_TICKET)).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(true);
		when(rs.getInt(1)).thenReturn(1);
		when(rs.getString(6)).thenReturn("BIKE");
		when(rs.getDouble(3)).thenReturn(1.5);
		when(rs.getTimestamp(anyInt())).thenReturn(new Timestamp(0));
		
		
		
		// When

		Ticket result = ticketDAO.getTicket(vehicleRegNumber);

		// Then
		
		assertThat(result);
	}

	@Test
	public void getTicket_withExceptionShouldReturnFalse() throws Exception {

		// Given
		when(con.prepareStatement(DBConstants.GET_TICKET)).thenReturn(ps);
		doThrow(SQLException.class).when(ps).setString(2, ticket.getVehicleRegNumber());
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);

		// When
		ticketDAO.getTicket(vehicleRegNumber);

		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Error fetching ticket info");

	}
	
	
	
	@Test
	public void isRecurringReturnTrue() throws Exception {

		// Given
		ticket.setVehicleRegNumber(vehicleRegNumber);
		when(con.prepareStatement(DBConstants.CYCLIC_USER)).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(true);
		when(rs.getString(6)).thenReturn("CAR");
		
		
		// When

		boolean result = ticketDAO.isRecurring(vehicleRegNumber);

		// Then
		assertTrue(result);
		
	}
	
	
	@Test
	public void isRecurringReturnFalse() throws Exception {

		// Given
		ticket.setVehicleRegNumber(vehicleRegNumber);
		when(con.prepareStatement(DBConstants.CYCLIC_USER)).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(false);
		
		
		
		// When

		boolean result = ticketDAO.isRecurring(vehicleRegNumber);

		// Then
		assertTrue(result);
		
	}
	
	@Test
	public void isCarInside_withExceptionShouldReturnFalse() throws Exception {
		// Given
		
		when(con.prepareStatement(DBConstants.IS_CAR_INSIDE)).thenReturn(ps);
		when(ps.executeQuery()).thenReturn(rs);
		when(rs.next()).thenReturn(true);
		
	
		// When
		boolean result = ticketDAO.isCarInside(vehicleRegNumber);

		// Then
		assertThat(result);
	}

	@Test
	public void isCarInside_withExceptionShouldReturnException() throws Exception {
		// Given
		ticket.setVehicleRegNumber(vehicleRegNumber);
		when(con.prepareStatement(DBConstants.IS_CAR_INSIDE)).thenReturn(ps);
		doThrow(SQLException.class).when(ps).setString(2, ticket.getVehicleRegNumber());

		// When
		boolean result = ticketDAO.isCarInside(vehicleRegNumber);

		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Error fetching next available slot");
		assertFalse(result);
	}

	@Test
	public void isSavedTicket_withExceptionShouldReturnTrue() throws Exception {
		// Given
		ticket.setVehicleRegNumber(vehicleRegNumber);
		when(con.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(ps);

		// When
		ticketDAO.updateTicket(ticket);
		boolean result = ticketDAO.isSaved(vehicleRegNumber);

		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Error fetching recurring vehicle ");
		assertFalse(result);
	}

	@Test
	public void deleteTicket_withExceptionShouldReturnTrue() throws Exception {
		// Given
		ticket.setVehicleRegNumber(vehicleRegNumber);
		when(con.prepareStatement(DBConstants.DELETE_TICKET)).thenReturn(ps);

		// When
		ticketDAO.updateTicket(ticket);
		ticketDAO.isSaved(vehicleRegNumber);
		ticketDAO.deleteTicket(ticket);

		boolean result = ticketDAO.deleteTicket(ticket);

		// Then

		assert (result);
	}
	
	
	@Test
	public void updateTicketCar_withExceptionShouldReturnTrue() throws Exception {
		// Given
				ticket.setVehicleRegNumber(vehicleRegNumber);
				when(con.prepareStatement(DBConstants.UPDATE_TICKET)).thenReturn(ps);
				when(ps.executeQuery()).thenReturn(rs);
				when(rs.next()).thenReturn(true);
				when(rs.getInt(1)).thenReturn(1);
				when(rs.getString(anyInt())).thenReturn("CAR");
				
				
				ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
				ticket.setParkingSpot(parkingSpot);
				ticketDAO.getTicket(vehicleRegNumber);
				ticketDAO.updateTicket(ticket);
				ticketDAO.isSaved(vehicleRegNumber);
				ticket.setInTime(LocalDateTime.now());
				ticketDAO.getTicket(vehicleRegNumber);
				ticket.setOutTime(LocalDateTime.now());
				// When

				boolean result = ticketDAO.updateTicket(ticket);

				// Then
				assertTrue(result);
				
	}

	@Test
	public void updateTicket_withExceptionShouldReturnFalse() throws Exception {
		// Given
		ticket.setVehicleRegNumber(vehicleRegNumber);
		when(con.prepareStatement(DBConstants.UPDATE_TICKET)).thenReturn(ps);

		// When
		boolean result = ticketDAO.updateTicket(ticket);

		// Then
		assertThat(logcaptor.getErrorLogs()).contains("Error saving ticket info");
		assertFalse(result);
	}

	
	public class checkVehicleWhenEnteringOrExiting {

		@Test

		public void checkAlreadyOutShouldReturnTrue() throws Exception {
			// Given
			boolean expectedValue = true;

			// When
			boolean actualValue = ticketDAO.isRecurring(vehicleRegNumber);

			// Then
			assertEquals(expectedValue, actualValue);
		}

		@Test

		public void checkAlreadyInShouldReturnTrue() throws Exception {
			// Given
			boolean expectedValue = true;

			// When
			boolean actualValue = ticketDAO.isCarInside(vehicleNotRegNumber);

			// Then
			assertEquals(expectedValue, actualValue);
		}
	}
}
