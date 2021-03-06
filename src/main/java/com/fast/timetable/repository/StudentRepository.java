package com.fast.timetable.repository;

import org.springframework.data.repository.CrudRepository;

import com.fast.timetable.entity.Student;

public interface StudentRepository extends CrudRepository<Student, Long> {

	public Student save(Student student);
	
	public Student findByRollNumber(String rollNumber);
}
