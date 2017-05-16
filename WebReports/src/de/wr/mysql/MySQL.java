package de.wr.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL {
	public static Connection connection;
	public static String prefix = "[WebReports] ";
	
	public static void onConnect() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + MySQLData.database + "?autoReconnect=true",
					MySQLData.username, MySQLData.password);
			System.out.println(prefix + "MySQL-Verbindung hergestellt!");

			System.out.println(prefix + "MySQL aktiviert");
			onCreate();
		}

		catch (SQLException e) {
			e.printStackTrace();
			System.err.println(prefix + "MySQL-Verbindung konnte nicht hergestellt werden!");
			System.err.println(prefix + "MySQL wurde deaktiviert");
		}
	}

	public static void onCreate() {		
		MySQL.onUpdate("CREATE TABLE IF NOT EXISTS reports(id INT AUTO_INCREMENT, reporter VARCHAR(16), reported VARCHAR(16), server VARCHAR(50), grund VARCHAR(255) DEFAULT 'Hacking', time datetime NOT NULL, moderator VARCHAR(25) DEFAULT 'none', status INT(1) DEFAULT 0, secret VARCHAR(16), PRIMARY KEY (id))");
	}
	
	public static void onDisconect() {
		if (connection != null) {
			try {
				connection.close();

				System.out.println(prefix + " MySQL-Verbindung beendet!");
			}

			catch (SQLException e) {
				e.printStackTrace();

				System.err.println(prefix + " MySQL-Verbindung konnte nicht getrennt werden!");
			}
		}
	}

	public static void onUpdate(String qry) {
		try {
			Statement stmt = connection.createStatement();

			stmt.executeUpdate(qry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ResultSet onQuery(String qry) {
		ResultSet rs = null;

		try {
			Statement stmt = connection.createStatement();

			rs = stmt.executeQuery(qry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
}