package com.fast.timetable.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fast.timetable.entity.Student;
import com.fast.timetable.entity.Teacher;
import com.fast.timetable.repository.CourseSectionTeacherRepository;
import com.fast.timetable.repository.LoginRepository;
import com.fast.timetable.repository.StudentRepository;
import com.fast.timetable.repository.TeacherRepository;
import com.fast.timetable.repository.TimeTableRepository;

@Service
public class StudentService {

	@Autowired
	LoginRepository loginRepository;
	
	@Autowired
	TimeTableRepository timeTableRepository;
	
	public Student login(String username , String password){
		List<Teacher> list = new ArrayList<>();
		Student student = loginRepository.findByUsername(username);
		return student;
	}
	
	public List<HashMap<String, String>> getStudentTimeTable(Long id){
		List<HashMap<String, String>> list = new ArrayList<>();
		for(Object[] obj :timeTableRepository.getTimetableByStudentId(id)){
			HashMap<String, String> map = new HashMap<>();
			map.put("day", obj[0].toString());
			map.put("time", obj[1].toString());
			map.put("room", obj[2].toString());
			map.put("course", obj[3].toString());
			map.put("section", obj[4].toString());
			map.put("teacher", obj[5].toString());
			list.add(map);
		}
		return list ;
	}
	
}
