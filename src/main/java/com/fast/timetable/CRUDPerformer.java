package com.fast.timetable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Component;

import com.fast.timetable.pojo.CourseBean;
import com.fast.timetable.pojo.SectionBean;
import com.fast.timetable.pojo.TeacherBean;
import com.fast.timetable.pojo.TimeTableBean;

@Component
public class CRUDPerformer implements CommandLineRunner {

	public static Connection getDBConnection() {

		System.out.println("-------- MySQL JDBC Connection Testing ------------");

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return null;
		}

		System.out.println("MySQL JDBC Driver Registered!");
		Connection connection = null;

		try {
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/uni_schedule", "root", "root");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		return connection;

		// Course course = new Course();
		// course.setShortName("ABC");
		//
		// Section section = new Section();
		// section.setName("A");
		//
		// Teacher teacher = new Teacher();
		// teacher.setName("Tahir");
		//
		//
		// courseRepository.save(course);
		// sectionRepository.save(section);
		// teacherRepository.save(teacher);

	}

	public static int getCST(Connection conn, PreparedStatement preparedStatement, String shortName, String section,
			String teacher) throws SQLException {
		int cID = -1, sID = -1, tID = -1, cstID = -1;
		try {
			String selectSQL = "SELECT c.id FROM course c " + "WHERE c.short_name = ?";

			preparedStatement = conn.prepareStatement(selectSQL);

			preparedStatement.setString(1, shortName);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				cID = rs.getInt("id");
			}

			selectSQL = "SELECT s.id FROM section s " + "WHERE s.name = ?";

			preparedStatement = conn.prepareStatement(selectSQL);

			preparedStatement.setString(1, section);
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				sID = rs.getInt("id");
			}

