package com.fast.timetable.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fast.timetable.entity.CSTStudent;
import com.fast.timetable.entity.Course;
import com.fast.timetable.entity.CourseSectionTeacher;
import com.fast.timetable.entity.Student;
import com.fast.timetable.repository.CSTStudentRepository;
import com.fast.timetable.repository.CourseSectionTeacherRepository;
import com.fast.timetable.repository.StudentRepository;

@Service
public class CSTStudentService {
	@Autowired
	CourseSectionTeacherRepository courseSectionTeacherRepository;

	@Autowired
	StudentRepository studentRepository;

	@Autowired
	CSTStudentRepository cstStudentRepository;

	public List<HashMap<String, String>> findStudentCourses(Long studentId) {
		List<HashMap<String, String>> list = new ArrayList<>();

		for (CourseSectionTeacher cst : cstStudentRepository.findStudentCourses(studentId)) {
			HashMap<String, String> map = new HashMap<>();
			map.put("id", Long.toString(cst.getId()));
			map.put("courseCode", cst.getCourse().getCode());
			map.put("fullname", cst.getCourse().getFullName());
			map.put("shortname", cst.getCourse().getShortName());
			map.put("teacher", cst.getTeacher().getName());
			map.put("section", cst.getSection().getName());
			list.add(map);
		}
		return list;
	}

	public List<HashMap<String, String>> addCourse(Long cstId, Long studentId) throws IllegalArgumentException {
		List<HashMap<String, String>> list = new ArrayList<>();
		CourseSectionTeacher courseSectionTeacher = courseSectionTeacherRepository.findOne(cstId);
		if (courseSectionTeacher == null) {
			throw new IllegalArgumentException();
		}
		Student student = studentRepository.findOne(studentId);
		if (student == null) {
			throw new IllegalArgumentException();
		}

		CSTStudent cstStudent = new CSTStudent();
		cstStudent.setCourseSectionTeacher(courseSectionTeacher);
		cstStudent.setStudent(student);
		cstStudentRepository.save(cstStudent);

		return findStudentCourses(studentId);

	}

	public List<HashMap<String, String>> removeCourse(Long cstId, Long studentId) throws IllegalArgumentException {
		List<HashMap<String, String>> list = new ArrayList<>();

		if (studentRepository.findOne(studentId) == null) {
			throw new IllegalArgumentException();
		}
		if (courseSectionTeacherRepository.findOne(cstId) == null) {
			throw new IllegalArgumentException();
		}
		cstStudentRepository.removeCst(studentId, cstId);

		// for(CSTStudent cstStudent : cstStudentRepository.findAll()){
		// if(cstStudent.getCourseSectionTeacher().getId() == cstId &&
		// cstStudent.getStudent().getId() == studentId){
		// cstStudentRepository.delete(cstStudent);
		// break;
		// }
		// }

		return findStudentCourses(studentId);

	}

	public List<HashMap<String, String>> attendance(Long studentId) {
		List<HashMap<String, String>> list = new ArrayList<>();
		if (studentId != null) {
			for (CSTStudent cstStudent : cstStudentRepository.findByStudentId(studentId)) {
				HashMap<String, String> map = new HashMap<>();
				map.put("id", Long.toString(cstStudent.getId()));
				map.put("course", cstStudent.getCourseSectionTeacher().getCourse().getFullName());
				map.put("attended", Integer.toString(cstStudent.getTotalAttend()));
				String per = Double.toString(calculatePercentage(cstStudent.getCourseSectionTeacher().getCourse(),
						cstStudent.getTotalAttend()));
				if (per.indexOf(".") + 3 < per.length()) {
					per = per.substring(0, per.indexOf(".") + 3);
				}
				map.put("percentage", per + " %");
				map.put("mark", Boolean.toString(canMarkAttendance(cstStudent.getLastAttend())));
				list.add(map);
			}
		}
		return list;
	}

	public List<HashMap<String, String>> markAttendance(Long id) {
		CSTStudent cstStudent = cstStudentRepository.findOne(id);
		if (canMarkAttendance(cstStudent.getLastAttend())) {
			cstStudent.setTotalAttend(cstStudent.getTotalAttend() + 1);
			cstStudent.setLastAttend(Calendar.getInstance().getTime());
			cstStudentRepository.save(cstStudent);
		}
		return attendance(cstStudent.getStudent().getId());
	}

	private Double calculatePercentage(Course course, Integer attended) {
		if (course != null) {

			if (course.isLab()) {
				return (double) ((attended / 16d) * 100);
			} else {
				return (double) ((attended / 48d) * 100);
			}
		}
		return 0d;
	}

	private boolean canMarkAttendance(Date lastAttended) {
		Date currentDate = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			currentDate = sdf.parse(sdf.format(currentDate));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (lastAttended != null) {
			return currentDate.after(lastAttended);
		}
		return true;
	}
}
