package ch.npl.cash.client.users;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CsvUserProvider implements UserProvider {
	private static final String TEXT_FILE = "users.csv";

	public List<User> fetchUsers() {
		List<User> result = new ArrayList<User>();
		Scanner scanner;
		try {
			scanner = new Scanner(new File(TEXT_FILE));
	        while(scanner.hasNextLine()){
	        	String[] data = scanner.nextLine().split(";");
	        	result.add(new User(Integer.valueOf(data[0]), data[1]));
	        }
	        scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return result;
	}
}
