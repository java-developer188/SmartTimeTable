package com.fast.timetable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.fast.timetable.entity.Seating;

public interface SeatingRepository extends CrudRepository<Seating, Long>{

	@Query("Select s FROM Seating s where s.courseSectionTeacherStudent.id = :cststudentid")
	public Seating findUsingCSTStudent(@Param(value = "cststudentid") long cstStudentId);
}
