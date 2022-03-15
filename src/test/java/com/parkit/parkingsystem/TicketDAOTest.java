package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	public void checkIfVehicleIsRecurringTrue() throws Exception {
		// Given
		boolean expectedValue = true;

		// When
		boolean actualValue = ticketDAO.isRecurring(vehicleRegNumber);

		// Then
		assertEquals(expectedValue, actualValue);

	}

	@Test
	public void saveTicketShouldReturnTrue() throws Exception {

		// Given
		boolean expectedValue = false;
		Ticket ticket = new Ticket();
		ticket.setInTime(LocalDateTime.now());
		ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.CAR, true);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setPrice(0.0);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(null);

		// When
		ticketDAO.saveTicket(ticket);
		String actualPlate = ticketDAO.getTicket(vehicleRegNumber).getVehicleRegNumber();

		// Then
		assertEquals(expectedValue, actualPlate);
		assertEquals(vehicleRegNumber, actualPlate);

	}

	@Test
	public void updateTicketShouldReturnTrue() throws Exception {

		// Given
		Ticket ticket = new Ticket();
		ticket = ticketDAO.getTicket(vehicleRegNumber);
		boolean expectedValue = true;

		// When
		boolean actualValue = ticketDAO.updateTicket(ticket);

		// Then
		assertEquals(expectedValue, actualValue);

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
