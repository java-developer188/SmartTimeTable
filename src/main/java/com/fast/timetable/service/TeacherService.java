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
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fast.timetable.DataManager;
import com.fast.timetable.TeacherDataManager;
import com.fast.timetable.entity.Course;
import com.fast.timetable.entity.CourseTeacher;
import com.fast.timetable.entity.Login;
import com.fast.timetable.entity.Student;
import com.fast.timetable.entity.Teacher;
import com.fast.timetable.repository.CourseRepository;
import com.fast.timetable.repository.CourseSectionTeacherRepository;
import com.fast.timetable.repository.CourseTeacherRepository;
import com.fast.timetable.repository.LoginRepository;
import com.fast.timetable.repository.RoomLocationRepository;
import com.fast.timetable.repository.TeacherRepository;
import com.fast.timetable.repository.TimeTableRepository;
import com.fast.timetable.utilities.EncryptorDecryptor;

@Service
public class TeacherService {

	protected static final String PROP_FILE = "config.properties";
	private static final String KEY_CLEAN_FILE = "CLEAN_DATA_FILENAME";
	private static final int SHEET_INDEX = 0;
	private static final String COLUMN_HEADING = "Column8";
	private static final int TEACHER_NAMES = 8;
	private static final int COURSE_NAMES = 3;

	protected String fileName;// "BSCS-Spring-2017-Timetable-V5.xlsx";
	protected Properties prop = new Properties();

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	CourseTeacherRepository courseTeacherRepository;

	@Autowired
	TeacherRepository teacherRepository;

	@Autowired
	LoginRepository loginRepository;

	@Autowired
	TimeTableRepository timeTableRepository;

	@Autowired
	CourseSectionTeacherRepository courseSectionTeacherRepository;

	@Autowired
	RoomLocationRepository roomLocationRepository;

	public void save(List<Teacher> list) {
		teacherRepository.save(list);
	}

	public void save() {
		loadFile();
	}

	public List<Teacher> getAll() {
		List<Teacher> targetCollection = new ArrayList<Teacher>();
		CollectionUtils.addAll(targetCollection, teacherRepository.findAll().iterator());
		return targetCollection;
	}

	public List<HashMap<String, String>> getTeacherTimeTable(Long id) {
		List<HashMap<String, String>> list = new ArrayList<>();
		for (Object[] obj : timeTableRepository.getTimetableByTeacherId(id)) {
			HashMap<String, String> map = new HashMap<>();
			map.put("day", obj[0].toString());
			map.put("time", obj[1].toString());
			map.put("room", obj[2].toString());
			map.put("course", obj[3].toString());
			map.put("section", obj[4].toString());
			map.put("location", roomLocationRepository.getLocationByRoom(obj[2].toString()));
			list.add(map);
		}
		return list;
	}

	private void loadData(Sheet sheet, Properties prop) {

		Iterator<Row> rowIterator = sheet.iterator();

		while (rowIterator.hasNext()) {
			Row currentRow = rowIterator.next();
			Cell courseCell = currentRow.getCell(COURSE_NAMES);
			String courseName = "";
			if (courseCell != null && courseCell.getCellTypeEnum().equals(CellType.STRING)) {
				courseName = courseCell.getStringCellValue();
			}
			Cell cell = currentRow.getCell(TEACHER_NAMES); // taking
															// decision
															// on only first
															// cell for now
			if (cell != null && cell.getCellTypeEnum().equals(CellType.STRING)) {
				String value = cell.getStringCellValue();
				if (!value.equals(COLUMN_HEADING)) {
					for (String name : value.split(",")) {

						String[] uncapitalize = name.trim().split(" ");
						String capitalize = "";
						for (String s : uncapitalize) {
							capitalize += (s.length() == 0) ? s
									: s.substring(0, 1).toUpperCase() + s.substring(1) + " ";
						}
						capitalize = capitalize.trim();

						System.out.println(capitalize);
						Teacher teacher = teacherRepository.findByName(capitalize);
						if (teacher == null) {
							teacher = new Teacher();
							teacher.setName(capitalize);
							teacher = teacherRepository.save(teacher);
							Login login = createLogin(teacher);
							if (login != null) {
								System.out.println("Login account created with username: " + login.getUsername());
								Logger.getGlobal().log(Level.FINE,
										"Login account created with username: " + login.getUsername());
							}
						}
						Course course = courseRepository.findByFullName(courseName);
						if (course != null) {
							CourseTeacher courseTeacher = new CourseTeacher();
							courseTeacher.setTeacher(teacher);
							courseTeacher.setCourse(course);
							courseTeacherRepository.save(courseTeacher);
						}

					}
				}
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

		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Error in loading file : " + PROP_FILE);
		}
		executeDataLoading();
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

	public Teacher login(String username, String password) {
		String encpwd = EncryptorDecryptor.encryptDecrypt(password);
		Teacher teacher = loginRepository.findTeacherByUsernameAndPassword(username, encpwd);
		return teacher;
	}
	
	public boolean changePassword(String username, String newPassword) {
		Login login = loginRepository.findByUsername(username);
		if (login != null) {
			String encpwd = EncryptorDecryptor.encryptDecrypt(newPassword);
			login.setPassword(encpwd);
			loginRepository.save(login);
			return true;
		}
		return false;
	}

	private Login createLogin(Teacher teacher) {
		Login login = new Login();
		try {
			if (teacher != null) {
				for (String username : teacher.getName().split(" ")) {
					if (username.length() > 3) {
						login.setTeacher(teacher);
						login = loginRepository.save(login);
						login.setUsername(username.toLowerCase()+login.getId());
						login.setPassword(EncryptorDecryptor.encryptDecrypt(login.getUsername()));
					}
				}

			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Error creating login fro teacher:" + teacher.getName());
		}
		return login;
	}
	
	public List<Teacher> findAllForAddInstructor(){
		List<Teacher> list = IterableUtils.toList(teacherRepository.findAll());
		Teacher nullTeacher = new Teacher();
		nullTeacher.setId(-1L);
		nullTeacher.setName("Select");
		list.add(0, nullTeacher);
		return list;
	}

	private String getFileName() {
		return KEY_CLEAN_FILE;
	}

	private int getSheetIndex() {
		return SHEET_INDEX;
	}

}
