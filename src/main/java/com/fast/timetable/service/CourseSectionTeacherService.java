package com.fast.timetable.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fast.timetable.entity.CourseSectionTeacher;
import com.fast.timetable.repository.CourseSectionTeacherRepository;

@Service
public class CourseSectionTeacherService {

	@Autowired
	CourseSectionTeacherRepository courseSectionTeacherRepository;
	
	public List<HashMap<String, String>> getCSTByCourseId(Long courseId){
		List<HashMap<String, String>> list = new ArrayList<>();
		for (CourseSectionTeacher cst : courseSectionTeacherRepository.findByCourseId(courseId)) {
			HashMap<String, String> map = new HashMap<>();
			map.put("id", Long.toString(cst.getId()));
			map.put("courseCode", cst.getCourse().getCode());
			map.put("fullname", cst.getCourse().getFullName());
			map.put("shortname",cst.getCourse().getShortName());
			map.put("teacher", cst.getTeacher().getName());
			map.put("section", cst.getSection().getName());
			list.add(map);
		}
		return list;
		
	}
	
	public List<CourseSectionTeacher> getAllCST(){
		List<CourseSectionTeacher> list = new ArrayList<>();
//		List<HashMap<String, String>> list = new ArrayList<>();
		for (CourseSectionTeacher cst : courseSectionTeacherRepository.findAll()) {
//			HashMap<String, String> map = new HashMap<>();
//			map.put("id", Long.toString(cst.getId()));
//			map.put("courseCode", cst.getCourse().getCode());
//			map.put("fullname", cst.getCourse().getFullName());
//			map.put("shortname",cst.getCourse().getShortName());
//			map.put("teacher", cst.getTeacher().getName());
//			map.put("section", cst.getSection().getName());
//			list.add(map);
			list.add(cst);
		}
		return list;
		
	}
}
