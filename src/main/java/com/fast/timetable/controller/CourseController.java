package com.fast.timetable.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fast.timetable.entity.Course;
import com.fast.timetable.pojo.RegistrationPojo;
import com.fast.timetable.repository.CourseRepository;
import com.fast.timetable.repository.CourseSectionTeacherRepository;
import com.fast.timetable.repository.TeacherRepository;
import com.fast.timetable.repository.TimeTableRepository;
import com.fast.timetable.service.CSTStudentService;
import com.fast.timetable.service.CourseSectionTeacherService;
import com.fast.timetable.service.CourseService;
import com.fast.timetable.service.SectionService;
import com.fast.timetable.service.StudentService;
import com.fast.timetable.service.TeacherService;
import com.fast.timetable.service.TimeTableService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(path = "/courses")
public class CourseController {

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
	
	@Autowired
	CourseSectionTeacherService courseSectionTeacherService;
	
	@Autowired
	CSTStudentService cstStudentService;


	/**
	 * Controller to get list of courses
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping( method = { RequestMethod.GET, RequestMethod.POST })
	public String courses(@RequestBody HashMap<String, String> map) {
		long entry = System.currentTimeMillis();
		HashMap<String, Object> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (map.containsKey("id")) {
				Long id = Long.valueOf(map.get("id"));
				List<HashMap<String, String>> result = courseSectionTeacherService.getCSTByCourseId(id);
				response.put("CST", mapper.writeValueAsString(result));
				response.put("CSTCount", result.size());
				response.put("result", "SUCCESS");
			} else {
				List<Course> result = courseService.getAll();
				response.put("Courses", mapper.writeValueAsString(result));
				response.put("CourseCount", result.size());
				response.put("result", "SUCCESS");
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
	
	
	/**
	 * Controller to get list of courses
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/batch", method = { RequestMethod.GET, RequestMethod.POST })
	public String coursesByBatch(@RequestBody HashMap<String, String> map) {
		long entry = System.currentTimeMillis();
		HashMap<String, Object> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (map.containsKey("batch")) {
				Integer batch = Integer.valueOf(map.get("batch"));
				List<Course> result = courseService.getCoursesByBatch(batch);
				response.put("Courses", mapper.writeValueAsString(result));
				response.put("CourseCount", result.size());
				response.put("result", "SUCCESS");
			} else {
				response.put("result", "ERROR");
				response.put("errorDescription","Value of year must be provided");
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
	
	/**
	 * Controller to get list of courses
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/selected", method = { RequestMethod.GET, RequestMethod.POST })
	public String selectedCourses(@RequestBody HashMap<String, String> map) {
		long entry = System.currentTimeMillis();
		HashMap<String, Object> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (map.containsKey("id")) {
				Long id = Long.valueOf(map.get("id"));
				List<HashMap<String, String>> result = cstStudentService.findStudentCourses(id);
				response.put("CST", mapper.writeValueAsString(result));
				response.put("CSTCount", result.size());
				response.put("result", "SUCCESS");
			} else {
				response.put("result", "ERROR");
				response.put("errorDescription","Student ID must be provided");
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
	
	/**
	 * Controller to get list of courses
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/add", method = { RequestMethod.GET, RequestMethod.POST })
	public String addCourse(@RequestBody HashMap<String, String> map) {
		long entry = System.currentTimeMillis();
		HashMap<String, Object> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (map.containsKey("id") && map.containsKey("studentId")) {
				Long id = Long.valueOf(map.get("id"));
				Long studentId = Long.valueOf(map.get("studentId"));
				List<HashMap<String, String>> result = cstStudentService.addCourse(id, studentId);
				response.put("CST", mapper.writeValueAsString(result));
				response.put("CSTCount", result.size());
				response.put("result", "SUCCESS");
			} else {
				response.put("result", "ERROR");
				response.put("errorDescription","Invalid arguments");
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
	
	
	/**
	 * Controller to get list of courses
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/remove", method = { RequestMethod.GET, RequestMethod.POST })
	public String removeCourse(@RequestBody HashMap<String, String> map) {
		long entry = System.currentTimeMillis();
		HashMap<String, Object> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (map.containsKey("id") && map.containsKey("studentId")) {
				Long id = Long.valueOf(map.get("id"));
				Long studentId = Long.valueOf(map.get("studentId"));
				List<HashMap<String, String>> result = cstStudentService.removeCourse(id, studentId);
				response.put("CST", mapper.writeValueAsString(result));
				response.put("CSTCount", result.size());
				response.put("result", "SUCCESS");
			} else {
				response.put("result", "ERROR");
				response.put("errorDescription","Invalid arguments");
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
