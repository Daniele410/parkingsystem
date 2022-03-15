package com.parkit.parkingsystem;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

	TicketDAO ticketDAO = new TicketDAO();

	Ticket ticket = new Ticket();

	@Mock
	Logger logger = LogManager.getLogger("TicketDAO");

	@Mock
	DataBaseConfig dataBaseConfig = new DataBaseConfig();

	private static DataBasePrepareService dataBasePrepareService;

	Connection con;

	String vehicleRegNumber = "qwerty";

	@BeforeAll
	void setUp() throws Exception, SQLException {

		Connection con;
		PreparedStatement ps;

		con = dataBaseConfig.getConnection();
		logger.info("Test environment database has been set up");

		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseConfig;

		// Clearing previous tested entries
		dataBasePrepareService = new DataBasePrepareService();
		dataBasePrepareService.clearDataBaseEntries();

		// CREATING TEST ENTRIES IN DATABASE
		con = dataBaseConfig.getConnection();
		ps = con.prepareStatement(DBConstants.GET_TICKET);

		// Query : Save a ticket in database to check if vehicle is regular or not
		ps.setInt(1, ticket.getParkingSpot().getId());
		ps.setString(1, ticket.getVehicleRegNumber());
		ps.setDouble(3, ticket.getPrice());
		ps.setTimestamp(4, Timestamp.valueOf(ticket.toString()));
		ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (Timestamp.valueOf(ticket.getOutTime())));
		ps.execute();

	}

	@AfterAll
	void tearDown() throws Exception {
		con.close();
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
	void checkIfVehicleIsRecurringTrue() throws Exception {
		// Given
		boolean expectedValue = true;

		// When
		boolean actualValue = ticketDAO.isRecurring(vehicleRegNumber);

		// Then
		assertEquals(expectedValue, actualValue);

	}

	@Test
	public void saveTicket_shouldReturnTrue() throws Exception {
		// ARRANGE

		boolean expectedValue = false;
		Ticket ticket = new Ticket();
		ticket.setInTime(LocalDateTime.now());

		ParkingSpot parkingSpot = new ParkingSpot(2, ParkingType.CAR, true);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setPrice(0.0);
		ticket.setInTime(LocalDateTime.now());
		ticket.setOutTime(null);

		// ACT

		ticketDAO.saveTicket(ticket);
		String actualPlate = ticketDAO.getTicket(vehicleRegNumber).getVehicleRegNumber();

		// ASSERT
		assertEquals(expectedValue, actualPlate);
		assertEquals(vehicleRegNumber, actualPlate);

	}

	public void updateTicket_shouldReturnTrue() throws Exception {

		// ARRANGE
		Ticket ticket = new Ticket();
		ticket = ticketDAO.getTicket(vehicleRegNumber);
		boolean expectedValue = true;

		// ACT
		boolean actualValue = ticketDAO.updateTicket(ticket);

		// ASSERT
		assertEquals(expectedValue, actualValue);

	}
}
