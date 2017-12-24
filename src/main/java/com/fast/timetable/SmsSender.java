package com.fast.timetable;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SmsSender {

	public static final String username = "923472404043";
	public static final String password = "9318";
	public static final String testing_mobile = "923002326886";
	public static final String sender = "Smart Timetable Notifier";
	public static final String testing_message = "Smart TimeTable Final Project SMS feature working fine. This is a testing SMS";
	public static final String testing_sendSmsUrl = "http://sendpk.com/api/sms.php?username=" + username + "&password="
			+ password + "&mobile=" + testing_mobile + "&sender=" + sender + "&message=" + testing_message;

	public static final String hajana_API_KEY = "WbbPHgQTVgFE";
	public static final String hajanaUrl = "http://www.hajanaone.com/api/sendsms.php?apikey=" + hajana_API_KEY
			+ "&phone=" + testing_mobile + "&sender=" + sender + "&message=" + testing_message;

	public static void main(String[] args) {

		// SmsSender smsSender = new SmsSender();
		// smsSender.sendSms("03472404043", "testing");
		// smsSender.sendSms("+923472404043", "testing");
		// smsSender.sendSms("00923472404043", "testing");
		// smsSender.sendSms("+920347240404", "testing");
		// smsSender.sendSms("0092034724040", "testing");
		if (httpGet(testing_sendSmsUrl)) {
			System.out.println("sms sent");
		} else {
			System.out.println("sms failed");
		}
	}

	public boolean sendSms(String recipent, String message) {
		String formattedNumber = "";
		if (recipent.matches("^(0[0-9]{10})$")) {
			formattedNumber = "92" + recipent.substring(1);
		} else if (recipent.matches("^(\\+92[1-9][0-9]{9})$")) {
			formattedNumber = recipent.substring(1);
		} else if (recipent.matches("^(0092[1-9][0-9]{9})$")) {
			formattedNumber = recipent.substring(2);
		}

		if (formattedNumber.matches("^(92[0-9]{10})$")) {

			String sendSmsUrl = "http://sendpk.com/api/sms.php?username=" + username + "&password=" + password
					+ "&mobile=" + recipent + "&sender=" + sender + "&message=" + message;

			if (httpGet(sendSmsUrl)) {
				System.out.println("SMS sent successfully to " + recipent);
				return true;
			} else {
				System.out.println("Failed to send SMS to " + recipent);
				return false;
			}
		} else {
			System.out.println("Phone Number Invalid");
			return false;
		}
	}

	static boolean httpGet(String url) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		return response.getStatusCode() == HttpStatus.OK;

	}
}