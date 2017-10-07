package com.fast.timetable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fast.timetable.entity.Course;
import com.fast.timetable.entity.Teacher;

public class DaySheetParser {

	private static final String CST_SPLIT_REGEX = "\\s{2,}";
	private static final String PROP_FILE = "config.properties";
	private static final String KEY_TIMETABLE_FILE = "TIMETABLE_FILENAME";
	private static final String NEW_TIME_TABLE_SHEET = "TimeTable_Sheet";
	private static final String INTERMEDIATE_TIMETABLE = "INTERMEDIATE_TIMETABLE_FILE";

	private static final int DAY = 0;
	private static final int TIMESLOT = 1;
	private static final int ROOM = 2;
	private static final int COURSE = 3;
	private static final int SECTION = 4;
	private static final int TEACHER = 5;
	
	private static final int START_SHEET = 6;
	private static final int END_SHEET = 10;
	private static final int START_ROW = 4;
	private static final int END_ROW = 31;
	private static final int START_COLUMN = 1;
	private static final int END_COLUMN = 8;
	private static final int LAB_ROW = 22;
	private static final int NAMAZ_BREAK = 6;
	private static final int FRIDAY_SHEET = 10;
	
	
	private String fileName;
	private Properties prop = new Properties();
	private Workbook readWorkbook,writeWorkBook;
	private Sheet currentReadSheet,writeSheet;


	public static Map<String,Course> COURSES = new HashMap<>();
	public static Map<String,Course> LAB_COURSES = new HashMap<>();
	public static Map<String,Course> EX_COURSES = new HashMap<>();
	public static Map<String,Teacher> TEACHERS = new HashMap<>();

	public static Map<Integer, String> ROOMS = new HashMap<>();
	public static List<String> TIME_SLOTS = new ArrayList<>();

	public DaySheetParser(List<Course>courses, List<Teacher> teachers) {
		try {
			InputStream input = getClass().getClassLoader().getResourceAsStream(PROP_FILE);
			if (input == null) {
				System.out.println("Sorry, unable to find " + PROP_FILE);
				return;
			}

			prop.load(input);
			fileName = prop.getProperty(KEY_TIMETABLE_FILE);
			
		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "File: "+fileName+" not found!");
		}
		
