package com.fast.timetable.utilities;

public class EncryptorDecryptor {
	
	public static String encryptDecrypt(String input) {
		char[] key = { 's', 'm', 'a', 'r', 't' }; // Can be any chars, and any
													// length array
		StringBuilder output = new StringBuilder();

		for (int i = 0; i < input.length(); i++) {
			output.append((char) (input.charAt(i) ^ key[i % key.length]));
		}

		return output.toString();
	}

}
