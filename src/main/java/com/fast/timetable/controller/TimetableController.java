package com.fast.timetable.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fast.timetable.DaySheetParser;
import com.fast.timetable.entity.Course;
import com.fast.timetable.entity.CourseSectionTeacher;
import com.fast.timetable.entity.Student;
import com.fast.timetable.entity.Teacher;
import com.fast.timetable.entity.TimeTable;
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
public class TimetableController {

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

	boolean onlyOnce = true;

	/**
	 * Controller to call Category page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/test", method = RequestMethod.GET)
	public String test() {
		long entry = System.currentTimeMillis();
		HashMap<String, Object> response = new HashMap<>();
		ObjectMapper m = new ObjectMapper();
		try {
			Course course = courseRepository.findOne((long) 7);
			TimeTable tt = timeTableRepository.findOne((long) 5);
			CourseSectionTeacher cst = cstRepository.findOne((long) 6);
			response.put("Course", m.writeValueAsString(course));
			response.put("test", "testing value is good");
		} catch (Exception e) {

			response.put("resultcode", "INVALID_ARGUMENT");
			response.put("resultdescription", e.getMessage());

		} finally {
			long exit = System.currentTimeMillis();
		}
		try {
			return m.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			return "JSON Processing error";
		}
	}

	/**
	 * Controller to call Category page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/init", method = RequestMethod.GET)
	public String initialize(@RequestParam("value") boolean init) {
		long entry = System.currentTimeMillis();
		HashMap<String, Object> response = new HashMap<>();
		ObjectMapper m = new ObjectMapper();
		try {
			if (onlyOnce && init) {
				onlyOnce = false;
				courseService.save();
				teacherService.save();
				
				DaySheetParser daySheetParser = new DaySheetParser(courseService.getAll(), teacherService.getAll());
				daySheetParser.execute();
				
				sectionService.save();
				
				timeTableService.save();
				response.put("Result", "System Initialized Successfully");
			} else {
				response.put("Result", "Init Paramter found false");
			}
		} catch (Exception e) {

			response.put("resultcode", "INVALID_ARGUMENT");
			response.put("resultdescription", e.getMessage());

		} finally {
			long exit = System.currentTimeMillis();
		}
		try {
			return m.writeValueAsString(response);
		} catch (JsonProcessingException e) {
			return "JSON Processing error";
		}
	}

	/**
	 * Controller to call Category page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/login", method = { RequestMethod.GET, RequestMethod.POST })
	public String login(@RequestBody HashMap<String, String> map) {
		long entry = System.currentTimeMillis();
		HashMap<String, Object> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			String username = "", password = "";
			if (map.containsKey("username") && map.containsKey("password")) {
				username = map.get("username");
				password = map.get("password");

				Student student = studentService.login(username, password);
				response.put("Student", mapper.writeValueAsString(student));
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

	/**
	 * Controller to call Category page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/faculty", method = { RequestMethod.GET, RequestMethod.POST })
	public String faculty(@RequestBody HashMap<String, String> map) {
		long entry = System.currentTimeMillis();
		HashMap<String, Object> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (map.containsKey("id")) {
				Long id = Long.valueOf(map.get("id"));
				List<HashMap<String, String>> result = teacherService.getTeacherTimeTable(id);
				response.put("TimeTable", mapper.writeValueAsString(result));
				response.put("TimeTableCount", result.size());
				response.put("result", "SUCCESS");
			} else {
				List<Teacher> result = teacherService.getAll();
				response.put("Teachers", mapper.writeValueAsString(result));
				response.put("TeacherCount", result.size());
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
	 * Controller to call Category page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/courses", method = { RequestMethod.GET, RequestMethod.POST })
	public String courses(@RequestBody HashMap<String, String> map) {
		long entry = System.currentTimeMillis();
		HashMap<String, Object> response = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (map.containsKey("id")) {
				Long id = Long.valueOf(map.get("id"));
//				List<HashMap<String, String>> result = teacherService.getTeacherTimeTable(id);
//				response.put("TimeTable", mapper.writeValueAsString(result));
//				response.put("TimeTableCount", result.size());
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
	 * Controller to call Category page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/student", method = { RequestMethod.GET, RequestMethod.POST })
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