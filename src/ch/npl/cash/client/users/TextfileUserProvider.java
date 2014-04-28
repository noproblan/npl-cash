package ch.npl.cash.client.users;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TextfileUserProvider implements UserProvider {
	private static final String TEXT_FILE = "users.txt";

	public List<User> fetchUsers() {
		List<User> result = new ArrayList<User>();
		int i = 1;
		Scanner scanner;
		try {
			scanner = new Scanner(new File(TEXT_FILE));
	        while(scanner.hasNextLine()){
	        	result.add(new User(i++, scanner.nextLine()));
	        }
	        scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return result;
	}
}
