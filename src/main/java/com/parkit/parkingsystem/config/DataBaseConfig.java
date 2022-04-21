package com.parkit.parkingsystem.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataBaseConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseConfig");

	public Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
		logger.info("Create DB connection");
		FileInputStream file = new FileInputStream("database.properties");
		try {

			Properties properties = new Properties();
			properties.load(file);

			String DB_CLASS_NAME = (String) properties.get("db.class.name");
			String DB_URL = (String) properties.get("db.url");
			String DB_USERNAME = (String) properties.get("db.username");
			String DB_PASSWORD = (String) properties.get("db.password");

			Class.forName(DB_CLASS_NAME);
			Connection con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			return con;
		} finally {
			file.close();
		}

	}

	public void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
				logger.info("Closing DB connection");
			} catch (SQLException e) {
				logger.error("Error while closing connection", e);
			}
		}
	}

	public void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
				logger.info("Closing Prepared Statement");
			} catch (SQLException e) {
				logger.error("Error while closing prepared statement", e);
			}
		}
	}

	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				logger.info("Closing Result Set");
			} catch (SQLException e) {
				logger.error("Error while closing result set", e);
			}
		}
	}
}
