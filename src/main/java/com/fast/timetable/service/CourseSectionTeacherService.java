package com.fast.timetable.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fast.timetable.entity.AddInstructor;
import com.fast.timetable.entity.CSTStudent;
import com.fast.timetable.entity.CancelMakeupClass;
import com.fast.timetable.entity.CourseSectionTeacher;
import com.fast.timetable.entity.Seating;
import com.fast.timetable.entity.Teacher;
import com.fast.timetable.pojo.AddInstructorPojo;
import com.fast.timetable.pojo.CancelPojo;
import com.fast.timetable.pojo.MakeupPojo;
import com.fast.timetable.repository.AddInstructorRepository;
import com.fast.timetable.repository.CSTStudentRepository;
import com.fast.timetable.repository.CancelMakeupClassRepository;
import com.fast.timetable.repository.CourseSectionTeacherRepository;
import com.fast.timetable.repository.SeatingRepository;
import com.fast.timetable.repository.TeacherRepository;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

@Service
public class CourseSectionTeacherService {

	protected static final String PROP_FILE = "config.properties";
	private static final String KEY_CLEAN_FILE = "SEATING_";
	private static final String SEATING_FILE_COUNT = "SEATING_FILE_COUNT";
	private static final int SHEET_INDEX = 0;
	private static final String COLUMN_HEADING = "FAST-NUCES";
	private static final int COLUMN_ZERO = 0;
	private static final int ROLL_COLUMN = 3;
	private static final int TIME_STU_NAME_COLUMN = 4;
	private static final int DATE_COLUMN = 5;
	private static final int TEACHER_NAME = 2;
	private static final int COURSE_NAMES = 3;

	protected String fileName;// "BSCS-Spring-2017-Timetable-V5.xlsx";
	protected Properties prop = new Properties();

	@Autowired
	CourseSectionTeacherRepository courseSectionTeacherRepository;

	@Autowired
	CSTStudentRepository cstStudentRepository;

	@Autowired
	CancelMakeupClassRepository cancelMakeupClassRepository;

	@Autowired
	AddInstructorRepository addInstructorRepository;

	@Autowired
	TeacherRepository teacherRepository;

	@Autowired
	SeatingRepository seatingRepository;

	public List<HashMap<String, String>> getCSTByCourseId(Long courseId) {
		List<HashMap<String, String>> list = new ArrayList<>();
		for (CourseSectionTeacher cst : courseSectionTeacherRepository.findByCourseId(courseId)) {
			HashMap<String, String> map = new HashMap<>();
			map.put("id", Long.toString(cst.getId()));
			map.put("courseCode", cst.getCourse().getCode());
			map.put("fullname", cst.getCourse().getFullName());
			map.put("shortname", cst.getCourse().getShortName());
			map.put("teacher", cst.getTeacher().getName());
			map.put("section", cst.getSection().getName());
			list.add(map);
		}
		return list;
	}

	public List<CourseSectionTeacher> getCSTByTeacherId(Long teacherId) {
		return courseSectionTeacherRepository.findByTeacherId(teacherId);
	}

	public List<CourseSectionTeacher> getAllCST() {
		List<CourseSectionTeacher> list = new ArrayList<>();
		// List<HashMap<String, String>> list = new ArrayList<>();
		for (CourseSectionTeacher cst : courseSectionTeacherRepository.findAll()) {
			// HashMap<String, String> map = new HashMap<>();
			// map.put("id", Long.toString(cst.getId()));
			// map.put("courseCode", cst.getCourse().getCode());
			// map.put("fullname", cst.getCourse().getFullName());
			// map.put("shortname",cst.getCourse().getShortName());
			// map.put("teacher", cst.getTeacher().getName());
			// map.put("section", cst.getSection().getName());
			// list.add(map);
			list.add(cst);
		}
		return list;

	}

	public void cancelClass(CancelPojo cancelPojo) throws Exception {
		try {
			if (cancelPojo != null) {
				CancelMakeupClass entity = new CancelMakeupClass();
				entity.setAction(CancelMakeupClass.CANCEL);
				entity.setCourseSectionTeacher(courseSectionTeacherRepository.findOne(cancelPojo.getCst()));
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date date = df.parse(cancelPojo.getDate());
				entity.setDate(date);
				entity.setTime(cancelPojo.getTimeslot());
				if (cancelMakeupClassRepository.save(entity) == null) {
					throw new Exception("Error in saving class cancellation");
				}
			}
		} catch (Exception e) {
			throw new Exception("Error in saving class cancellation");
		}
	}

	public void makeupClass(MakeupPojo makeupPojo) throws Exception {
		try {
			if (makeupPojo != null) {
				CancelMakeupClass entity = new CancelMakeupClass();
				entity.setAction(CancelMakeupClass.MAKE_UP);
				entity.setCourseSectionTeacher(courseSectionTeacherRepository.findOne(makeupPojo.getCst()));
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date date = df.parse(makeupPojo.getDate());
				entity.setDate(date);
				entity.setRoom(makeupPojo.getRoom());
				entity.setTime(makeupPojo.getTimeslot());
				if (cancelMakeupClassRepository.save(entity) == null) {
					throw new Exception("Error in scheduling a makeup class.");
				}
			}
		} catch (Exception e) {
			throw new Exception("Error in scheduling a makeup class.");
		}
	}

