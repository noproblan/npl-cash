package ch.npl.cash.client.users;

import java.util.List;

public interface UserProvider {
	class User {
		private int id;
		private String username;
		public User(int id, String username) {
			this.id = id;
			this.username = username;
		}
		public int getId() {
			return id;
		}
		public String getUsername() {
			return username;
		}
	}
	
	List<User> fetchUsers();
}
