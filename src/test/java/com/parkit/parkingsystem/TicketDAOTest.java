package com.parkit.parkingsystem;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.TicketDAO;

@ExtendWith(MockitoExtension.class)
public class TicketDAOTest {

	TicketDAO ticketDAO = new TicketDAO();

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Disabled
	@Test
	void test() {
		fail("Not yet implemented");
	}

}
