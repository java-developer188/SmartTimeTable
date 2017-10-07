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
	
}
