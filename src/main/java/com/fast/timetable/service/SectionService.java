package com.fast.timetable.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

import com.fast.timetable.entity.Section;
import com.fast.timetable.repository.SectionRepository;

@Service
public class SectionService {

	protected static final String PROP_FILE = "config.properties";
	private static final String DATA_FILE = "INTERMEDIATE_TIMETABLE_FILE";
	private static final int SHEET_INDEX = 0;
	private static final int SECTION_COLUMN = 4;
	private static final int START_ROW = 2;

	protected String fileName;// "BSCS-Spring-2017-Timetable-V5.xlsx";
	protected Properties prop = new Properties();

	@Autowired
	SectionRepository sectionRepository;

	public void save(List<Section> list) {
		sectionRepository.save(list);
	}

	public List<Section> getAll() {
		List<Section> targetCollection = new ArrayList<Section>();
		CollectionUtils.addAll(targetCollection, sectionRepository.findAll().iterator());
		return targetCollection;
	}

	public void save() {
		loadFile();
	}

	private String getFileName() {
		return DATA_FILE;
	}

	private int getSheetIndex() {
		return SHEET_INDEX;
	}

	private void loadData(Sheet sheet, Properties prop) {
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
						Section section = sectionRepository.findByName(value);
						if (section == null) {
							section = new Section();
							section.setName(value);
							sectionRepository.save(section);
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
}
