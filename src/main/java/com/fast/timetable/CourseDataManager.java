package com.fast.timetable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.fast.timetable.entity.Course;

public class CourseDataManager extends DataManager<Course> {

	private static final String KEY_CLEAN_FILE = "CLEAN_DATA_FILENAME";
	private static final int SHEET_INDEX = 0;
	private static final String COLUMN_HEADING = "Column2";

	private static final int COURSE_CODE = 2;
	private static final int FULLNAME = 3;
	private static final int SHORTNAME = 4;
	private static final int NUMBER_OF_SECTION = 5;
	private static final int CREDIT_HOURS = 6;
	private static final int BATCH = 7;

	private List<Course> courses;


	@Override
	protected void loadData(Sheet sheet, Properties prop) {
		Iterator<Row> rowIterator = sheet.iterator();
		courses = new ArrayList<>();
		while (rowIterator.hasNext()) {
			Row currentRow = rowIterator.next();
			Cell cell = currentRow.getCell(COURSE_CODE); // taking decision
															// on only first
															// cell for now
			if (cell != null && cell.getCellTypeEnum().equals(CellType.STRING)) {
				String value = cell.getStringCellValue();
				if (!value.equals(COLUMN_HEADING)) {

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
			
					courses.add(course);
				}
			}
		}
	}

	@Override
	protected String getFileName() {
		return KEY_CLEAN_FILE;
	}

	@Override
	protected int getSheetIndex() {
		return SHEET_INDEX;
	}

	@Override
	public List<Course> getData() {
		return courses;
	}

}