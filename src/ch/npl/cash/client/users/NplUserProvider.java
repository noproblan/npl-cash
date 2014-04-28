package ch.npl.cash.client.users;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NplUserProvider implements UserProvider {
	private static final String DB_URI = "jdbc:mysql://npl.ch:3306/npl";

	public List<User> fetchUsers() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("[NplDbSync] " + "MySQL JDBC driver loaded ok.");
		} catch (ClassNotFoundException e) {
			System.out.println("[NplDbSync] " + "MYSQL Treiber konnte nicht geladen werden");
		}
	      
		List<User> result = new ArrayList<User>();
		Properties properties = new Properties();
		properties.setProperty("user", "npl_public");
		properties.setProperty("password", "****");
		properties.setProperty("ssl", "true");
		
		try (Connection connection = DriverManager.getConnection(DB_URI, properties)) {
			System.out.println("[NplDbSync] " + "Connected with " + DB_URI);
			try (Statement stmt = connection.createStatement()) {
				ResultSet resultSet = stmt.executeQuery("SELECT id, username FROM npl_view_users ORDER BY id");
				while (resultSet.next()) {
					int id = resultSet.getInt("id");
					String username = resultSet.getString("username");
					result.add(new User(id, username));
				}
			} catch (Exception e) {
				System.out.println("[NplDbSync] " + "SQL Error: " + e.getMessage());
			}
		} catch (SQLException e) {
			System.out.println("[NplDbSync] " + "Connection Error: " + e.getMessage());
		}
		return result;
	}
}
