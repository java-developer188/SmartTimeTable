package com.fast.timetable.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fast.timetable.entity.Course;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {

	public Course findByFullName(String courseFullName);
	
	
	public Course findByCode(String code);
	
	public List<Course> findByBatch(int batch);

}