		try {
			readWorkbook = new XSSFWorkbook(new FileInputStream(new File(fileName)));
		} catch (FileNotFoundException e) {
			Logger.getGlobal().log(Level.SEVERE, "File: "+fileName+" not found!");
		} catch (IOException e) {			
			Logger.getGlobal().log(Level.SEVERE, "Error opening file: "+fileName);
		}
		createCourseNameMap(courses);
		createTeacherNameMap(teachers);
	}
	


	public  void execute() {
		try {

			writeWorkBook = new XSSFWorkbook();
			writeSheet = writeWorkBook.createSheet(NEW_TIME_TABLE_SHEET);
		
			int writeRowCount = 0;
			Row heading = writeSheet.createRow(writeRowCount);
			heading.createCell(DAY).setCellValue("Day");
			heading.createCell(TIMESLOT).setCellValue("Time Slot");
			heading.createCell(ROOM).setCellValue("Room");
			heading.createCell(COURSE).setCellValue("Course");
			heading.createCell(SECTION).setCellValue("Section");
			heading.createCell(TEACHER).setCellValue("Teacher");
			writeRowCount++;

			for (int sheet = START_SHEET; sheet <= END_SHEET; sheet++) {
				currentReadSheet = readWorkbook.getSheetAt(sheet);
				System.out.println("Sheet: " + currentReadSheet.getSheetName());
				
				extractRooms(currentReadSheet);
				extractTimeSlots(currentReadSheet);
				boolean isExCourse ;
				for (int row = START_ROW; row < END_ROW; row++) {
					if (row != LAB_ROW) {
						Row currentRow = currentReadSheet.getRow(row);
						for (int col = START_COLUMN; col <= END_COLUMN; col++) {
							if (sheet != FRIDAY_SHEET || col != NAMAZ_BREAK)  {
								Cell cell = currentRow.getCell(col);
								if (cell != null && cell.getCellTypeEnum().equals(CellType.STRING)) {
									writeRowCount++;
									if (row < LAB_ROW) {
										Row writeRow = writeSheet.createRow(writeRowCount);
										writeRow.createCell(DAY).setCellValue(currentReadSheet.getSheetName().trim());
										writeRow.createCell(TIMESLOT).setCellValue(TIME_SLOTS.get(col - 1));
										writeRow.createCell(ROOM).setCellValue(ROOMS.get(row));
										String dirty = cell.getStringCellValue();
										System.out.println(dirty);
										String[] courseSecTeacher = dirty.split(CST_SPLIT_REGEX);

										isExCourse =false;
											for (String name : EX_COURSES.keySet()) {
												Pattern p = Pattern.compile(name);   // the pattern to search for
											    Matcher m = p.matcher(courseSecTeacher[0]);
												if (m.find()) {
													System.out.println(name);
													writeRow.createCell(COURSE).setCellValue(EX_COURSES.get(name).getFullName());
													String[] courseSec = courseSecTeacher[0].split(name);
													String section = "";
													if (courseSec[1].trim().charAt(0) == '-') {
														if (courseSec[1].trim().contains("("))
															section = courseSec[1].trim().substring(1,
																	courseSec[1].trim().indexOf('('));
														else
															section = courseSec[1].trim().substring(1);
													} else {
														if (courseSec[1].trim().contains("("))
															section = courseSec[1].trim().substring(0,
																	courseSec[1].trim().indexOf('('));
														else
															section = courseSec[1].trim();
													}
													section = section.replace("Tutorial", "");
													writeRow.createCell(SECTION).setCellValue(section.trim().toUpperCase());
													System.out.println(section.trim());
													isExCourse = true;
													break;
													// System.out.println(courseSec[1]);
												}
											}
										 if(!isExCourse) {
											for (String name : COURSES.keySet()) {
												if (courseSecTeacher[0].contains(name)) {
													System.out.println(name);
													writeRow.createCell(COURSE).setCellValue(COURSES.get(name).getFullName());
													String[] courseSec = courseSecTeacher[0].split(name);
													String section = "";
													if (courseSec[1].trim().charAt(0) == '-') {
														if (courseSec[1].trim().contains("("))
															section = courseSec[1].trim().substring(1,
																	courseSec[1].trim().indexOf('('));
														else
															section = courseSec[1].trim().substring(1);
													} else {
														if (courseSec[1].trim().contains("("))
															section = courseSec[1].trim().substring(0,
																	courseSec[1].trim().indexOf('('));
														else
															section = courseSec[1].trim();
													}
													section = section.replace("Tutorial", "");
													writeRow.createCell(SECTION).setCellValue(section.trim().toUpperCase());
													System.out.println(section.trim());
													break;
													// System.out.println(courseSec[1]);
												}
											}
										}
										 String[] uncapitalize = courseSecTeacher[1].trim().split(" ");
											String capitalize = "";
											for (String s : uncapitalize) {
												capitalize += (s.length() == 0) ? s
														: s.substring(0, 1).toUpperCase() + s.substring(1) + " ";
											}
											capitalize = capitalize.trim();
										if (TEACHERS.containsKey(capitalize)) {
											writeRow.createCell(TEACHER).setCellValue(
													TEACHERS.get(capitalize).getName());
											System.out.println(TEACHERS.get(capitalize).getName());
										}
										else{
											writeRow.createCell(TEACHER).setCellValue(capitalize);
											System.out.println(capitalize);
										}
									} else {
										if (row > LAB_ROW) {
											// Row writeRow =
											// writeSheet.createRow(writeRowCount);
											// writeRow.createCell(0).setCellValue(TIME_SLOTS.get(col
											// - 1));
											// writeRow.createCell(1).setCellValue(ROOMS.get(row));
											String dirty = cell.getStringCellValue();
											System.out.println(dirty);
											String[] courseSecTeacher = dirty.split(CST_SPLIT_REGEX);
											
											isExCourse =false;
											for (String name : EX_COURSES.keySet()) {
												Row writeRow = writeSheet.createRow(writeRowCount);
												writeRow.createCell(DAY).setCellValue(currentReadSheet.getSheetName().trim());
												writeRow.createCell(TIMESLOT).setCellValue(TIME_SLOTS.get(col - 1));
												writeRow.createCell(ROOM).setCellValue(ROOMS.get(row));
												Pattern p = Pattern.compile(name);   // the pattern to search for
											    Matcher m = p.matcher(courseSecTeacher[0]);
												if (m.find()) {
													System.out.println(name);
													writeRow.createCell(COURSE).setCellValue(EX_COURSES.get(name).getFullName());
													String[] courseSec = courseSecTeacher[0].split(name);
													String section = "";
													if (courseSec[1].trim().charAt(0) == '-') {
														if (courseSec[1].trim().contains("("))
															section = courseSec[1].trim().substring(1,
																	courseSec[1].trim().indexOf('('));
														else
															section = courseSec[1].trim().substring(1);
													} else {
														if (courseSec[1].trim().contains("("))
															section = courseSec[1].trim().substring(0,
																	courseSec[1].trim().indexOf('('));
														else
															section = courseSec[1].trim();
													}
													section = section.replace("Tutorial", "");
													writeRow.createCell(SECTION).setCellValue(section.trim().toUpperCase());
													System.out.println(section.trim());
													String[] uncapitalize = courseSecTeacher[1].trim().split(" ");
													String capitalize = "";
													for (String s : uncapitalize) {
														capitalize += (s.length() == 0) ? s
																: s.substring(0, 1).toUpperCase() + s.substring(1) + " ";
													}
													capitalize = capitalize.trim();
												if (TEACHERS.containsKey(capitalize)) {
													writeRow.createCell(TEACHER).setCellValue(
															TEACHERS.get(capitalize).getName());
													System.out.println(TEACHERS.get(capitalize).getName());
												}
												else{
													writeRow.createCell(TEACHER).setCellValue(capitalize);
													System.out.println(capitalize);
												}
													isExCourse = true;
													break;
													// System.out.println(courseSec[1]);
												}
											}
										 if(!isExCourse) {
											
											boolean notInLab = true;
											for (String name : LAB_COURSES.keySet()) {
												if (courseSecTeacher[0].contains(name)) {
													notInLab = false;
													System.out.println(name);
													// writeRow.createCell(2).setCellValue(name);
													String[] courseSec = courseSecTeacher[0].split(name);
													if (courseSec[1].trim().contains("+")) {
														// Lab special case when
														// two sections are
														// combined
														
														String section[] = courseSec[1].trim().split("\\+");
														for (int s = 0; s < section.length; s++) {
															if (section[s].trim().charAt(0) == '-') {
																if (section[s].trim().contains("("))
																	section[s] = section[s].trim().substring(1,
																			section[s].trim().indexOf('('));
																else
																	section[s] = section[s].trim().substring(1);
															} else {
																if (section[s].trim().contains("("))
																	section[s] = section[s].trim().substring(0,
																			section[s].trim().indexOf('('));
																else
																	section[s] = section[s].trim();
															}
														}
														String teacher[] = null;
														if (courseSecTeacher[1].trim().contains("+")) {
															teacher = courseSecTeacher[1].trim().split("\\+");
														}
														else{
															if (courseSecTeacher[1].trim().contains("&")) {
																teacher = courseSecTeacher[1].trim().split("&");
															}
														}
														for (int i = 0; i < section.length; i++) {
															Row writeRow = writeSheet.createRow(writeRowCount);
															writeRow.createCell(DAY)
																	.setCellValue(currentReadSheet.getSheetName());
															writeRow.createCell(TIMESLOT)
																	.setCellValue(TIME_SLOTS.get(col - 1));
															writeRow.createCell(ROOM).setCellValue(ROOMS.get(row));
															writeRow.createCell(COURSE).setCellValue(LAB_COURSES.get(name).getFullName());
															section[i] = section[i].replace("Tutorial", "");
															writeRow.createCell(SECTION)
																	.setCellValue(section[i].trim().toUpperCase());
															System.out.println(section[i].trim());
															String[] uncapitalize = teacher[i].trim().split(" ");
															String capitalize = "";
															for (String s : uncapitalize) {
																capitalize += (s.length() == 0) ? s
																		: s.substring(0, 1).toUpperCase() + s.substring(1) + " ";
															}
															capitalize = capitalize.trim();
															if (TEACHERS.containsKey(capitalize)) {
																writeRow.createCell(TEACHER)
																		.setCellValue(TEACHERS.get(capitalize).getName());
																System.out.println(TEACHERS.get(capitalize).getName());
															}
															else {
																writeRow.createCell(TEACHER)
																		.setCellValue(capitalize);
																System.out.println(capitalize);
															}
															writeRowCount++;
														}
													} else {
														String section = "";
														if (courseSec[1].trim().charAt(0) == '-') {
															if (courseSec[1].trim().contains("("))
																section = courseSec[1].trim().substring(1,
																		courseSec[1].trim().indexOf('('));
															else
																section = courseSec[1].trim().substring(1);
														} else {
															if (courseSec[1].trim().contains("("))
																section = courseSec[1].trim().substring(0,
																		courseSec[1].trim().indexOf('('));
															else
																section = courseSec[1].trim();
														}
														Row writeRow = writeSheet.createRow(writeRowCount);
														writeRow.createCell(DAY)
																.setCellValue(currentReadSheet.getSheetName());
														writeRow.createCell(TIMESLOT)
																.setCellValue(TIME_SLOTS.get(col - 1));
														writeRow.createCell(ROOM).setCellValue(ROOMS.get(row));
														writeRow.createCell(COURSE).setCellValue(LAB_COURSES.get(name).getFullName());
														section = section.replace("Tutorial", "");
														writeRow.createCell(SECTION).setCellValue(section.trim().toUpperCase());
														System.out.println(section.trim());
														String[] uncapitalize = courseSecTeacher[1].trim().split(" ");
														String capitalize = "";
														for (String s : uncapitalize) {
															capitalize += (s.length() == 0) ? s
																	: s.substring(0, 1).toUpperCase() + s.substring(1) + " ";
														}
														capitalize = capitalize.trim();
														if (TEACHERS.containsKey(capitalize)) {
															writeRow.createCell(TEACHER)
																	.setCellValue(TEACHERS.get(capitalize).getName());
															System.out.println(TEACHERS.get(capitalize).getName());
														}
														else{
															writeRow.createCell(TEACHER)
																	.setCellValue(capitalize);
															System.out.println(capitalize);
														}
													}
													break;
												}
											}
											if (notInLab) {
												for (String name : COURSES.keySet()) {
													if (courseSecTeacher[0].contains(name)) {
														System.out.println(name);
														// writeRow.createCell(2).setCellValue(name);
														String[] courseSec = courseSecTeacher[0].split(name);
														if (courseSec[1].trim().contains("+")) {
															// Lab special case
															// when
															// two sections are
															// combined
															String section[] = courseSec[1].trim().split("\\+");
															for (int s = 0; s < section.length; s++) {
																if (section[s].trim().charAt(0) == '-') {
																	if (section[s].trim().contains("("))
																		section[s] = section[s].trim().substring(1,
																				section[s].trim().indexOf('('));
																	else
																		section[s] = section[s].trim().substring(1);
																} else {
																	if (section[s].trim().contains("("))
																		section[s] = section[s].trim().substring(0,
																				section[s].trim().indexOf('('));
																	else
																		section[s] = section[s].trim();
																}
															}
															String teacher[] = null;
															if (courseSecTeacher[1].trim().contains("+")) {
																teacher = courseSecTeacher[1].trim().split("\\+");
															}
															else{
																if (courseSecTeacher[1].trim().contains("&")) {
																	teacher = courseSecTeacher[1].trim().split("&");
																}
															}
															for (int i = 0; i < section.length; i++) {
																Row writeRow = writeSheet.createRow(writeRowCount);
																writeRow.createCell(DAY)
																		.setCellValue(currentReadSheet.getSheetName());
																writeRow.createCell(TIMESLOT)
																		.setCellValue(TIME_SLOTS.get(col - 1));
																writeRow.createCell(ROOM).setCellValue(ROOMS.get(row));
																writeRow.createCell(COURSE).setCellValue(COURSES.get(name).getFullName());
																section[i] = section[i].replace("Tutorial", "");
																writeRow.createCell(SECTION)
																		.setCellValue(section[i].trim().toUpperCase());
																System.out.println(section[i].trim());
																String[] uncapitalize = teacher[i].trim().split(" ");
																String capitalize = "";
																for (String s : uncapitalize) {
																	capitalize += (s.length() == 0) ? s
																			: s.substring(0, 1).toUpperCase() + s.substring(1) + " ";
																}
																capitalize = capitalize.trim();
																if (TEACHERS.containsKey(capitalize)) {
																	writeRow.createCell(TEACHER).setCellValue(TEACHERS
																			.get(capitalize).getName());
																	System.out.println(TEACHERS.get(capitalize)
																			.getName());
																}
																else{
																	writeRow.createCell(TEACHER)
																			.setCellValue(capitalize);
																	System.out.println(capitalize);
																}
																writeRowCount++;
															}
														} else {
															String section = "";
															if (courseSec[1].trim().charAt(0) == '-') {
																if (courseSec[1].trim().contains("("))
																	section = courseSec[1].trim().substring(1,
																			courseSec[1].trim().indexOf('('));
																else
																	section = courseSec[1].trim().substring(1);
															} else {
																if (courseSec[1].trim().contains("("))
																	section = courseSec[1].trim().substring(0,
																			courseSec[1].trim().indexOf('('));
																else
																	section = courseSec[1].trim();
															}
															Row writeRow = writeSheet.createRow(writeRowCount);
															writeRow.createCell(DAY)
																	.setCellValue(currentReadSheet.getSheetName());
															writeRow.createCell(TIMESLOT)
																	.setCellValue(TIME_SLOTS.get(col - 1));
															writeRow.createCell(ROOM).setCellValue(ROOMS.get(row));
															writeRow.createCell(COURSE).setCellValue(COURSES.get(name).getFullName());
															section = section.replace("Tutorial", "");
															writeRow.createCell(SECTION).setCellValue(section.trim().toUpperCase());
															System.out.println(section.trim());
															
															String[] uncapitalize = courseSecTeacher[1].trim().split(" ");
															String capitalize = "";
															for (String s : uncapitalize) {
																capitalize += (s.length() == 0) ? s
																		: s.substring(0, 1).toUpperCase() + s.substring(1) + " ";
															}
															capitalize = capitalize.trim();
															if(TEACHERS.containsKey(capitalize)){
															writeRow.createCell(TEACHER)
																	.setCellValue(TEACHERS.get(capitalize).getName());
															System.out.println(TEACHERS.get(capitalize).getName());
															}
															else{
																writeRow.createCell(TEACHER)
																		.setCellValue(capitalize);
																System.out.println(capitalize);
															}
														}
														break;
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
				}
			}
			writeRowCount+=2;
			Row writeRow = writeSheet.createRow(writeRowCount);
			for(int i = 0 ; i < 6 ; i++){
				writeRow.createCell(i).setCellValue("END");
			}
			readWorkbook.close();
			if(prop.containsKey(INTERMEDIATE_TIMETABLE)){
			String file = prop.getProperty(INTERMEDIATE_TIMETABLE);
			FileOutputStream outputStream = new FileOutputStream(file);
			writeWorkBook.write(outputStream);
			}
			else{
				Logger.getGlobal().log(Level.SEVERE, "No output location define");
			}
			writeWorkBook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void createCourseNameMap(List<Course> list) {

		List<String> exCourses = null;
		boolean isExCourse;
		if (prop.containsKey("Exclude_Courses")) {
			exCourses = Arrays.asList(prop.getProperty("Exclude_Courses").split(","));
		}
		for (Course course : list) {
			isExCourse = false;
			if (exCourses != null) {
				for (String str : exCourses) {
					if (course.getShortName().matches(str)) {
						EX_COURSES.put(str, course);
						isExCourse = true;
						String propKey = course.getFullName().replaceAll("\\.", " ").replaceAll("\\s+", "_");
						if (prop.containsKey(propKey)) {
							for (String value : prop.getProperty(propKey).split(",")) {
								EX_COURSES.put(value.trim(), course);
							}
						}
						break;
					}
				}
			}
			if (!isExCourse) {
				String propKey = course.getFullName().replaceAll("\\.", " ").replaceAll("\\s+", "_");
				if (prop.containsKey(propKey)) {
					for (String value : prop.getProperty(propKey).split(",")) {
						if (course.getShortName().matches("([\\w]+-Lab[\\w]*)")) {
							LAB_COURSES.put(value.trim(), course);
						} else {
							COURSES.put(value.trim(), course);
						}
					}
				}
				if (course.getShortName().matches("([\\w]+-Lab[\\w]*)")) {
					LAB_COURSES.put(course.getShortName(), course);
				} else {
					COURSES.put(course.getShortName(), course);
				}

			}
		}
	}

	private void createTeacherNameMap(List<Teacher> list) {

		for (Teacher teacher : list) {
			String propKey = teacher.getName().replaceAll("\\.", " ").replaceAll("\\s+", "_");
			if (prop.containsKey(propKey)) {
				for (String value : prop.getProperty(propKey).split(",")) {
					TEACHERS.put(value.trim(), teacher);
				}
			} else {
				TEACHERS.put(teacher.getName(), teacher);
			}
		}
	}


	public static boolean isLabHeadingRow(Row row) {
		if (row.getCell(0).getCellTypeEnum() == CellType.STRING && row.getCell(0).getStringCellValue().equals("LABS")
				&& row.getCell(2).getCellTypeEnum() == CellType.BLANK
				&& row.getCell(3).getCellTypeEnum() == CellType.BLANK
				&& row.getCell(4).getCellTypeEnum() == CellType.BLANK
				&& row.getCell(5).getCellTypeEnum() == CellType.BLANK
				&& row.getCell(6).getCellTypeEnum() == CellType.BLANK
				&& row.getCell(7).getCellTypeEnum() == CellType.BLANK
				&& row.getCell(8).getCellTypeEnum() == CellType.BLANK)
			return true;
		return false;
	}

	private void extractRooms(Sheet sheet) {
		System.out.println("======== ROOMS ==========");
		if (ROOMS.isEmpty()) {
			for (int index = START_ROW; index < END_ROW; index++) {
				Row row = sheet.getRow(index);
				Cell cell = row.getCell(0);
				if (cell != null && cell.getCellTypeEnum().equals(CellType.STRING)
						&& !cell.getStringCellValue().isEmpty()) {
					System.out.println(cell.getStringCellValue().trim());
					ROOMS.put(index, cell.getStringCellValue().trim());
				}
			}
		} 
	}

	private void extractTimeSlots(Sheet sheet) {
		System.out.println("======== TIMESLOTS ==========");
		if (TIME_SLOTS.isEmpty()) {
			Row row = sheet.getRow(2);
			for (int index = START_COLUMN; index <= END_COLUMN; index++) {
				Cell cell = row.getCell(index);
				if (cell != null) {
					System.out.println(cell.getStringCellValue().trim());
					TIME_SLOTS.add(cell.getStringCellValue().trim());
				}
			}
		}
	}

	public File getFileFromResource(String name) {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource(name).getFile());
			return file;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return null;
		}
	}
}
