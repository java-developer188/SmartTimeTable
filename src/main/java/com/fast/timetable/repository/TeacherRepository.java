package com.fast.timetable.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fast.timetable.entity.Teacher;


@Repository
public interface TeacherRepository extends CrudRepository<Teacher, Long> {

	@Query("Select t From CourseTeacher ct join ct.teacher t where ct.course.fullName = :courseFullName "
			+ " and ct.teacher.name like %:teacherName%")
	public Teacher findByNameAndCourse(@Param("teacherName") String teacherName,
			@Param("courseFullName") String courseFullName);
	
	
	public Teacher findByName(@Param("teacherName") String teacherName);
}

