package com.fast.timetable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import com.fast.timetable.entity.Section;

public class SectionDataManager extends DataManager<Section> {

	private static final String DATA_FILE = "INTERMEDIATE_TIMETABLE_FILE";
	private static final int SHEET_INDEX = 0;
	private static final int SECTION_COLUMN = 4;
	private static final int START_ROW = 2;
	public Map<String, Section> sections;


	protected String getFileName() {
		return DATA_FILE;
	}

	
	@Override
	protected int getSheetIndex() {
		return SHEET_INDEX;
	}

	@Override
	protected void loadData(Sheet sheet, Properties prop) {
		sections = new HashMap<>();
		for (int row = START_ROW; true; row++) {
			Row currentRow = sheet.getRow(row);
			if (currentRow != null) {
				Cell cell = currentRow.getCell(SECTION_COLUMN);
				if (cell != null && cell.getCellTypeEnum().equals(CellType.STRING)
						&& !cell.getStringCellValue().isEmpty()) {
					String value = cell.getStringCellValue().trim();
					if (value.equals("END")) {
						break;
					} else {
						System.out.println("Row:" + (row - 2) + "Section:" + value);
						if (!sections.containsKey(value)) {
							Section section = new Section();
							section.setName(value);
							sections.put(value, section);
						}
					}
				}
			}
		}
	}

	@Override
	public List<Section> getData() {
		return new ArrayList<Section>(sections.values());
	}
}
