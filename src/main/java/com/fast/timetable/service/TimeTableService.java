package com.fast.timetable.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

import com.fast.timetable.entity.Course;
import com.fast.timetable.entity.CourseSectionTeacher;
import com.fast.timetable.entity.Section;
import com.fast.timetable.entity.Teacher;
import com.fast.timetable.entity.TimeTable;
import com.fast.timetable.repository.CourseRepository;
import com.fast.timetable.repository.CourseSectionTeacherRepository;
import com.fast.timetable.repository.SectionRepository;
import com.fast.timetable.repository.TeacherRepository;
import com.fast.timetable.repository.TimeTableRepository;

@Service
public class TimeTableService {

	private static final String PROP_FILE = "config.properties";
	private static final String INTERMEDIATE_TIMETABLE = "INTERMEDIATE_TIMETABLE_FILE";

	private static final int SHEET_INDEX = 0;
	private static final int START_ROW = 2;

	private static final int DAY = 0;
	private static final int TIMESLOT = 1;
	private static final int ROOM = 2;
	private static final int COURSE = 3;
	private static final int SECTION = 4;
	private static final int TEACHER = 5;

	private String fileName;
	private Properties prop = new Properties();
	private Workbook readWorkbook;
	private Sheet currentReadSheet;

	@Autowired
	TeacherRepository teacherRepository;

	@Autowired
	SectionRepository sectionRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	TimeTableRepository timeTableRepository;

	@Autowired
	CourseSectionTeacherRepository courseSectionTeacherRepository;

	private void loadFile() {

		try {
			InputStream input = getClass().getClassLoader().getResourceAsStream(PROP_FILE);
			if (input == null) {
				System.out.println("Sorry, unable to find " + PROP_FILE);
				return;
			}

			prop.load(input);
			fileName = prop.getProperty(INTERMEDIATE_TIMETABLE);

		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "File: " + fileName + " not found!");
		}

		try {
			readWorkbook = new XSSFWorkbook(new FileInputStream(new File(fileName)));
			currentReadSheet = readWorkbook.getSheetAt(SHEET_INDEX);
		} catch (FileNotFoundException e) {
			Logger.getGlobal().log(Level.SEVERE, "File: " + fileName + " not found!");
		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Error opening file: " + fileName);
		}

	}

	public void save() {
		loadFile();
		extract();
	}

	private void extract() {
	//	try{
		int rowIndex = START_ROW;
		do {
			Row currentRow = currentReadSheet.getRow(rowIndex);
			if (currentRow != null) {
				Cell cell = currentRow.getCell(DAY);
				if (cell != null && cell.getCellTypeEnum().equals(CellType.STRING)
						&& !cell.getStringCellValue().isEmpty()) {
					String value = cell.getStringCellValue();
					if (value.matches("END"))
						break;

					TimeTable timeTable = new TimeTable();
					timeTable.setDay(value);
					timeTable.setTime(currentRow.getCell(TIMESLOT).getStringCellValue());
					timeTable.setRoom(currentRow.getCell(ROOM).getStringCellValue());
					String courseFullName = currentRow.getCell(COURSE).getStringCellValue();
					String sectionName = currentRow.getCell(SECTION).getStringCellValue();
					String teacherName = currentRow.getCell(TEACHER).getStringCellValue();
					CourseSectionTeacher cst = courseSectionTeacherRepository.findByCST(courseFullName, sectionName,
							teacherName);

					if (cst == null) {
						Course course = courseRepository.findByFullName(courseFullName);
						Section section = sectionRepository.findByName(sectionName);
						Teacher teacher = teacherRepository.findByNameAndCourse(teacherName,courseFullName);

						if (course != null && section != null && teacher != null) {
							cst = new CourseSectionTeacher();
							cst.setCourse(course);
							cst.setSection(section);
							cst.setTeacher(teacher);
							cst = courseSectionTeacherRepository.save(cst);
						}
					}

					if (cst.getId() > 0) {
						timeTable.setCourseSectionTeacher(cst);
						timeTableRepository.save(timeTable);
					}
				}
			}
			rowIndex++;
		} while (true);
//		}
//		catch(NullPointerException npe){
//			Logger.getGlobal().log(Level.SEVERE, npe.getLocalizedMessage());
//		}
//		catch(Exception e){
//			Logger.getGlobal().log(Level.SEVERE, e.getLocalizedMessage());
//		}
	}
}
