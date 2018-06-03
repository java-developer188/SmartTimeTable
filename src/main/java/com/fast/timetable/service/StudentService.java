package com.fast.timetable.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fast.timetable.entity.CSTStudent;
import com.fast.timetable.entity.Course;
import com.fast.timetable.entity.CourseSectionTeacher;
import com.fast.timetable.entity.Login;
import com.fast.timetable.entity.Section;
import com.fast.timetable.entity.Student;
import com.fast.timetable.entity.Teacher;
import com.fast.timetable.repository.CSTStudentRepository;
import com.fast.timetable.repository.CourseRepository;
import com.fast.timetable.repository.CourseSectionTeacherRepository;
import com.fast.timetable.repository.LoginRepository;
import com.fast.timetable.repository.RoomLocationRepository;
import com.fast.timetable.repository.SectionRepository;
import com.fast.timetable.repository.StudentRepository;
import com.fast.timetable.repository.TeacherRepository;
import com.fast.timetable.repository.TimeTableRepository;

@Service
public class StudentService {

	protected static final String PROP_FILE = "config.properties";
	private static final String REGISTERED_STUDENTS = "REGISTRATION_DATASET";
	private static final int SHEET_INDEX = 0;
	private static final String COLUMN_HEADING = "Roll";

	private static final int ROLL = 1;
	private static final int NAME = 2;
	private static final int CODE = 3;
	private static final int SECTION = 6;
	private static final int ST_SECTION = 11;

	protected String fileName;// "BSCS-Spring-2017-Timetable-V5.xlsx";
	protected Properties prop = new Properties();

	@Autowired
	LoginRepository loginRepository;

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	CSTStudentRepository cstStudentRepository;

	@Autowired
	TimeTableRepository timeTableRepository;

	@Autowired
	CourseSectionTeacherRepository courseSectionTeacherRepository;
	
	@Autowired
	RoomLocationRepository roomLocationRepository;

	public Student login(String username, String password) {
		String encpwd = encryptDecrypt(password);
		Student student = loginRepository.findStudentByUsernameAndPassword(username, encpwd);
		return student;
	}

	private String encryptDecrypt(String input) {
		char[] key = { 's', 'm', 'a', 'r', 't' }; // Can be any chars, and any
													// length array
		StringBuilder output = new StringBuilder();

		for (int i = 0; i < input.length(); i++) {
			output.append((char) (input.charAt(i) ^ key[i % key.length]));
		}

		return output.toString();
	}

	public List<HashMap<String, String>> getStudentTimeTable(Long id) {
		List<HashMap<String, String>> list = new ArrayList<>();
		for (Object[] obj : timeTableRepository.getTimetableByStudentId(id)) {
			HashMap<String, String> map = new HashMap<>();
			map.put("day", obj[0].toString());
			map.put("time", obj[1].toString());
			map.put("room", obj[2].toString());
			map.put("course", obj[3].toString());
			map.put("section", obj[4].toString());
			map.put("teacher", obj[5].toString());
			map.put("location", roomLocationRepository.getLocationByRoom(obj[2].toString()));
			list.add(map);
		}
		return list;
	}

	public Student getStudentByUsername(String username) {
		return loginRepository.findStudentByUsername(username);
	}

	public Student getStudentById(Long id) {
		return studentRepository.findOne(id);

	}

	public boolean changePassword(String username, String newPassword) {
		Login login = loginRepository.findByUsername(username);
		if (login != null) {
			String encpwd = encryptDecrypt(newPassword);
			login.setPassword(encpwd);
			loginRepository.save(login);
			return true;
		}
		return false;
	}

	public void populateStudent() {
		loadFile();

	}

	private void loadData(Sheet sheet, Properties prop) {
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			String roll = null;
			String courseCode = null;
			String sectionString = null;
			Row currentRow = rowIterator.next();
			Cell cell = currentRow.getCell(ROLL); // taking decision
													// on only first
			try { // cell for now
				if (cell != null && cell.getCellTypeEnum().equals(CellType.STRING)) {
					String value = cell.getStringCellValue();
					if (!value.contains(COLUMN_HEADING) && currentRow.getCell(ROLL) != null
							&& !currentRow.getCell(ROLL).getStringCellValue().isEmpty()
							&& currentRow.getCell(CODE) != null
							&& !currentRow.getCell(CODE).getStringCellValue().isEmpty()
							&& currentRow.getCell(SECTION) != null
							&& !currentRow.getCell(SECTION).getStringCellValue().isEmpty()) {
						Student student;

						roll = currentRow.getCell(ROLL).getStringCellValue().trim();
						student = studentRepository.findByRollNumber(roll);

						courseCode = currentRow.getCell(CODE).getStringCellValue();

						sectionString = currentRow.getCell(SECTION).getStringCellValue();

						CourseSectionTeacher courseSectionTeacher = courseSectionTeacherRepository
								.findByCourseSection(courseCode, sectionString);

						if (courseSectionTeacher != null) {

							if (student == null) {
								student = new Student();
								student.setRollNumber(roll);
								student.setFullName(currentRow.getCell(NAME) != null
										? currentRow.getCell(NAME).getStringCellValue().trim() : null);
								student.setSection(currentRow.getCell(ST_SECTION) != null
										? currentRow.getCell(ST_SECTION).getStringCellValue().trim() : null);
								student = studentRepository.save(student);

								Login login = new Login();
								login.setUsername(student.getRollNumber());
								login.setPassword(encryptDecrypt(student.getRollNumber()));
								login.setStudent(student);
								login = loginRepository.save(login);
							}

							CSTStudent cstStudent = new CSTStudent();
							cstStudent.setCourseSectionTeacher(courseSectionTeacher);
							cstStudent.setStudent(student);
							cstStudentRepository.save(cstStudent);

						}
					}
				}
			} catch (Exception ex) {
				Logger.getGlobal().log(Level.SEVERE,
						"Error in saving : Roll:" + roll + " Code: " + courseCode + " Section: " + sectionString);
			}
		}
	}

	private void loadFile() {
		try {
			InputStream input = getClass().getClassLoader().getResourceAsStream(PROP_FILE);
			if (input == null) {
				System.out.println("Sorry, unable to find " + PROP_FILE);
				return;
			}

			prop.load(input);
			executeDataLoading();

		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Error in loading file : " + PROP_FILE);
		}
	}

	private void executeDataLoading() {
		loadExcelFile(getFileName());
	}

	private void loadExcelFile(String fileNameProperty) {
		fileName = prop.getProperty(fileNameProperty);
		try (Workbook excelWorkbook = new XSSFWorkbook(new FileInputStream(new File(fileName)))) {
			loadData(readSheet(excelWorkbook, getSheetIndex()), prop);
		} catch (FileNotFoundException ex) {
			Logger.getGlobal().log(Level.SEVERE, "File not found: " + PROP_FILE);
		} catch (IOException ex) {
			Logger.getGlobal().log(Level.SEVERE, "Error in loading file : " + PROP_FILE);
		}
	}

	private Sheet readSheet(Workbook workbook, int sheetIndex) {
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		if (sheet != null) {
			Logger.getGlobal().log(Level.INFO, "Sheet Loaded successfully: " + sheet.getSheetName());
		} else {
			Logger.getGlobal().log(Level.SEVERE, "Sheet not found at index: " + sheetIndex);
		}
		return sheet;
	}

	private String getFileName() {
		return REGISTERED_STUDENTS;
	}

	private int getSheetIndex() {
		return SHEET_INDEX;
	}

}
