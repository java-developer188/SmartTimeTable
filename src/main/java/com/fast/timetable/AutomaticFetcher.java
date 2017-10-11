package com.fast.timetable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fast.timetable.pojo.StudentPojo;

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
	private final String URL = "http://localhost:8088/";

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
//		automaticFetcher.execute();
		automaticFetcher.dummy();
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
				ExecuteShellComand shellCommand = new ExecuteShellComand();
				System.out.println("Executing run(): " + DATE_FORMAT.format(Calendar.getInstance().getTime()));
				String fromFile = "https://drive.google.com/uc?export=download&id=0BxVBN-pWNf_rc1NBck94QzlkR0E";
				String toFile = "/home/sshaider/FypMs/DataCleaning/input/BSCS.xlsx";
				try {
					// connectionTimeout, readTimeout = 10 seconds
					
					if (onlyOnce) {
						// initialize system
						FileUtils.copyURLToFile(new URL(fromFile), new File(toFile), 10000, 10000);
						shellCommand.executeOpenRefineScript();
						String url = URL +"init?value=true";
						callController(url,null);
						System.out.println("initialize system");
					} else {
						// regenerate timetable only
						shellCommand.executePartialSchemaScript();
						FileUtils.copyURLToFile(new URL(fromFile), new File(toFile), 10000, 10000);
						String url = URL +"partial";
						callController(url,null);
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
	
	
	public void dummy(){
		StudentPojo studentPojo = new StudentPojo();
		studentPojo.setBatch(2017);
		studentPojo.setFullName("S S HAIDER");
		studentPojo.setRollNumber("EE1202017");
		studentPojo.setSection("B");
		studentPojo.setEmail("haider188@hotmail.com");
		studentPojo.setMobileNumber("03472404043");
		Integer[] courses = new Integer[]{1,3,4,6};
		studentPojo.setCourses(Arrays.asList(courses));
		String url = URL +"student/register";
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<StudentPojo> request = new HttpEntity<>(studentPojo);
		ResponseEntity<String> response = restTemplate
		  .exchange(url, HttpMethod.POST, request, String.class);
		  
//		Map<String,Object> map = new HashMap<>();
//		map.put("Student", studentPojo);
//		postController(url, map);
	}
	
	private boolean postController(String url,Map params){
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<Map<String,Object>> request = new HttpEntity<>(params);
		ResponseEntity<String> response = restTemplate
		  .exchange(url, HttpMethod.POST, request, String.class);
		  
		return response.getStatusCode() == HttpStatus.OK;

	}
	
	private boolean callController(String url,Map params){
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<Map<String,Object>> request = new HttpEntity<>(params);
		ResponseEntity<String> response = restTemplate
		  .exchange(url, HttpMethod.GET, request, String.class);
		  
		return response.getStatusCode() == HttpStatus.OK;

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
				//System.out.println("Interval " + currentInterval + " fetches file every " + numberOfhours + " hours.");
				System.out.println("Interval " + currentInterval + " fetches file every " + numberOfhours + " seconds.");
				
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
