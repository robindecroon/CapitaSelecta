package util;

import java.util.Scanner;

public class Prompter {
	public static String prompt(String question) {
		System.err.println(question);
		Scanner scanner = new Scanner(System.in);
		
		return scanner.nextLine();
	}
}
