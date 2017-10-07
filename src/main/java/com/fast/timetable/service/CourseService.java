package com.fast.timetable.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fast.timetable.entity.Course;
import com.fast.timetable.repository.CourseRepository;
import com.fast.timetable.repository.CourseSectionTeacherRepository;
import com.fast.timetable.repository.TimeTableRepository;

@Service
public class CourseService {
	protected static final String PROP_FILE = "config.properties";
	private static final String KEY_CLEAN_FILE = "CLEAN_DATA_FILENAME";
	private static final int SHEET_INDEX = 0;
	private static final String COLUMN_HEADING = "Column";

	private static final int COURSE_CODE = 2;
	private static final int FULLNAME = 3;
	private static final int SHORTNAME = 4;
	private static final int NUMBER_OF_SECTION = 5;
	private static final int CREDIT_HOURS = 6;
	private static final int BATCH = 7;
	
	protected String fileName;// "BSCS-Spring-2017-Timetable-V5.xlsx";
	protected Properties prop = new Properties();

	
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	TimeTableRepository timeTableRepository;
	
	@Autowired
	CourseSectionTeacherRepository courseSectionTeacherRepository;
	
	
	public void save(List<Course> list){
			courseRepository.save(list);
	}
	
	public void save(){
		loadFile();
	}

	public List<Course> getAll(){
		List<Course> targetCollection = new ArrayList<Course>();
		CollectionUtils.addAll(targetCollection, courseRepository.findAll().iterator());
		return targetCollection;
	}
	
	private void loadData(Sheet sheet, Properties prop) {
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row currentRow = rowIterator.next();
			Cell cell = currentRow.getCell(COURSE_CODE); // taking decision
															// on only first
															// cell for now
			if (cell != null && cell.getCellTypeEnum().equals(CellType.STRING)) {
				String value = cell.getStringCellValue();
				if (!value.contains(COLUMN_HEADING)) {

					Course course = new Course();
					course.setCode(currentRow.getCell(COURSE_CODE) != null
							? currentRow.getCell(COURSE_CODE).getStringCellValue().trim() : null);
					course.setFullName(currentRow.getCell(FULLNAME) != null
							? currentRow.getCell(FULLNAME).getStringCellValue().trim() : null);
					course.setShortName(currentRow.getCell(SHORTNAME) != null
							? currentRow.getCell(SHORTNAME).getStringCellValue().trim() : null);
					course.setNumberOfSections(currentRow.getCell(NUMBER_OF_SECTION) != null
							&& cell.getCellTypeEnum().equals(CellType.NUMERIC)
									? (int) currentRow.getCell(NUMBER_OF_SECTION).getNumericCellValue() : 0);
					course.setCreditHours(currentRow.getCell(CREDIT_HOURS) != null
							? (int) currentRow.getCell(CREDIT_HOURS).getNumericCellValue() : 0);
					if (currentRow.getCell(BATCH) != null
							&& !currentRow.getCell(BATCH).getCellTypeEnum().equals(CellType.STRING)) {
						course.setBatch((int) currentRow.getCell(BATCH).getNumericCellValue());
					}
					if (course.getFullName().contains("Lab") || course.getFullName().contains("lab")
							|| course.getShortName().contains("Lab") || course.getShortName().contains("lab")) {
						course.setLab(true);
					}
					System.out.println(course.getFullName());
			
					courseRepository.save(course);
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
		try(Workbook excelWorkbook = new XSSFWorkbook(new FileInputStream(new File(fileName)))) {
			loadData(readSheet(excelWorkbook , getSheetIndex()), prop);
		} catch (FileNotFoundException ex) {
			Logger.getGlobal().log(Level.SEVERE, "File not found: " + PROP_FILE);
		} catch (IOException ex) {
			Logger.getGlobal().log(Level.SEVERE, "Error in loading file : " + PROP_FILE);
		}
	}

	private Sheet readSheet(Workbook workbook , int sheetIndex) {
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		if(sheet != null){
		Logger.getGlobal().log(Level.INFO, "Sheet Loaded successfully: " + sheet.getSheetName());
		}
		else{
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
