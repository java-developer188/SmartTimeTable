/**
 * 
 */
package com.fast.timetable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fast.timetable.entity.TimeTable;

/**
 * @author Haider
 *
 */

@Repository
public interface TimeTableRepository extends CrudRepository<TimeTable, Long> {
	@Query("select t.day,t.time,t.room,cst.course.fullName,cst.section.name from TimeTable t inner join t.courseSectionTeacher cst where cst.teacher.id = :id")
	List<Object[]> getTimetableByTeacherId(@Param(value = "id") Long id);
	
	@Query("select tt.day,tt.time,tt.room,cst.course.fullName,cst.section.name,cst.teacher.name from TimeTable tt inner join tt.courseSectionTeacher cst , CSTStudent x where x.courseSectionTeacher.id = cst.id and x.student.id = :id")
	List<Object[]> getTimetableByStudentId(@Param(value = "id") Long id);
	
	@Query("select tt.time from TimeTable tt where tt.time LIKE CONCAT('%',:hour,'%')")
	List<String> getTimeFromHour(@Param(value = "hour") int hour);
	
	@Query("select distinct tt.time from TimeTable tt ")
	List<String> getTimeSlots();
	
	@Query("select distinct tt.room from TimeTable tt ")
	List<String> getRooms();
	
	@Query(  "select tt.time,cst.course.fullName,cst.course.shortName,l.token,csts.student.mobileNumber from TimeTable tt  inner join tt.courseSectionTeacher cst ,CSTStudent csts ,Login l where csts.courseSectionTeacher.id = cst.id and l.student.id = csts.student.id and tt.day =:day and tt.time = :time")
	List<Object[]> getNotificationRecipentData(@Param(value = "day") String day , @Param(value = "time") String time);
	
}
