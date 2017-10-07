package com.fast.timetable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.fast.timetable.entity.Teacher;

public class TeacherDataManager extends DataManager<Teacher> {

	private static final String KEY_CLEAN_FILE = "CLEAN_DATA_FILENAME";
	private static final int SHEET_INDEX = 0;
	private static final String COLUMN_HEADING = "Column8";
	private static final int TEACHER_NAMES = 8;

	private Map<String, Teacher> teachers ;


	@Override
	protected void loadData(Sheet sheet, Properties prop) {

		Iterator<Row> rowIterator = sheet.iterator();
		teachers = new HashMap<String, Teacher>();
		while (rowIterator.hasNext()) {
			Row currentRow = rowIterator.next();
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

						// System.out.println(capitalize);
						if (!teachers.containsKey(capitalize)) {
								Teacher teacher = new Teacher();
								teacher.setName(capitalize);
								teachers.put(capitalize, teacher);
								System.out.println(capitalize + "---> : " + capitalize);
							}
						}
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
	public List<Teacher> getData() {
		return new ArrayList<Teacher>(teachers.values());
	}

}