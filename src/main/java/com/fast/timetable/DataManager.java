package com.fast.timetable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class DataManager<T> {

	protected static final String PROP_FILE = "config.properties";

	protected String fileName;// "BSCS-Spring-2017-Timetable-V5.xlsx";
	protected Properties prop = new Properties();
	
	public DataManager() {
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

	protected void executeDataLoading() {
		loadExcelFile(getFileName());
	}

	protected abstract String getFileName();

	protected abstract int getSheetIndex();

	protected abstract void loadData(Sheet sheet, Properties prop );
	
	public abstract List<T> getData();

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

	protected Sheet readSheet(Workbook workbook , int sheetIndex) {
		Sheet sheet = workbook.getSheetAt(sheetIndex);
		if(sheet != null){
		Logger.getGlobal().log(Level.INFO, "Sheet Loaded successfully: " + sheet.getSheetName());
		}
		else{
			Logger.getGlobal().log(Level.SEVERE, "Sheet not found at index: " + sheetIndex);
		}
		return sheet;
	}

}
