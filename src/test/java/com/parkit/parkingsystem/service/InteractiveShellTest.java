package com.parkit.parkingsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
class InteractiveShellTest {

	@Mock
	InputReaderUtil inputReaderUtil;

	@Mock
	ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO();

	TicketDAO ticketDAO = new TicketDAO();

	@Mock
	ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

	InteractiveShell interactiveShell = new InteractiveShell();

	@BeforeEach
	void setUp() throws Exception {

//		InteractiveShell.loadInterface();

	}

//	@Disabled
//	@Test
//	void Trytest() {
//
//		assertEquals("App initialized!!!", logger.toString()
//				.trim());
//
//	}

}
