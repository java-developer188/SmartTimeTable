package com.fast.timetable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.fast.timetable.entity.CSTStudent;
import com.fast.timetable.entity.CourseSectionTeacher;
import com.fast.timetable.entity.Student;

public interface CSTStudentRepository extends CrudRepository<CSTStudent, Long> {

	@Transactional
	@Modifying
	@Query("delete FROM CSTStudent csts  where csts.student.id = :studentid and csts.courseSectionTeacher.id = :cstid")
	void removeCst(@Param(value = "studentid") long studentid, @Param(value = "cstid") long cstid);

	@Query("Select csts.courseSectionTeacher FROM CSTStudent csts inner join csts.student s where s.id = :studentid")
	List<CourseSectionTeacher> findStudentCourses(@Param(value = "studentid") long studentid);

	@Query("Select csts FROM CSTStudent csts inner join csts.student s inner join csts.courseSectionTeacher cst "
			+ "where s.id = :studentid and cst.id = :cstid")
	CSTStudent findUsingStudentAndCourseSectionTeacher(@Param(value = "studentid") long studentid,
			@Param(value = "cstid") long cstid);
	
	@Query("Select csts FROM CSTStudent csts inner join csts.student s inner join csts.courseSectionTeacher.course c "
			+ " where c.code like %:code% and s.rollNumber like %:roll%")
	CSTStudent findUsingStudentCourseTeacher(@Param(value = "code") String code,@Param(value = "roll") String rollNumber);

	@Query("FROM CSTStudent csts where csts.student.id = :studentid")
	List<CSTStudent> findByStudentId(@Param(value = "studentid") long studentid);
}
