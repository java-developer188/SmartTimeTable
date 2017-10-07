package com.fast.timetable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FetchController {

	private static final String FILE_NAME = "BSCS-Spring-2017-Timetable-V5.xlsx";
	public static final int TYPE_DAY = 1;
	public static final int TYPE_OTHER = 2;
	private static final int STARTING_ROW = 5;
	
	public static void perform() {

		try {
			FetchController fetch = new FetchController();
			FileInputStream excelFile = new FileInputStream(fetch.getFileFromResource(FILE_NAME));

			Workbook readWorkbook = new XSSFWorkbook(excelFile);

			XSSFWorkbook writeWorkBook = new XSSFWorkbook();
			XSSFSheet writeSheet = writeWorkBook.createSheet("Courses_Sheet");

			int writeRowCount=0;
			Row heading = writeSheet.createRow(writeRowCount);
			heading.createCell(0).setCellValue("Course_Category");
			heading.createCell(1).setCellValue("Course_Code");
			heading.createCell(2).setCellValue("Course_Name");
			heading.createCell(3).setCellValue("Course_Attribute_Count");
			heading.createCell(4).setCellValue("Course_Attribute_1");
			heading.createCell(5).setCellValue("Course_Attribute_1_Type");
			heading.createCell(6).setCellValue("Course_Attribute_2");
			heading.createCell(7).setCellValue("Course_Attribute_2_Type");
			heading.createCell(8).setCellValue("Course_Attribute_3");
			heading.createCell(9).setCellValue("Course_Attribute_3_Type");
			heading.createCell(10).setCellValue("Course_Short");
			heading.createCell(11).setCellValue("Course_Sections");
			heading.createCell(12).setCellValue("Course_Credit_Hours");
			heading.createCell(13).setCellValue("Course_Batch");
			heading.createCell(14).setCellValue("Course_Planning");
			heading.createCell(15).setCellValue("Course_Teachers");

			for (int sheet = 1; sheet <= 4; sheet++) {
				Sheet datatypeSheet = readWorkbook.getSheetAt(sheet);
				System.out.println("Sheet: " + datatypeSheet.getSheetName());
				if (datatypeSheet.getSheetName().contains("Batch") || datatypeSheet.getSheetName().contains("BATCH")) {
//					Batch batch = new Batch();
//					batch.setValue(Integer.parseInt(datatypeSheet.getSheetName().split(" ")[1]));
				}
				String category = "";
				int rowCount = STARTING_ROW;
				boolean hasRow = true;
				do {
					Row currentRow = datatypeSheet.getRow(rowCount);
					writeRowCount++;
					Row writeRow = writeSheet.createRow(writeRowCount);
					
					if (isLastRow(currentRow)) {
						break;
					}
					if (isCategoryRow(currentRow)) {
						// System.out.print("Category: " +
						// currentRow.getCell(1).getStringCellValue());
						category = currentRow.getCell(1).getStringCellValue();
						// System.out.println();
					} else {

						writeRow.createCell(0).setCellValue(category);
						writeRow.createCell(1).setCellValue(currentRow.getCell(2).getStringCellValue());

						extractCourseName(currentRow.getCell(3), writeRow);

						writeRow.createCell(10).setCellValue(currentRow.getCell(4).getStringCellValue());

						if(currentRow.getCell(5).getCellTypeEnum() == CellType.NUMERIC)
						writeRow.createCell(11)
								.setCellValue(currentRow.getCell(5).getNumericCellValue());
						
						writeRow.createCell(12)
								.setCellValue(currentRow.getCell(6).getNumericCellValue());

						if(currentRow.getCell(7).getCellTypeEnum() == CellType.NUMERIC)
						writeRow.createCell(13)
								.setCellValue(currentRow.getCell(7).getNumericCellValue());

						extractPlanning(currentRow.getCell(8), writeRow);

					}
					rowCount++;
				} while (hasRow);		
			}
			writeRowCount++;
			Row writeRow = writeSheet.createRow(writeRowCount);
			for(int x=0 ; x<= 15 ; x++){
				writeRow.createCell(x).setCellValue("END");
			}
			
			readWorkbook.close();
			FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Haider\\Desktop\\CleanData.xlsx");
            writeWorkBook.write(outputStream);
            writeWorkBook.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void extractPlanning(Cell currentCell, Row writeRow) {
		if (currentCell.getCellTypeEnum() == CellType.STRING) {
			String value = currentCell.getStringCellValue();

			if (value.contains("Course planning") || value.contains("Course Planning")
					|| value.contains("Course planing") || value.contains("Course Planing")) {
				String planning = "";
				String remaining = "";
				String[] arr = currentCell.getStringCellValue().split("\\s{5,}");
				if (arr[0].equals("Course planning:") || arr[0].equals("Course Planning:")
						|| arr[0].equals("Course planing:") || arr[0].equals("Course Planing:") && arr.length > 2) {
					planning = (arr[1] == null) ? "" : arr[1];
					// System.out.println("Planning:" + planning);
					for (int i = 2; i < arr.length; i++) {
						remaining = remaining + arr[i];
					}
					// System.out.println("Remaining:" + remaining);
				} else {
					if (arr.length > 1) {
						planning = arr[0].split(":")[1];
						// System.out.println("Planning:" + planning);

						for (int i = 1; i < arr.length; i++) {
							remaining = remaining + arr[i];
						}
						// System.out.println("Remaining:" + remaining);
					} else if (arr.length == 1) {
						if (arr[0].split("[\\r\\n]").length > 1) {
							String[] further = arr[0].split("[\\r\\n]");
							planning = further[0].split(":")[1];
							// System.out.println("Planning:" + planning);

							for (int i = 1; i < further.length; i++) {
								remaining = remaining + further[i];
							}
							// System.out.println("Remaining:" + remaining);
						} else {
							String rawPlanning = arr[0].split(":")[1];
							String[] disposable = rawPlanning.split("(\\))");
							for (int i = 0; i < disposable.length; i++) {
								if (disposable[i].contains("="))
									planning = planning + disposable[i] + ")";
								else
									remaining = remaining + disposable[i];
							}
							// System.out.println("Planning:" + planning);
							// System.out.println("Remaining:" + remaining);
						}
					}
				}
				writeRow.createCell(14).setCellValue(planning);
				writeRow.createCell(15).setCellValue(remaining);
			} else {
				// String[] unplanned =
				// currentCell.getStringCellValue().split("(\\),)");
				writeRow.createCell(15).setCellValue(currentCell.getStringCellValue());
			}
		}
	}

	public static void extractCourseName(Cell cell, Row writeRow) {
		if (cell.getCellTypeEnum() == CellType.STRING) {
			String temp = cell.getStringCellValue();
			System.out.println(cell.getStringCellValue());
			if (temp.split("\\s{5,}").length > 1) {
				// int courseLength = temp.split("\\s{5,}").length;
				String[] str = temp.split("\\s{5,}");
				writeRow.createCell(2).setCellValue(str[0].trim());
				writeRow.createCell(3).setCellValue(str.length - 1);
				for (int i = 1, help = 0; i <= str.length-1; i++, help++) {
					if (checkForAttrType(str[i])) {
						writeRow.createCell(i + 3 + help).setCellValue(str[i].trim());
						writeRow.createCell(i + 4 + help).setCellValue(TYPE_DAY);
					} else {
						writeRow.createCell(i + 3 + help).setCellValue(str[i].trim());
						writeRow.createCell(i + 4 + help).setCellValue(TYPE_OTHER);
					}
				}
			} else {
				writeRow.createCell(2).setCellValue(cell.getStringCellValue());
			}
		}
	}

	private static boolean checkForAttrType(String attribute) {
		for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
			String day = Character.toUpperCase(dayOfWeek.name().toLowerCase().charAt(0))
					+ dayOfWeek.name().toLowerCase().substring(1, dayOfWeek.name().length());
			if (attribute.contains(day)) {
				return true;
			} else if (attribute.contains(dayOfWeek.name())) {
				return true;
			} else if (attribute.contains(dayOfWeek.name().toLowerCase())) {
				return true;
			} 
		}
		return false;
	}

	public static boolean isCategoryRow(Row row) {
		if (row.getCell(0).getCellTypeEnum() == CellType.BLANK && row.getCell(1).getCellTypeEnum() == CellType.STRING
				&& row.getCell(2).getCellTypeEnum() == CellType.BLANK
				&& row.getCell(3).getCellTypeEnum() == CellType.BLANK
				&& row.getCell(4).getCellTypeEnum() == CellType.BLANK
				&& row.getCell(5).getCellTypeEnum() == CellType.BLANK
				&& row.getCell(6).getCellTypeEnum() == CellType.BLANK
				&& row.getCell(7).getCellTypeEnum() == CellType.BLANK
				&& row.getCell(8).getCellTypeEnum() == CellType.BLANK)
			return true;
		return false;
	}

	public static boolean isLastRow(Row row) {
		if (row.getCell(1).getCellTypeEnum() == CellType.STRING
				&& row.getCell(1).getStringCellValue().contentEquals("Total Sections"))
			return true;
		return false;
	}

	public File getFileFromResource(String name) {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource(name).getFile());
			return file;
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return null;
		}
	}
}
