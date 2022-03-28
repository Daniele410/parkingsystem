package com.parkit.parkingsystem.service;

import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
class InteractiveShellTest {

	Logger logger = LogManager.getLogger("InteractiveShell");

	InteractiveShell interactiveShell = new InteractiveShell();

	static OutputStreamWriter outputStreamWriter = new OutputStreamWriter(System.out);

	@Mock
	static InputReaderUtil inputReaderUtil = new InputReaderUtil();

	@Mock
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();
	
	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

	@Mock
	static TicketDAO ticketDAO = new TicketDAO();

	@Mock
	static Ticket ticket;

	@Mock
	static DataBaseTestConfig databaseConfig;


	private static LogCaptor logcaptor;

	private static Scanner scan = new Scanner(System.in);

	static String vehicleRegNumber = "qwerty";
	
	static boolean continueApp = true;

	@Mock
	static Connection con;

	Scanner input = new Scanner(System.in);
	
	
	

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		
		
	}

	@AfterAll
	static void tearDownTestEnvironment() throws Exception {

	}

//	@Test
//	public void Verify() {
//		
////		 InteractiveShell.loadInterface();
//		 
//		 
//		 
//	}


}
