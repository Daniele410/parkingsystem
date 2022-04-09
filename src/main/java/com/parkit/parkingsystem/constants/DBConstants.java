package com.parkit.parkingsystem.constants;

public class DBConstants {
	/*
	 * cette classe contient différentes requêtes(query) SQL
	 */

	public static final String GET_NEXT_PARKING_SPOT = "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE =?";
	public static final String UPDATE_PARKING_SPOT = "update parking set available =? where PARKING_NUMBER =?";
	public static final String SAVE_TICKET = "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";
	public static final String UPDATE_TICKET = "update ticket set PRICE=?, OUT_TIME=?, IN_TIME=? where ID=?";
	public static final String GET_TICKET = "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.ID desc limit 1";

	// Requête pour vérifier si une voiture est déjà venue dans le parking
	public static final String CYCLIC_USER = "SELECT * FROM ticket WHERE VEHICLE_REG_NUMBER=? and OUT_TIME IS NOT NULL";
	// Requête pour savoir si la voiture est dans le parking
	public static final String IS_CAR_INSIDE = "SELECT * from ticket where VEHICLE_REG_NUMBER=? and OUT_TIME is null;";
	
}


//Requête pour efacer le ticket dans le parking
//	public static final String DELETE_TICKET = "delete from ticket where ID=?";
	// "UPDATE ticket SET PARKING_NUMBER = 0"

//"select count(VEHICLE_REG_NUMBER) from ticket t where t.VEHICLE_REG_NUMBER=?";

//public static final String CYCLIC_USER = "select * from ticket t where t.VEHICLE_REG_NUMBER = ? and OUT_TIME is not null;";

//public static final String CYCLIC_USER = "select exists (select * from ticket t where t.VEHICLE_REG_NUMBER = ?)";

//public static final String CYCLIC_USER = "select count(VEHICLE_REG_NUMBER) from ticket t where t.VEHICLE_REG_NUMBER=?";