	public void addInstructorGuest(AddInstructorPojo addInstructorPojo, Teacher invitedBy) throws Exception {
		try {
			if (addInstructorPojo != null) {
				AddInstructor entity = new AddInstructor();
				entity.setInvitedBy(invitedBy);
				if (addInstructorPojo.getCst() != null) {
					entity.setCourseSectionTeacher(courseSectionTeacherRepository.findOne(addInstructorPojo.getCst()));
				}

				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date date = df.parse(addInstructorPojo.getDate());
				entity.setDate(date);
				if (addInstructorPojo.getTeacher() != null && addInstructorPojo.getTeacher() >= 0) {
					Teacher teacher = teacherRepository.findOne(addInstructorPojo.getTeacher());
					if (teacher != null) {
						entity.setTeacher(teacher);
						entity.setStatus(AddInstructor.STATUS_PENDING);
					}
				} else {
					entity.setStatus(AddInstructor.STATUS_ACCEPT);
				}
				if (addInstructorPojo.getGuest() != null && addInstructorPojo.getGuest().length() > 0) {
					entity.setGuest(addInstructorPojo.getGuest());
				}
				entity.setRoom(addInstructorPojo.getRoom());
				entity.setTime(addInstructorPojo.getTimeslot());
				if (addInstructorRepository.save(entity) == null) {
					throw new Exception("Error in adding instructor/guest.");
				}
			}
		} catch (Exception e) {
			throw new Exception("Error in adding instructor/guest.");
		}
	}

	private void loadData(Sheet sheet, Properties prop) {

		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row currentRow = rowIterator.next();
			String courseCode = "";
			String teacher = "";
			String room = "";
			String time = "";
			String date = "";
			String roll = "";
			String name = "";

			Cell courseCell = currentRow.getCell(COLUMN_ZERO);
			if (courseCell != null && courseCell.getCellTypeEnum().equals(CellType.STRING)
					&& courseCell.getStringCellValue().contains(COLUMN_HEADING)) {
				currentRow = rowIterator.next();
				courseCell = currentRow.getCell(COLUMN_ZERO);
			}

			if (courseCell != null && courseCell.getCellTypeEnum().equals(CellType.STRING)) {
				courseCode = courseCell.getStringCellValue().trim();
			}
			Cell teacherCell = currentRow.getCell(TEACHER_NAME);
			if (teacherCell != null && teacherCell.getCellTypeEnum().equals(CellType.STRING)) {
				teacher = teacherCell.getStringCellValue().trim();
			}

			currentRow = rowIterator.next();
			Cell roomCell = currentRow.getCell(COLUMN_ZERO);
			if (roomCell != null && roomCell.getCellTypeEnum().equals(CellType.STRING)) {
				room = roomCell.getStringCellValue().trim();
			}
			Cell timeCell = currentRow.getCell(TIME_STU_NAME_COLUMN);
			if (timeCell != null && timeCell.getCellTypeEnum().equals(CellType.STRING)) {
				time = timeCell.getStringCellValue().trim();
			}
			Cell dateCell = currentRow.getCell(DATE_COLUMN);
			if (dateCell != null && dateCell.getCellTypeEnum().equals(CellType.NUMERIC)) {
				date = Double.toString(dateCell.getNumericCellValue());
			}

			while (rowIterator.hasNext()) {

				currentRow = rowIterator.next();

				Cell cell = currentRow.getCell(ROLL_COLUMN);
				if (cell != null && cell.getCellTypeEnum().equals(CellType.STRING)
						&& !cell.getStringCellValue().isEmpty()) {
					roll = cell.getStringCellValue().trim();
				} else {
					break;
				}
				cell = currentRow.getCell(TIME_STU_NAME_COLUMN);
				if (cell != null && cell.getCellTypeEnum().equals(CellType.STRING)) {
					name = cell.getStringCellValue().trim();
				}

				System.out
						.println(room + ", " + time + ", " + date + ", " + teacher + ", " + name + ", " + roll + ", ");

				CSTStudent cstStudent = cstStudentRepository.findUsingStudentCourseTeacher(courseCode, roll);

				if (cstStudent != null) {
					System.out.println("Object:" + cstStudent.getStudent().getFullName());
					Seating seating = new Seating();
					seating.setCourseSectionTeacherStudent(cstStudent);
					seating.setDate(date);
					seating.setRoom(room);
					seating.setTime(time);
					try {
						saveIfUnique(seating);
					} catch (Exception e) {
						Logger.getGlobal().log(Level.SEVERE, "Duplicate entry " + cstStudent.getId());
					}
				}
			}
		}
	}

	private void saveIfUnique(Seating seating) throws Exception {
		seatingRepository.save(seating);
	}

	public String populateSeating() {
		loadFile();
		return "success";
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
		int totalFiles = Integer.parseInt((String) prop.get(SEATING_FILE_COUNT));
		for (int i = 1; i <= totalFiles; i++) {
			executeDataLoading(i);
		}
	}

	private void executeDataLoading(int fileNumber) {
		loadExcelFile(getFileName() + fileNumber);
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
		return KEY_CLEAN_FILE;
	}

	private int getSheetIndex() {
		return SHEET_INDEX;
	}
}
