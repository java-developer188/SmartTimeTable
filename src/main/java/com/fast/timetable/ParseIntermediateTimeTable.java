package com.fast.timetable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fast.timetable.pojo.TimeTableBean;

public class ParseIntermediateTimeTable {

	private static final String FILE_NAME = "BSCS-Spring-2017-Timetable-V5.xlsx";
	private static final String INTERMEDIATE_FILE = "C:\\Users\\Haider\\Desktop\\TimeTable.xlsx";
	private static final int DAY = 0;
	private static final int TIMESLOT = 1;
	private static final int ROOM = 2;
	private static final int COURSE = 3;
	private static final int SECTION = 4;
	private static final int TEACHER = 5;

	private List<TimeTableBean> timetableBeans = new ArrayList<TimeTableBean>();
	
	public static void main(String[] args) {

		try {
			List<TimeTableBean> list = new ArrayList<TimeTableBean>();
			FileInputStream cleanData = new FileInputStream(INTERMEDIATE_FILE);
			Workbook cleanDataWorkbook = new XSSFWorkbook(cleanData);
			Sheet datatypeSheet = cleanDataWorkbook.getSheetAt(0);
			PreparedStatement preparedStatement = null;
			int x = 2;
			do {
				Row currentRow = datatypeSheet.getRow(x);
				if (currentRow != null) {
					Cell cell = currentRow.getCell(DAY);
					if (cell != null && cell.getCellTypeEnum().equals(CellType.STRING)
							&& !cell.getStringCellValue().isEmpty()) {
						String value = cell.getStringCellValue();
						if (value.matches("END"))
							break;

						TimeTableBean timeTableBean = new TimeTableBean();
						timeTableBean.setDay(value);
						timeTableBean.setTime(currentRow.getCell(TIMESLOT).getStringCellValue());
						timeTableBean.setRoom(currentRow.getCell(ROOM).getStringCellValue());
						String courseId = currentRow.getCell(COURSE).getStringCellValue();
						String sectionId = currentRow.getCell(SECTION).getStringCellValue();
						String teacherId = currentRow.getCell(TEACHER).getStringCellValue();
						int cstId = CRUDPerformer.getCST(CRUDPerformer.getDBConnection(),preparedStatement, courseId, sectionId,
								teacherId);
						if (cstId > -1) {
							timeTableBean.setCstId(cstId);
							list.add(timeTableBean);
						}
					}
				}
				x++;
			} while (true);
			CRUDPerformer.insertTimeTable(list);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	


	public List<TimeTableBean> getTimetableBeans() {
		return timetableBeans;
	}





	public void extractTimeTable() throws SQLException{
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			FileInputStream cleanData = new FileInputStream(INTERMEDIATE_FILE);
			Workbook cleanDataWorkbook = new XSSFWorkbook(cleanData);
			Sheet datatypeSheet = cleanDataWorkbook.getSheetAt(0);
			dbConnection = CRUDPerformer.getDBConnection();
			int x = 2;
			do {
				Row currentRow = datatypeSheet.getRow(x);
				if (currentRow != null) {
					Cell cell = currentRow.getCell(DAY);
					if (cell != null && cell.getCellTypeEnum().equals(CellType.STRING)
							&& !cell.getStringCellValue().isEmpty()) {
						String value = cell.getStringCellValue();
						if (value.matches("END"))
							break;

						TimeTableBean timeTableBean = new TimeTableBean();
						timeTableBean.setDay(value);
						timeTableBean.setTime(currentRow.getCell(TIMESLOT).getStringCellValue());
						timeTableBean.setRoom(currentRow.getCell(ROOM).getStringCellValue());
						String courseId = currentRow.getCell(COURSE).getStringCellValue();
						String sectionId = currentRow.getCell(SECTION).getStringCellValue();
						String teacherId = currentRow.getCell(TEACHER).getStringCellValue();
						int cstId = CRUDPerformer.getCST(dbConnection, preparedStatement , courseId, sectionId,
								teacherId);
						if (cstId > -1) {
							timeTableBean.setCstId(cstId);
							timetableBeans.add(timeTableBean);
						}
					}
				}
				x++;
			} while (true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}
	}
	
}
