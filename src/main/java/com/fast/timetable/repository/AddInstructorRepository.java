package com.fast.timetable.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fast.timetable.entity.AddInstructor;

@Repository
public interface AddInstructorRepository extends CrudRepository<AddInstructor, Long> {

	@Query(value="from AddInstructor ai where ai.date >= :date and (ai.invitedBy.id = :teacher or ai.teacher.id=:teacher)")
	public List<AddInstructor> findInboxItem(@Param("teacher") long teacher ,@Param("date") Date date);
 
}
