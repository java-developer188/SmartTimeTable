package com.fast.timetable.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.fast.timetable.entity.Login;
import com.fast.timetable.entity.Student;

public interface LoginRepository extends CrudRepository<Login, Long> {

	@Query("select l.student from Login l where l.username = :username")
	public Student findStudentByUsername(@Param(value = "username")String username);
	
	@Query("select l.student from Login l where l.username = :username and l.password = :password")
	public Student findByUsernameAndPassword(@Param(value = "username")String username , @Param(value = "password")String password);
	
	public Login findByStudentId(Long id);
	
	public Login findByUsername(String username);
}
