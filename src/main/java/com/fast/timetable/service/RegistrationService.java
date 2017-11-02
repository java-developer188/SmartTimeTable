package com.fast.timetable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fast.timetable.entity.CSTStudent;
import com.fast.timetable.entity.CourseSectionTeacher;
import com.fast.timetable.entity.Login;
import com.fast.timetable.entity.Student;
import com.fast.timetable.pojo.RegistrationPojo;
import com.fast.timetable.repository.CSTStudentRepository;
import com.fast.timetable.repository.CourseSectionTeacherRepository;
import com.fast.timetable.repository.LoginRepository;
import com.fast.timetable.repository.StudentRepository;

@Service
public class RegistrationService {

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	LoginRepository loginRepository;

	@Autowired
	CSTStudentRepository cstStudentRepository;

	@Autowired
	CourseSectionTeacherRepository courseSectionTeacherRepository;

	public boolean register(RegistrationPojo registrationPojo) {
		Student student = new Student();
		student.setBatch(registrationPojo.getBatch());
		student.setFullName(registrationPojo.getFullName());
		student.setEmail(registrationPojo.getEmail());
		student.setRollNumber(registrationPojo.getRollNumber());
		student.setSection(registrationPojo.getSection());
		student.setMobileNumber(registrationPojo.getMobileNumber());

		student = studentRepository.save(student);

		if (student.getId() != null && student.getId() > 0) {

			for (Long id : registrationPojo.getCourses()) {
				CourseSectionTeacher cst = courseSectionTeacherRepository.findOne(id);
				if (cst != null) {
					CSTStudent cstStudent = new CSTStudent();
					cstStudent.setCourseSectionTeacher(cst);
					cstStudent.setStudent(student);
					cstStudentRepository.save(cstStudent);
				}
			}

			Login login = null;

			login = new Login();
			login.setUsername(registrationPojo.getUserName());
			login.setStudent(student);
			login = loginRepository.save(login);

			if (login.getId() != null && login.getId() > 0) {
				long id = login.getId();
				String seed = "FAST";
				login.setPassword(Long.toString(id));
				loginRepository.save(login);
			}
			return true;

		}
		else{
			return false;
		}
	}
}
