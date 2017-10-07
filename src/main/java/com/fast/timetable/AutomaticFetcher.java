package com.fast.timetable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

public class AutomaticFetcher {
	private final String CONFIG_FILE = "config.properties";
	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private Properties prop = new Properties();
	private InputStream input;
	private ScheduledFuture futureTask;
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
	private int currentInterval = 0, totalInterval = 0;
	private Date intervalStart, intervalEnd;
	private boolean onlyOnce = true;

	public AutomaticFetcher() {
		try {
			input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
			if (input == null) {
				System.out.println("Sorry, unable to find " + CONFIG_FILE);
				return;
			}

			prop.load(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		AutomaticFetcher automaticFetcher = new AutomaticFetcher();
		automaticFetcher.execute();
	}

	public void execute() {
		intervalStart = Calendar.getInstance().getTime();
		intervalEnd = intervalStart;
		System.out.println("Execute() " + DATE_FORMAT.format(intervalStart));
		totalInterval = Integer.parseInt(prop.getProperty("Total_Interval"));
		System.out.println("Total Intervals :" + totalInterval);
		generateFetcher().run();
	}

	private Runnable generateFetcher() {
		Runnable fetcher = new Runnable() {

			@Override
			public void run() {
				System.out.println("Executing run(): " + DATE_FORMAT.format(Calendar.getInstance().getTime()));
				String fromFile = "https://drive.google.com/uc?export=download&id=0BxVBN-pWNf_rc1NBck94QzlkR0E";
				String toFile = "/home/sshaider/FypMs/DataCleaning/input/BSCS.xlsx";
				try {
					// connectionTimeout, readTimeout = 10 seconds
					FileUtils.copyURLToFile(new URL(fromFile), new File(toFile), 10000, 10000);
					if (onlyOnce) {
						// initialize system
						System.out.println("initialize system");
					} else {
						// regenerate timetable only
						System.out.println("regenerate timetable only");
					}

					changeReadInterval();
				} catch (Exception e) {
					e.printStackTrace();
					changeReadInterval();
				}
			}
		};
		return fetcher;
	}

	/**
	 * This method will reschedule given JOB with the new param time
	 */
	private void changeReadInterval() {
		Date currentDate = Calendar.getInstance().getTime();
		System.out.println("Current Timestamp: " + DATE_FORMAT.format(currentDate));
		if (onlyOnce || (currentDate.after(intervalEnd))) {
			currentInterval++;
			onlyOnce = false;
			if (currentInterval > totalInterval) {
				futureTask.cancel(true);
			} else {
				int numberOfWeeks = Integer.parseInt(prop.getProperty("Interval_" + currentInterval));
				intervalEnd = calculateIntervalEnd(currentDate, numberOfWeeks);
				System.out.println("Interval " + currentInterval + " will End at :" + intervalEnd);

				int numberOfhours = Integer.parseInt(prop.getProperty("Value_" + currentInterval));
				System.out.println("Interval " + currentInterval + " fetches file every " + numberOfhours + " hours.");

				if (numberOfhours > 0) {
					if (futureTask != null) {
						futureTask.cancel(true);
					}
					int delayInSeconds = numberOfhours;
					// int delayInSeconds = numberOfhours * 3600 ;
					futureTask = scheduledExecutorService.scheduleAtFixedRate(generateFetcher(), delayInSeconds,
							delayInSeconds, TimeUnit.SECONDS);
				}
			}
		}
	}

	private Date calculateIntervalEnd(Date date, int weeks) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, weeks); // for testing in minutes
		// cal.add(Calendar.DATE, weeks * 7);
		return cal.getTime();
	}
}
