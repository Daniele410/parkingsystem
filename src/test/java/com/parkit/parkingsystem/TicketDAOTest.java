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

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAOTest {

	TicketDAO ticketDAO;

	Ticket ticket;

	Logger logger = LogManager.getLogger(TicketDAOTest.class);

	DataBaseConfig dataBaseConfig = new DataBaseConfig();

	private static DataBasePrepareService dataBasePrepareService;

	Connection con;

	String vehicleRegNumber = "qwerty";
	String vehicleNotRegNumber = "ABCDEF";

	@BeforeAll
	void setUp() throws Exception, SQLException {

		con = dataBaseConfig.getConnection();
		logger.info("Test environment database has been set up");
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseConfig;
		dataBasePrepareService = new DataBasePrepareService();
		dataBasePrepareService.clearDataBaseEntries();

		PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
		ps.setInt(1, ticket.getParkingSpot().getId());
		ps.setString(2, ticket.getVehicleRegNumber());
		ps.setDouble(3, ticket.getPrice());
		ps.setTimestamp(4, Timestamp.valueOf(ticket.getInTime()));
		ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (Timestamp.valueOf(ticket.getOutTime())));
		ps.execute();

	}

	@AfterAll
	void tearDown() throws Exception {
		con.close();
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
	void checkIfVehicleIsRecurringTrue() throws Exception {
		// Given
		boolean expectedValue = true;

		// When
		boolean actualValue = ticketDAO.isRecurring(vehicleRegNumber);

		// Then
		assertEquals(expectedValue, actualValue);

	}

	@Test
	public void saveTicketShouldReturnTrue() throws Exception {
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

	public void updateTicketShouldReturnTrue() throws Exception {

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
