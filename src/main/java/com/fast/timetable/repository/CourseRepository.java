package com.fast.timetable.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fast.timetable.entity.Course;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {

	public Course findByFullName(String courseFullName);

}
