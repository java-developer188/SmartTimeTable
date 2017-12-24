package com.fast.timetable.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fast.timetable.entity.TimeTable;
import com.fast.timetable.repository.TimeTableRepository;
import com.fast.timetable.service.TimeTableService;

@Component
public class ClassNotificationTask {

	private static final String PROP_FILE = "config.properties";
	private Properties prop = new Properties();

	@Autowired
	TimeTableRepository timeTableRepository;

	@Autowired
	TimeTableService timeTableService;

	private void loadProp() {
		try {
			InputStream input = getClass().getClassLoader().getResourceAsStream(PROP_FILE);
			if (input == null) {
				System.out.println("Sorry, unable to find " + PROP_FILE);
				return;
			}

			prop.load(input);

		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Sorry, unable to find " + PROP_FILE);
		}
	}

	public void execute() {
		loadProp();
		LocalDateTime localNow = LocalDateTime.now();
		String day = localNow.getDayOfWeek().name().charAt(0)
				+ localNow.getDayOfWeek().name().substring(1).toLowerCase();
		if (!day.equals("Saturday") && !day.equals("Sunday") && localNow.getHour() < 16) {
			int hour = localNow.getHour() > 12 ? localNow.getHour() - 12 : localNow.getHour();
			hour = hour + Integer.parseInt(prop.getProperty("Early_Notify_Hour"));
			String timeSlot = timeTableService.getTimeSlotFromHour(hour);
			if (timeSlot != null && !timeSlot.isEmpty()) {
				System.out.println(day);
				System.out.println(hour);
				System.out.println(timeSlot);
				timeTableService.sendSMSAndGCM(day, timeSlot);
			}
		}
	}

}
