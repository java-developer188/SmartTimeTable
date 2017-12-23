package com.fast.timetable.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fast.timetable.entity.CourseSectionTeacher;
import com.fast.timetable.entity.Login;
import com.fast.timetable.entity.Student;
import com.fast.timetable.pojo.ChangePasswordPojo;
import com.fast.timetable.pojo.RegistrationPojo;
import com.fast.timetable.service.CourseSectionTeacherService;
import com.fast.timetable.service.RegistrationService;
import com.fast.timetable.service.StudentService;

@Controller
@RequestMapping(path = "/web")
public class WebController {

	@Autowired
	StudentService studentService;

	@Autowired
	CourseSectionTeacherService courseSectionTeacherService;

	@Autowired
	RegistrationService registrationService;

	/**
	 * Controller to call login from web page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public String defaultPage(@ModelAttribute("attr") String param, Model model) {
		if (param != null && !param.isEmpty()) {
			String decrypt = encryptDecrypt((String) param);
			long id = Long.parseLong(decrypt);
			if (id > 0) {
				Student student = studentService.getStudentById(id);
				if (student != null) {
					model.addAttribute("registrationSuccess", true);
					return "login";
				}
			} else {
				model.addAttribute("registrationFailed", true);
				return "login";
			}
		}
		return "login";
	}

	/**
	 * Controller to call Category page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/login", method = { RequestMethod.GET, RequestMethod.POST })
	public String login(@ModelAttribute Login login, Model model) {
		long entry = System.currentTimeMillis();
		String username = "", password = "";
		if (login.getUsername() != null) {
			username = login.getUsername();
			password = login.getPassword();

			Student student = studentService.login(username, password);
			if (student != null) {
				List<Object> timetable = new ArrayList<>();
				for (HashMap<String, String> map : studentService.getStudentTimeTable(student.getId())) {

					Object record = new Object() {
						public String day = map.get("day");
						public String time = map.get("time");
						public String room = map.get("room");
						public String course = map.get("course");
						public String section = map.get("section");
						public String teacher = map.get("teacher");
					};
					timetable.add(record);
				}
				model.addAttribute("timetable", timetable);
				model.addAttribute("student", student);
				return "welcome";
			} else {
				model.addAttribute("loginfail", true);
				return "login";
			}
		} else {
			return "login";
		}

	}

	/**
	 * Controller to call registration page.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/register", method = { RequestMethod.GET, RequestMethod.POST })
	public String register(Model model) {
		List<CourseSectionTeacher> result = courseSectionTeacherService.getAllCST();
		model.addAttribute("cst", result);
		return "registration";
	}

	/**
	 * Controller to call login from web page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/register/submit", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView processRegistration(
			@ModelAttribute("registrationPojo") @Valid RegistrationPojo registrationPojo, BindingResult bindingResult,
			ModelMap model) {
		boolean validation = true;
		if (bindingResult.hasErrors()) {
			ModelAndView mav = new ModelAndView();
			if (bindingResult.hasFieldErrors("fullName")) {
				model.addAttribute("fullNameError", bindingResult.getFieldError("fullName").getDefaultMessage());
				validation = false;
			}
			if (bindingResult.hasFieldErrors("section")) {
				model.addAttribute("sectionError", bindingResult.getFieldError("section").getDefaultMessage());
				validation = false;
			}
			if (bindingResult.hasFieldErrors("rollNumber")) {
				model.addAttribute("rollNumberError", bindingResult.getFieldError("rollNumber").getDefaultMessage());
				validation = false;
			}
			if (bindingResult.hasFieldErrors("batch")) {
				model.addAttribute("batchError", bindingResult.getFieldError("batch").getDefaultMessage());
				validation = false;
			}
			if (bindingResult.hasFieldErrors("userName")) {
				model.addAttribute("userNameError", bindingResult.getFieldError("userName").getDefaultMessage());
				validation = false;
			}
			if (bindingResult.hasFieldErrors("email")) {
				model.addAttribute("emailError", bindingResult.getFieldError("email").getDefaultMessage());
				validation = false;
			}
			if (bindingResult.hasFieldErrors("mobileNumber")) {
				model.addAttribute("mobileNumberError",
						bindingResult.getFieldError("mobileNumber").getDefaultMessage());
				validation = false;
			}
			if (!validation) {
				model.addAttribute("validation", false);
				model.addAttribute("fullName", registrationPojo.getFullName());
				model.addAttribute("section", registrationPojo.getSection());
				model.addAttribute("rollNumber", registrationPojo.getRollNumber());
				model.addAttribute("batch", registrationPojo.getBatch());
				model.addAttribute("userName", registrationPojo.getUserName());
				model.addAttribute("email", registrationPojo.getEmail());
				model.addAttribute("mobileNumber", registrationPojo.getMobileNumber());
			}
			List<CourseSectionTeacher> result = courseSectionTeacherService.getAllCST();
			model.addAttribute("cst", result);
			mav.addObject("registrationPojo", registrationPojo);
			mav.setViewName("registration");
			return mav;
		}

		Student student = registrationService.register(registrationPojo);
		if (student != null) {
			model.addAttribute("attr", encryptDecrypt(Long.toString(student.getId())));
		} else {
			model.addAttribute("attr", "-1");
		}
		return new ModelAndView("redirect:/web", model);
	}
	
	/**
	 * Controller to call change password page.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/changepassword", method = { RequestMethod.GET})
	public String changepassword(Model model) {
		return "changepassword";
	}
	
	
	/**
	 * Controller to call change password page.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/changepassword", method = {RequestMethod.POST })
	public  ModelAndView changepassword(
			@ModelAttribute("changepasswordPojo") @Valid ChangePasswordPojo changePasswordPojo, BindingResult bindingResult,
			ModelMap model) { 
				boolean validation = true;
				if (bindingResult.hasErrors()) {
					ModelAndView mav = new ModelAndView();
					if (bindingResult.hasFieldErrors("userName")) {
						model.addAttribute("userNameError", bindingResult.getFieldError("userName").getDefaultMessage());
						validation = false;
					}
					if (bindingResult.hasFieldErrors("newPassword")) {
						model.addAttribute("newPasswordError", bindingResult.getFieldError("newPassword").getDefaultMessage());
						validation = false;
					}
					if (bindingResult.hasFieldErrors("confirmPassword")) {
						model.addAttribute("confirmPasswordError", bindingResult.getFieldError("confirmPassword").getDefaultMessage());
						validation = false;
					}
					if (bindingResult.hasFieldErrors("conditionTrue")) {
						model.addAttribute("isConditionTrue", bindingResult.getFieldError("conditionTrue").getDefaultMessage());
						validation = false;
					}
					if (!validation) {
						model.addAttribute("validation", false);
						model.addAttribute("userName", changePasswordPojo.getUserName());
						model.addAttribute("newPassword", "");
						model.addAttribute("confirmPassword", "");
					}
					mav.addObject("changePasswordPojo", changePasswordPojo);
					mav.setViewName("changepassword");
					return mav;
				}

				if (studentService.changePassword(changePasswordPojo.getUserName(), changePasswordPojo.getNewPassword())) {
					model.addAttribute("changePasswordSuccess", true);
				} else {
					model.addAttribute("changePasswordError", true);
				}
				return new ModelAndView("changepassword", model);
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
