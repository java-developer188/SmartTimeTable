package com.fast.timetable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.fast.timetable.entity.CourseSectionTeacher;

public interface CourseSectionTeacherRepository extends CrudRepository<CourseSectionTeacher, Long> {

	@Query("FROM CourseSectionTeacher cst WHERE cst.course.fullName = :courseFullName"
			+ " and cst.section.name = :section" + " and cst.teacher.name like %:teacherName%")
	public CourseSectionTeacher findByCST(@Param("courseFullName") String courseFullName,
			@Param("section") String section, @Param("teacherName") String teacherName);
	
	@Query("FROM CourseSectionTeacher cst WHERE cst.course.code = :code"
			+ " and cst.section.name = :section ")
	public CourseSectionTeacher findByCourseSection(@Param("code") String code,
			@Param("section") String section);

	public List<CourseSectionTeacher> findByCourseId(long courseId);
	
	public List<CourseSectionTeacher> findByTeacherId(long teacherId);

}
