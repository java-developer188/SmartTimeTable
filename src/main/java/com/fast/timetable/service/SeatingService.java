package com.fast.timetable.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fast.timetable.entity.CSTStudent;
import com.fast.timetable.entity.Seating;
import com.fast.timetable.entity.Student;
import com.fast.timetable.repository.CSTStudentRepository;
import com.fast.timetable.repository.SeatingRepository;

@Service
public class SeatingService {

	@Autowired
	CSTStudentRepository cstStudentRepository;

	@Autowired
	SeatingRepository seatingRepository;

	public List<HashMap<String, String>> getSeating(Long studentId) {
		List<HashMap<String, String>> list = new ArrayList<>();
		for (CSTStudent cstStudent : cstStudentRepository.findByStudentId(studentId)) {
			Seating seating = seatingRepository.findUsingCSTStudent(cstStudent.getId());
			if (seating != null) {
			HashMap<String, String> map = new HashMap<>();
			map.put("course", seating.getCourseSectionTeacherStudent().getCourseSectionTeacher().getCourse().getFullName());
			map.put("time", seating.getTime());
			map.put("room", seating.getRoom());
			list.add(map);
			}
		}
		return list;
	}
}
