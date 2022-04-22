package com.parkit.parkingsystem.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DBConstantsTest {

	DBConstants dBConstant = new DBConstants();

	@BeforeEach
	void setUp() throws Exception {

	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	public void verifyQueryGetNextParkingSpot() {

		assertEquals(DBConstants.GET_NEXT_PARKING_SPOT,
				"select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE =?");

	}

	@Test
	public void verifyQueryUpdateParkingSpot() {

		assertEquals(DBConstants.UPDATE_PARKING_SPOT,
				"update parking set available =? where PARKING_NUMBER =?");

	}

	@Test
	public void verifyQueryUpdateTicket() {

		assertEquals(DBConstants.UPDATE_TICKET,
				"update ticket set PRICE=?, OUT_TIME=?, IN_TIME=? WHERE ID=?");

	}

	@Test
	public void verifyQueryGetTicket() {

		assertEquals(DBConstants.GET_TICKET,
				"select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.ID desc limit 1");

	}

	@Test
	public void verifyQueryCyclicUser() {

		assertEquals(DBConstants.CYCLIC_USER,
				"SELECT * FROM ticket WHERE VEHICLE_REG_NUMBER=? and OUT_TIME IS NOT NULL");

	}

	@Test
	public void verifyQuerySaveTicket() {

		assertEquals(DBConstants.IS_CAR_INSIDE,
				"select * from ticket where VEHICLE_REG_NUMBER=? and OUT_TIME is NULL");

	}

}