			if (teacher.split(" ").length > 1) {
				String concat = "CONCAT(";
				String chunks[] = teacher.split(" ");
				for (String chunk : chunks) {
					concat += "'%',?,";
				}
				concat += "'%')";
				selectSQL = "SELECT t.id FROM teacher t " + "WHERE t.full_name LIKE " + concat;

				preparedStatement = conn.prepareStatement(selectSQL);

				for (int i = 0; i < chunks.length; i++) {
					preparedStatement.setString(i + 1, chunks[i]);
				}
			} else {
				selectSQL = "SELECT t.id FROM teacher t " + "WHERE t.full_name LIKE CONCAT('%',?,'%')";

				preparedStatement = conn.prepareStatement(selectSQL);

				preparedStatement.setString(1, teacher);
			}

			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				tID = rs.getInt("id");
			}

			selectSQL = "SELECT cst.id FROM course_section_teacher cst "
					+ "WHERE cst.course_id = ? AND cst.section_id = ? AND cst.teacher_id = ?";

			preparedStatement = conn.prepareStatement(selectSQL);

			preparedStatement.setString(1, Integer.toString(cID));
			preparedStatement.setString(2, Integer.toString(sID));
			preparedStatement.setString(3, Integer.toString(tID));
			rs = preparedStatement.executeQuery();
			while (rs.next()) {
				cstID = rs.getInt("id");
			}
			if (cstID != -1 && cstID > 0) {
				return cstID;
			} else {
				String insertTableSQL = "INSERT INTO course_section_teacher"
						+ "(section_id,course_id,teacher_id) VALUES " + "(?,?,?)";

				preparedStatement = conn.prepareStatement(insertTableSQL);

				preparedStatement.setInt(1, sID);
				preparedStatement.setInt(2, cID);
				preparedStatement.setInt(3, tID);

				System.out.println(preparedStatement.toString());
				System.out.println("sid:" + sID);
				System.out.println("cid:" + cID);
				System.out.println("tid:" + tID);
				// execute insert SQL stetement
				preparedStatement.execute();

				selectSQL = "SELECT cst.id FROM course_section_teacher cst "
						+ "WHERE cst.course_id = ? AND cst.section_id = ? AND cst.teacher_id = ?";

				preparedStatement = conn.prepareStatement(selectSQL);

				preparedStatement.setString(1, Integer.toString(cID));
				preparedStatement.setString(2, Integer.toString(sID));
				preparedStatement.setString(3, Integer.toString(tID));
				rs = preparedStatement.executeQuery();
				while (rs.next()) {
					cstID = rs.getInt("id");
				}
				return cstID;
			}
		} catch (SQLException e) {

			System.out.println(e.getMessage());
			return -1;

		}
	}

	private static List<CourseBean> getCourseByShortName(Connection conn, String shortName) {
		List<CourseBean> list = new ArrayList<>();
		PreparedStatement preparedStatement = null;
		try {
			String selectSQL = "SELECT * FROM course " + "WHERE short_name = ?";

			preparedStatement = conn.prepareStatement(selectSQL);

			preparedStatement.setString(1, shortName);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				CourseBean courseBean = new CourseBean();
				courseBean.setId(rs.getInt("id"));
				courseBean.setBatch(rs.getInt("batch"));
				courseBean.setCategory(rs.getString("category"));
				courseBean.setCode(rs.getString("code"));
				courseBean.setCreditHours(rs.getInt("credit_hours"));
				courseBean.setFullName(rs.getString("full_name"));
				courseBean.setNumberOfSections(rs.getInt("number_of_sections"));
				courseBean.setPlanning(rs.getBoolean("planning"));
				courseBean.setShortName(rs.getString("short_name"));
				list.add(courseBean);
			}
			return list;
		} catch (SQLException e) {

			System.out.println(e.getMessage());
			return null;

		}
	}

	public static void insertTimeTable(List<TimeTableBean> timeTableBeans) throws SQLException {

		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			dbConnection = getDBConnection();

			for (TimeTableBean timeTable : timeTableBeans) {
				String insertTableSQL = "INSERT INTO time_table" + "(day,room,time,course_section_teacher_id) VALUES"
						+ "(?,?,?,?)";

				preparedStatement = dbConnection.prepareStatement(insertTableSQL);

				preparedStatement.setString(1, timeTable.getDay());
				preparedStatement.setString(2, timeTable.getRoom());
				preparedStatement.setString(3, timeTable.getTime());
				preparedStatement.setInt(4, timeTable.getCstId());
				// execute insert SQL stetement
				preparedStatement.execute();

				System.out.println("Record is inserted into Timetable table!");
			}
		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}

	}

	public static void insertCourses(List<CourseBean> courses) throws SQLException {

		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			dbConnection = getDBConnection();

			for (CourseBean courseBean : courses) {
				String insertTableSQL = "INSERT INTO course"
						+ "(batch,category,code,credit_hours,full_name,number_of_sections,planning,short_name) VALUES"
						+ "(?,?,?,?,?,?,?,?)";

				preparedStatement = dbConnection.prepareStatement(insertTableSQL);

				preparedStatement.setInt(1, courseBean.getBatch());
				preparedStatement.setString(2, courseBean.getCategory());
				preparedStatement.setString(3, courseBean.getCode());
				preparedStatement.setInt(4, courseBean.getCreditHours());
				preparedStatement.setString(5, courseBean.getFullName());
				preparedStatement.setInt(6, courseBean.getNumberOfSections());
				preparedStatement.setBoolean(7, courseBean.isPlanning());
				preparedStatement.setString(8, courseBean.getShortName());
				// execute insert SQL stetement
				preparedStatement.execute();

				System.out.println("Record is inserted into Course table!");
			}
		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}

	}

	public static void insertSections(List<SectionBean> sections) throws SQLException {

		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			dbConnection = getDBConnection();

			for (SectionBean sectionBean : sections) {
				String insertTableSQL = "INSERT INTO section" + "(name) VALUES" + "(?)";

				preparedStatement = dbConnection.prepareStatement(insertTableSQL);

				preparedStatement.setString(1, sectionBean.getName());
				// execute insert SQL stetement
				preparedStatement.execute();

				System.out.println("Record is inserted into Section table!");
			}
		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}

	}

	public static void insertTeachers(List<TeacherBean> teachers) throws SQLException {

		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		try {
			dbConnection = getDBConnection();

			for (TeacherBean teacherBean : teachers) {
				String insertTableSQL = "INSERT INTO teacher" + "(full_name) VALUES" + "(?)";

				preparedStatement = dbConnection.prepareStatement(insertTableSQL);

				preparedStatement.setString(1, teacherBean.getName());
				// execute insert SQL stetement
				preparedStatement.execute();

				System.out.println("Record is inserted into Teacher table!");
			}
		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}

	}
	// public void insertListOfPojos(final List<CourseBean> courses) {
	//
	// String sql = "INSERT INTO " + "MY_TABLE " + "(FIELD_1,FIELD_2,FIELD_3) "
	// + "VALUES " + "(?,?,?)";
	//
	// getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
	//
	// @Override
	// public void setValues(PreparedStatement ps, int i) throws SQLException {
	//
	// MyPojo myPojo = myPojoList.get(i);
	// ps.setString(1, myPojo.getField1());
	// ps.setString(2, myPojo.getField2());
	// ps.setString(3, myPojo.getField3());
	//
	// }
	//
	// @Override
	// public int getBatchSize() {
	// return myPojoList.size();
	// }
	// });
	//
	// }

	@Override
	public void run(String... args) throws Exception {
//		FetchController.main(args);
//		DaySheetParser.main(args);
//		CourseDataManager courseDataManager = new CourseDataManager();
//		insertCourses(courseDataManager.getCourseBeans());
//		SectionDataManager sectionDataManager = new SectionDataManager();
//		sectionDataManager.extractSections();
//		insertSections(sectionDataManager.getSectionBeans());
//		TeacherDataManager teacherDataManager = new TeacherDataManager();
//		teacherDataManager.extractTeacher();
//		insertTeachers(teacherDataManager.getTeacherBeans());
//		ParseIntermediateTimeTable parseIntermediateTimeTable = new ParseIntermediateTimeTable();
//		parseIntermediateTimeTable.extractTimeTable();
//		insertTimeTable(parseIntermediateTimeTable.getTimetableBeans());
	}

}
