package com.fast.timetable.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.fast.timetable.entity.Login;
import com.fast.timetable.entity.Student;
import com.fast.timetable.entity.Teacher;

public interface LoginRepository extends CrudRepository<Login, Long> {

	@Query("select l.student from Login l where l.username = :username")
	public Student findStudentByUsername(@Param(value = "username")String username);
	
	@Query("select l.student from Login l where l.username = :username and l.password = :password and (l.student.id is not null and l.teacher.id is null)")
	public Student findStudentByUsernameAndPassword(@Param(value = "username")String username , @Param(value = "password")String password);
	
	@Query("select l.teacher from Login l where l.username = :username and l.password = :password and (l.student.id is null and l.teacher.id is not null)")
	public Teacher findTeacherByUsernameAndPassword(@Param(value = "username")String username , @Param(value = "password")String password);
	
	public Login findByStudentId(Long id);
	
	public Login findByUsername(String username);
}
