package com.fast.timetable.service;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fast.timetable.EmailService;
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

	@Autowired
	EmailService emailService;

	public Student register(RegistrationPojo registrationPojo) {
		Student student = null;
		Login login = null;
		CSTStudent cstStudent = null;
		ArrayList<CSTStudent> csts = new ArrayList<>();
		String password = "";
		try {
			student = getStudent(registrationPojo);

			if (student.getId() != null && student.getId() > 0) {

				for (Long id : registrationPojo.getCourses()) {// we sent cst id to registration page so in return we are getting it back
					CourseSectionTeacher cst = courseSectionTeacherRepository.findOne(id);
					
					if (cst != null) {
						cstStudent = new CSTStudent();
						cstStudent.setCourseSectionTeacher(cst);
						cstStudent.setStudent(student);
						cstStudentRepository.save(cstStudent);
						csts.add(cstStudent);
					}
				}

				login = new Login();
				login.setUsername(registrationPojo.getUserName());
				login.setStudent(student);
				login = loginRepository.save(login);

				if (login.getId() != null && login.getId() > 0) {
					long id = login.getId();
					password = Long.toString(id) + "smart@"+student.getFullName().split(" ")[0];
					login.setPassword(encryptDecrypt(password));
					loginRepository.save(login);
				}

				emailService.sendSimpleMessage(student.getEmail(), "Smart TimeTable Credentials",
						"Dear user your Password for Username:'" + login.getUsername() + "' is " + password);

				return student;

			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			if (student != null) {
				if (login != null) {
					loginRepository.delete(login);
				}
				for (CSTStudent obj : csts) {
					cstStudentRepository.delete(obj);
				}
				studentRepository.delete(student);
			}
			System.out.println(e.getStackTrace());
			return null;
		}
	}

	public Student getStudent(RegistrationPojo registrationPojo) {
		Student student;
		student = new Student();
		student.setBatch(registrationPojo.getBatch());
		student.setFullName(registrationPojo.getFullName());
		student.setEmail(registrationPojo.getEmail());
		student.setRollNumber(registrationPojo.getRollNumber());
		student.setSection(registrationPojo.getSection());
		student.setMobileNumber(registrationPojo.getMobileNumber());

		student = studentRepository.save(student);
		return student;
	}
	
	private String encryptDecrypt(String input) {
		char[] key = { 's', 'm', 'a', 'r', 't' }; // Can be any chars, and any
													// length array
		StringBuilder output = new StringBuilder();

		for (int i = 0; i < input.length(); i++) {
			output.append((char) (input.charAt(i) ^ key[i % key.length]));
		}

		return output.toString();
	}
}
