package com.fast.timetable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

import com.fast.timetable.pojo.RegistrationPojo;
import com.fast.timetable.utilities.Quickstart;
//import com.fast.timetable.utilities.Quickstart;

public class AutomaticFetcher {
	private final String CONFIG_FILE = "config.properties";
	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	private final String VERSIONING = "Versioning";
	private final String VERSIONING_GOOGLE = "google";
	private final String VERSIONING_FAST = "fast";
	private Properties prop = new Properties();
	private InputStream input;
	private ScheduledFuture futureTask;
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
	private int currentInterval = 0, totalInterval = 0;
	private Date intervalStart, intervalEnd;
	private boolean onlyOnce = true;
	private final String URL = "http://localhost:8088/";
	public static int version = 1;
	public static int oldVersion = 1;

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
		//automaticFetcher.dummy();
	}

	public void execute() {
		intervalStart = Calendar.getInstance().getTime();
		intervalEnd = intervalStart;
		System.out.println("Execute() " + DATE_FORMAT.format(intervalStart));
		totalInterval = Integer.parseInt(prop.getProperty("Total_Interval"));
		System.out.println("Total Intervals :" + totalInterval);
		version = prop.getProperty("TimeTable_Version")!= null ? Integer.parseInt(prop.getProperty("TimeTable_Version")) : 1 ;
		oldVersion = version;
		generateFetcher().run();
	}

	private Runnable generateFetcher() {
		Runnable fetcher = new Runnable() {

			@Override
			public void run() {
				ExecuteShellComand shellCommand = new ExecuteShellComand();
				System.out.println("Executing run(): " + DATE_FORMAT.format(Calendar.getInstance().getTime()));
				try {
					String toFile = "/home/sshaider/FypMs/DataCleaning/input/BSCS.xlsx";
					String fromFile = "";
					if (prop.getProperty(VERSIONING) != null
							&& prop.getProperty(VERSIONING).equals(VERSIONING_GOOGLE)) {
						fromFile = prop.getProperty("Url")!= null ? prop.getProperty("Url") : "https://drive.google.com/uc?export=download&id=0BxVBN-pWNf_rc1NBck94QzlkR0E";
					} 
					else if (prop.getProperty(VERSIONING) != null
							&& prop.getProperty(VERSIONING).equals(VERSIONING_FAST)) {
						String fileName =  prop.getProperty("TimeTable_Filename")!= null ? prop.getProperty("TimeTable_Filename") : "BSCS_v";
						String extension =  prop.getProperty("TimeTable_FileType")!= null ? prop.getProperty("TimeTable_FileType") : ".xlsx";
						String[] versionAndFile = Quickstart.getVersionAndFileUrl(fileName,version,extension).split("#");
						version =  Integer.parseInt(versionAndFile[0]);
						fromFile = versionAndFile[1];
					}
					// connectionTimeout, readTimeout = 10 seconds
					
					if (onlyOnce) {
						// initialize system
						FileUtils.copyURLToFile(new URL(fromFile), new File(toFile), 10000, 10000);
						shellCommand.executeOpenRefineScript();
						String url = URL + "init?value=true";
						callController(url, null);
						System.out.println("initialize system");
						oldVersion = version;
					} else {
						if (prop.getProperty(VERSIONING) != null
								&& prop.getProperty(VERSIONING).equals(VERSIONING_FAST)) {
							if (version > oldVersion) {
								// regenerate timetable only if it is a  new version
								oldVersion = version;
								shellCommand.executePartialSchemaScript();
								FileUtils.copyURLToFile(new URL(fromFile), new File(toFile), 10000, 10000);
								String url = URL + "partial";
								callController(url, null);
								System.out.println("regenerate timetable only");
							}
						} else {
							// regenerate timetable every time 
							shellCommand.executePartialSchemaScript();
							FileUtils.copyURLToFile(new URL(fromFile), new File(toFile), 10000, 10000);
							String url = URL + "partial";
							callController(url, null);
							System.out.println("regenerate timetable only");
						}
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
		RegistrationPojo studentPojo = new RegistrationPojo();
		studentPojo.setBatch(2017);
		studentPojo.setFullName("S S HAIDER");
		studentPojo.setRollNumber("EE1202017");
		studentPojo.setSection("B");
		studentPojo.setEmail("haider188@hotmail.com");
		studentPojo.setMobileNumber("03472404043");
		Long[] courses = new Long[]{1l,3l,4l,6l};
		studentPojo.setCourses(Arrays.asList(courses));
		String url = URL +"student/register";
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<RegistrationPojo> request = new HttpEntity<>(studentPojo);
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
