package com.fast.timetable.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fast.timetable.pojo.StudentPojo;
import com.fast.timetable.repository.CourseRepository;
import com.fast.timetable.repository.CourseSectionTeacherRepository;
import com.fast.timetable.repository.TeacherRepository;
import com.fast.timetable.repository.TimeTableRepository;
import com.fast.timetable.service.CourseService;
import com.fast.timetable.service.SectionService;
import com.fast.timetable.service.StudentService;
import com.fast.timetable.service.TeacherService;
import com.fast.timetable.service.TimeTableService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(path = "/student")
public class StudentController {

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	TimeTableRepository timeTableRepository;
	
	@Autowired
	TimeTableService timeTableService;

	@Autowired
	CourseSectionTeacherRepository cstRepository;

	@Autowired
	TeacherService teacherService;
	
	@Autowired
	TeacherRepository teacherRepository;

	@Autowired
	StudentService studentService;
	
	@Autowired
	CourseService courseService;
	
	@Autowired
	SectionService sectionService;


	
	
	/**
	 * To fetch student's timetable
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/register", method = { RequestMethod.GET, RequestMethod.POST })
	public String register(@RequestBody StudentPojo studentPojo) {
		
		long entry = System.currentTimeMillis();
		HashMap<String, Object> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(studentPojo);
//			if (map.containsKey("id")) {
//				Long id = Long.valueOf(map.get("id"));
//				List<HashMap<String, String>> result = studentService.getStudentTimeTable(id);
//				response.put("TimeTable", mapper.writeValueAsString(result));
//				response.put("TimeTableCount", result.size());
//				response.put("result", "SUCCESS");
//			} else {
//				// List<Teacher> result = teacherService.getTeachers();
//				// response.put("Teachers", mapper.writeValueAsString(result));
//				// response.put("TeacherCount", result.size());
//				// response.put("result", "SUCCESS");
//			}
		} catch (Exception e) {
			response.put("result", "ERROR");
			response.put("errorDescription", e.getMessage());

		} finally {
			long exit = System.currentTimeMillis();
		}
		try {
			return mapper.writeValueAsString(studentPojo);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return "{\"result\":\"ERROR\"," + "\"errorDescription\":\"JSON Processing Error\"}";
		} catch (NumberFormatException nfe) {
			return "{\"result\":\"ERROR\"," + "\"errorDescription\":\"Invalid ID\"}";
		}
	}

	
	/**
	 * To fetch student's timetable
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/id", method = { RequestMethod.GET, RequestMethod.POST })
	public String student(@RequestBody HashMap<String, String> map) {
		long entry = System.currentTimeMillis();
		HashMap<String, Object> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (map.containsKey("id")) {
				Long id = Long.valueOf(map.get("id"));
				List<HashMap<String, String>> result = studentService.getStudentTimeTable(id);
				response.put("TimeTable", mapper.writeValueAsString(result));
				response.put("TimeTableCount", result.size());
				response.put("result", "SUCCESS");
			} else {
				// List<Teacher> result = teacherService.getTeachers();
				// response.put("Teachers", mapper.writeValueAsString(result));
				// response.put("TeacherCount", result.size());
				// response.put("result", "SUCCESS");
			}
		} catch (Exception e) {
			response.put("result", "ERROR");
			response.put("errorDescription", e.getMessage());

		} finally {
			long exit = System.currentTimeMillis();
		}
		try {
			return mapper.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return "{\"result\":\"ERROR\"," + "\"errorDescription\":\"JSON Processing Error\"}";
		} catch (NumberFormatException nfe) {
			return "{\"result\":\"ERROR\"," + "\"errorDescription\":\"Invalid ID\"}";
		}
	}

}
