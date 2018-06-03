package com.fast.timetable.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fast.timetable.entity.AddInstructor;
import com.fast.timetable.entity.CourseSectionTeacher;
import com.fast.timetable.entity.Login;
import com.fast.timetable.entity.Teacher;
import com.fast.timetable.pojo.AddInstructorPojo;
import com.fast.timetable.pojo.CancelPojo;
import com.fast.timetable.pojo.ChangePasswordPojo;
import com.fast.timetable.pojo.MakeupPojo;
import com.fast.timetable.repository.AddInstructorRepository;
import com.fast.timetable.repository.CourseSectionTeacherRepository;
import com.fast.timetable.repository.TeacherRepository;
import com.fast.timetable.repository.TimeTableRepository;
import com.fast.timetable.service.CourseSectionTeacherService;
import com.fast.timetable.service.RegistrationService;
import com.fast.timetable.service.StudentService;
import com.fast.timetable.service.TeacherService;

@Controller
@RequestMapping(path = "/web/faculty")
public class FacultyWebController {

	@Autowired
	StudentService studentService;

	@Autowired
	TeacherService teacherService;

	@Autowired
	CourseSectionTeacherService courseSectionTeacherService;

	@Autowired
	RegistrationService registrationService;

	@Autowired
	TimeTableRepository timeTableRepository;

	@Autowired
	AddInstructorRepository addInstructorRepository;

	@Autowired
	TeacherRepository teacherRepository;

	@Autowired
	CourseSectionTeacherRepository courseSectionTeacherRepository;

	/**
	 * Controller to call login from web page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
	public String defaultPage(@ModelAttribute("logout") String param, Model model, HttpSession httpSession) {
		if (param != null && param.equals("true")) {
			httpSession.removeAttribute("user");
			httpSession.removeAttribute("username");
		}
		return "facultylogin";

	}

	/**
	 * Controller to call login from web page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/cancelclass", method = { RequestMethod.GET, RequestMethod.POST })
	public String cancelclass(Model model, HttpSession httpSession) {
		if (httpSession.getAttribute("user") != null && httpSession.getAttribute("user") instanceof Teacher) {
			Teacher teacher = (Teacher) httpSession.getAttribute("user");
			List<CourseSectionTeacher> cst = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
			model.addAttribute("time", timeTableRepository.getTimeSlots());
			model.addAttribute("cst", cst);
			return "cancelclass";
		} else
			return "facultylogin";
	}

	/**
	 * Controller to call login from web page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/cc/submit", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView classCancelSubmit(@ModelAttribute("cancelmakeupPojo") @Valid CancelPojo cancelPojo,
			BindingResult bindingResult, ModelMap model, HttpSession httpSession) {
		ModelAndView mav = new ModelAndView();
		if (httpSession.getAttribute("user") != null && httpSession.getAttribute("user") instanceof Teacher) {
			Teacher teacher = (Teacher) httpSession.getAttribute("user");
			boolean validation = true;
			if (bindingResult.hasErrors()) {
				if (bindingResult.hasFieldErrors("date")) {
					model.addAttribute("dateError", bindingResult.getFieldError("date").getDefaultMessage());
					validation = false;
				}
				if (bindingResult.hasFieldErrors("cst")) {
					model.addAttribute("cstError", bindingResult.getFieldError("cst").getDefaultMessage());
					validation = false;
				}
				if (bindingResult.hasFieldErrors("timeslot")) {
					model.addAttribute("timeslotError", bindingResult.getFieldError("timeslot").getDefaultMessage());
					validation = false;
				}
				if (!validation) {
					model.addAttribute("validation", false);
					model.addAttribute("date", cancelPojo.getDate());
					model.addAttribute("cst", cancelPojo.getCst());
					model.addAttribute("timeslot", cancelPojo.getTimeslot());
				}
				List<CourseSectionTeacher> result = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
				model.addAttribute("cst", result);
				model.addAttribute("time", timeTableRepository.getTimeSlots());
				mav.addObject("cancelmakeupPojo", cancelPojo);
				mav.setViewName("cancelclass");
				return mav;
			}
			try {
				courseSectionTeacherService.cancelClass(cancelPojo);
				List<CourseSectionTeacher> result = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
				model.addAttribute("cst", result);
				model.addAttribute("time", timeTableRepository.getTimeSlots());
				model.addAttribute("cancelSuccess", true);
				mav.addObject("cancelmakeupPojo", cancelPojo);
				mav.setViewName("cancelclass");
				return mav;
			} catch (Exception e) {
				List<CourseSectionTeacher> result = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
				model.addAttribute("cst", result);
				model.addAttribute("time", timeTableRepository.getTimeSlots());
				model.addAttribute("cancelError", true);
				mav.addObject("cancelmakeupPojo", cancelPojo);
				mav.setViewName("cancelclass");
				return mav;
			}
		}
		mav.setViewName("facultylogin");
		return mav;

	}

	/**
	 * Controller to call login from web page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/makeupclass", method = { RequestMethod.GET, RequestMethod.POST })
	public String makeup(Model model, HttpSession httpSession) {
		if (httpSession.getAttribute("user") != null && httpSession.getAttribute("user") instanceof Teacher) {
			Teacher teacher = (Teacher) httpSession.getAttribute("user");
			List<CourseSectionTeacher> cst = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
			model.addAttribute("time", timeTableRepository.getTimeSlots());
			model.addAttribute("rooms", timeTableRepository.getRooms());
			model.addAttribute("cst", cst);
			return "makeupclass";
		} else
			return "facultylogin";
	}

	/**
	 * Controller to call login from web page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/mkup/submit", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView classMakeupSubmit(@ModelAttribute("cancelmakeupPojo") @Valid MakeupPojo makeupPojo,
			BindingResult bindingResult, ModelMap model, HttpSession httpSession) {
		ModelAndView mav = new ModelAndView();
		if (httpSession.getAttribute("user") != null && httpSession.getAttribute("user") instanceof Teacher) {
			Teacher teacher = (Teacher) httpSession.getAttribute("user");
			boolean validation = true;
			if (bindingResult.hasErrors()) {
				if (bindingResult.hasFieldErrors("date")) {
					model.addAttribute("dateError", bindingResult.getFieldError("date").getDefaultMessage());
					validation = false;
				}
				if (bindingResult.hasFieldErrors("room")) {
					model.addAttribute("roomError", bindingResult.getFieldError("room").getDefaultMessage());
					validation = false;
				}
				if (bindingResult.hasFieldErrors("cst")) {
					model.addAttribute("cstError", bindingResult.getFieldError("cst").getDefaultMessage());
					validation = false;
				}
				if (bindingResult.hasFieldErrors("timeslot")) {
					model.addAttribute("timeslotError", bindingResult.getFieldError("timeslot").getDefaultMessage());
					validation = false;
				}
				if (!validation) {
					model.addAttribute("validation", false);
					model.addAttribute("date", makeupPojo.getDate());
					model.addAttribute("cst", makeupPojo.getCst());
					model.addAttribute("room", makeupPojo.getRoom());
					model.addAttribute("timeslot", makeupPojo.getTimeslot());
				}
				List<CourseSectionTeacher> result = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
				model.addAttribute("cst", result);
				model.addAttribute("time", timeTableRepository.getTimeSlots());
				model.addAttribute("rooms", timeTableRepository.getRooms());
				mav.addObject("cancelmakeupPojo", makeupPojo);
				mav.setViewName("makeupclass");
				return mav;
			}
			try {
				courseSectionTeacherService.makeupClass(makeupPojo);
				List<CourseSectionTeacher> result = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
				model.addAttribute("cst", result);
				model.addAttribute("time", timeTableRepository.getTimeSlots());
				model.addAttribute("rooms", timeTableRepository.getRooms());
				model.addAttribute("makeupSuccess", true);
				mav.addObject("cancelmakeupPojo", makeupPojo);
				mav.setViewName("makeupclass");
				return mav;
			} catch (Exception e) {
				List<CourseSectionTeacher> result = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
				model.addAttribute("cst", result);
				model.addAttribute("time", timeTableRepository.getTimeSlots());
				model.addAttribute("rooms", timeTableRepository.getRooms());
				model.addAttribute("makeupError", true);
				mav.addObject("cancelmakeupPojo", makeupPojo);
				mav.setViewName("makeupclass");
				return mav;
			}
		}
		mav.setViewName("facultylogin");
		return mav;
	}

	/**
	 * Controller to call login from web page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/addins", method = { RequestMethod.GET, RequestMethod.POST })
	public String addInstructor(Model model, HttpSession httpSession) {
		if (httpSession.getAttribute("user") != null && httpSession.getAttribute("user") instanceof Teacher) {
			Teacher teacher = (Teacher) httpSession.getAttribute("user");
			List<CourseSectionTeacher> cst = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
			model.addAttribute("time", timeTableRepository.getTimeSlots());
			model.addAttribute("rooms", timeTableRepository.getRooms());
			model.addAttribute("teacher", teacherService.findAllForAddInstructor());
			model.addAttribute("cst", cst);
			return "addinstructor";
		} else
			return "facultylogin";
	}

	/**
	 * Controller to call login from web page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/ai/submit", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView addInstructorSubmit(@ModelAttribute("addinsPojo") @Valid AddInstructorPojo addInstructorPojo,
			BindingResult bindingResult, ModelMap model, HttpSession httpSession) {
		ModelAndView mav = new ModelAndView();
		if (httpSession.getAttribute("user") != null && httpSession.getAttribute("user") instanceof Teacher) {
			Teacher teacher = (Teacher) httpSession.getAttribute("user");
			boolean validation = true;
			if (addInstructorPojo.getTeacher() == -1
					&& (addInstructorPojo.getGuest() == null || addInstructorPojo.getGuest().isEmpty())) {
				model.addAttribute("tgError", true);
				validation = false;
				if (!validation) {
					model.addAttribute("validation", false);
					model.addAttribute("date", addInstructorPojo.getDate());
				}
				List<CourseSectionTeacher> result = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
				model.addAttribute("cst", result);
				model.addAttribute("time", timeTableRepository.getTimeSlots());
				model.addAttribute("rooms", timeTableRepository.getRooms());
				model.addAttribute("teacher", teacherService.findAllForAddInstructor());
				mav.addObject("addinsPojo", addInstructorPojo);
				mav.setViewName("addinstructor");
				return mav;
			}
			if (bindingResult.hasErrors()) {
				if (bindingResult.hasFieldErrors("date")) {
					model.addAttribute("dateError", bindingResult.getFieldError("date").getDefaultMessage());
					validation = false;
				}
				if (bindingResult.hasFieldErrors("guest")) {
					model.addAttribute("guestError", bindingResult.getFieldError("guest").getDefaultMessage());
					validation = false;
				}
				if (bindingResult.hasFieldErrors("room")) {
					model.addAttribute("roomError", bindingResult.getFieldError("room").getDefaultMessage());
					validation = false;
				}
				if (bindingResult.hasFieldErrors("cst")) {
					model.addAttribute("cstError", bindingResult.getFieldError("cst").getDefaultMessage());
					validation = false;
				}
				if (bindingResult.hasFieldErrors("timeslot")) {
					model.addAttribute("timeslotError", bindingResult.getFieldError("timeslot").getDefaultMessage());
					validation = false;
				}
				if (!validation) {
					model.addAttribute("validation", false);
					model.addAttribute("date", addInstructorPojo.getDate());
					model.addAttribute("guest", addInstructorPojo.getGuest());
				}
				List<CourseSectionTeacher> result = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
				model.addAttribute("cst", result);
				model.addAttribute("time", timeTableRepository.getTimeSlots());
				model.addAttribute("rooms", timeTableRepository.getRooms());
				model.addAttribute("teacher", teacherService.findAllForAddInstructor());
				mav.addObject("addinsPojo", addInstructorPojo);
				mav.setViewName("addinstructor");
				return mav;
			}
			try {
				courseSectionTeacherService.addInstructorGuest(addInstructorPojo, teacher);
				List<CourseSectionTeacher> result = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
				model.addAttribute("cst", result);
				model.addAttribute("time", timeTableRepository.getTimeSlots());
				model.addAttribute("rooms", timeTableRepository.getRooms());
				model.addAttribute("aiSuccess", true);
				mav.addObject("addinsPojo", addInstructorPojo);
				mav.setViewName("addinstructor");
				return mav;
			} catch (Exception e) {
				List<CourseSectionTeacher> result = courseSectionTeacherService.getCSTByTeacherId(teacher.getId());
				model.addAttribute("cst", result);
				model.addAttribute("time", timeTableRepository.getTimeSlots());
				model.addAttribute("rooms", timeTableRepository.getRooms());
				model.addAttribute("aiError", true);
				mav.addObject("addinsPojo", addInstructorPojo);
				mav.setViewName("addinstructor");
				return mav;
			}
		}
		mav.setViewName("facultylogin");
		return mav;
	}

	/**
	 * Controller to call Category page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/login", method = { RequestMethod.GET, RequestMethod.POST })
	public String login(@ModelAttribute Login login, Model model, HttpSession httpSession) {
		if (httpSession.getAttribute("user") != null && httpSession.getAttribute("user") instanceof Teacher) {
			Teacher teacher = (Teacher) httpSession.getAttribute("user");
			return findTeacher(model, teacher);
		} else {
			long entry = System.currentTimeMillis();
			String username = "", password = "";
			if (login.getUsername() != null) {
				username = login.getUsername();
				password = login.getPassword();

				Teacher teacher = teacherService.login(username, password);
				if (teacher != null) {
					String page = findTeacher(model, teacher);
					httpSession.setMaxInactiveInterval(900);
					httpSession.setAttribute("user", teacher);
					httpSession.setAttribute("username", login.getUsername());
					return page;
				} else {
					model.addAttribute("loginfail", true);
					return "facultylogin";
				}
			} else {
				return "facultylogin";
			}
		}

	}

	private String findTeacher(Model model, Teacher teacher) {
		List<Object> timetable = new ArrayList<>();
		for (HashMap<String, String> map : teacherService.getTeacherTimeTable(teacher.getId())) {

			Object record = new Object() {
				public String day = map.get("day");
				public String time = map.get("time");
				public String room = map.get("room");
				public String course = map.get("course");
				public String section = map.get("section");
				public String teacher = map.get("teacher");
				public String location = map.get("location");

			};
			timetable.add(record);
		}

		model.addAttribute("timetable", timetable);

		model.addAttribute("inbox", generateInboxItems(teacher));
		model.addAttribute("teacher", teacher);
		return "facultyhome";
	}

	private List<Object> generateInboxItems(Teacher teacher) {
		List<Object> inboxItems = new ArrayList<>();
		String itemString = "";
		List<AddInstructor> list = addInstructorRepository.findInboxItem(teacher.getId(),
				Calendar.getInstance().getTime());
		for (AddInstructor ai : list) {
			itemString = "";
			itemString = createInboxMessage(teacher, itemString, ai);
			inboxItems.add(getInboxItem(itemString, ai));
		}
		return inboxItems;
	}

	private String createInboxMessage(Teacher teacher, String itemString, AddInstructor ai) {
		if (ai.getTeacher().getId() == teacher.getId()) {

			if (AddInstructor.STATUS_ACCEPT.equals(ai.getStatus())) {
				itemString += "You accepted invitation for class "
						+ ai.getCourseSectionTeacher().getCourse().getFullName() + " by " + ai.getInvitedBy().getName();
			} else if (AddInstructor.STATUS_REJECT.equals(ai.getStatus())) {

				itemString += "You rejected invitation for class "
						+ ai.getCourseSectionTeacher().getCourse().getFullName() + " by " + ai.getInvitedBy().getName();

			} else if (AddInstructor.STATUS_PENDING.equals(ai.getStatus())) {
				itemString += "Your are invited by " + ai.getInvitedBy().getName() + " as an instructor in class of "
						+ ai.getCourseSectionTeacher().getCourse().getFullName();
			}
		}
		if (ai.getInvitedBy().getId() == teacher.getId()) {
			if (AddInstructor.STATUS_ACCEPT.equals(ai.getStatus())) {
				itemString += ai.getTeacher().getName() + " accepted your invitation for class "
						+ ai.getCourseSectionTeacher().getCourse().getFullName();
			} else if (AddInstructor.STATUS_REJECT.equals(ai.getStatus())) {

				itemString += ai.getTeacher().getName() + " rejected your invitation for class "
						+ ai.getCourseSectionTeacher().getCourse().getFullName();

			} else if (AddInstructor.STATUS_PENDING.equals(ai.getStatus())) {
				itemString += "You invited  " + ai.getTeacher().getName() + " as an instructor in class of "
						+ ai.getCourseSectionTeacher().getCourse().getFullName();
			}

		}
		return itemString;
	}

	private Object getInboxItem(String itemString, AddInstructor ai) {
		Object inbox = new Object() {
			public String item = itemString;
			public long aiId = ai.getId();

		};
		return inbox;
	}

	/**
	 * Controller
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/ai/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String acceptReject(@PathVariable Long id, Model model, HttpSession httpSession) {
		if (httpSession.getAttribute("user") != null && httpSession.getAttribute("user") instanceof Teacher) {
			Teacher teacher = (Teacher) httpSession.getAttribute("user");
			AddInstructor addInstructor = addInstructorRepository.findOne(id);
			if (addInstructor != null && addInstructor.getTeacher().getId() == teacher.getId()) {
				model.addAttribute("addInstructor", createInboxMessage(teacher, "", addInstructor));
				model.addAttribute("accept", AddInstructor.STATUS_ACCEPT.equals(addInstructor.getStatus()));
				model.addAttribute("reject", AddInstructor.STATUS_REJECT.equals(addInstructor.getStatus()));
				model.addAttribute("aiId", id);
				return "acceptReject";
			}
			if (addInstructor != null && addInstructor.getInvitedBy().getId() == teacher.getId()) {
				model.addAttribute("addInstructor", createInboxMessage(teacher, "", addInstructor));
				return "acceptReject";
			}
		}
		return "facultylogin";

	}

	/**
	 * Controller
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/ai/accept/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String acceptSubmit(@PathVariable Long id, Model model, HttpSession httpSession) {
		if (httpSession.getAttribute("user") != null && httpSession.getAttribute("user") instanceof Teacher) {
			Teacher teacher = (Teacher) httpSession.getAttribute("user");
			AddInstructor addInstructor = addInstructorRepository.findOne(id);
			if (addInstructor != null && addInstructor.getTeacher().getId() == teacher.getId()) {
				addInstructor.setStatus(AddInstructor.STATUS_ACCEPT);
				addInstructorRepository.save(addInstructor);
				model.addAttribute("addInstructor", createInboxMessage(teacher, "", addInstructor));
				model.addAttribute("accept", AddInstructor.STATUS_ACCEPT.equals(addInstructor.getStatus()));
				model.addAttribute("reject", AddInstructor.STATUS_REJECT.equals(addInstructor.getStatus()));
				model.addAttribute("aiId", id);
				return "acceptReject";
			}
		}
		return "facultylogin";

	}

	/**
	 * Controller
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/ai/reject/{id}", method = { RequestMethod.GET, RequestMethod.POST })
	public String rejectSubmit(@PathVariable Long id, Model model, HttpSession httpSession) {
		if (httpSession.getAttribute("user") != null && httpSession.getAttribute("user") instanceof Teacher) {
			Teacher teacher = (Teacher) httpSession.getAttribute("user");
			AddInstructor addInstructor = addInstructorRepository.findOne(id);
			if (addInstructor != null && addInstructor.getTeacher().getId() == teacher.getId()) {
				addInstructor.setStatus(AddInstructor.STATUS_REJECT);
				addInstructorRepository.save(addInstructor);
				model.addAttribute("addInstructor", createInboxMessage(teacher, "", addInstructor));
				model.addAttribute("accept", AddInstructor.STATUS_ACCEPT.equals(addInstructor.getStatus()));
				model.addAttribute("reject", AddInstructor.STATUS_REJECT.equals(addInstructor.getStatus()));
				model.addAttribute("aiId", id);
				return "acceptReject";
			}
		}
		return "facultylogin";

	}

	/**
	 * Controller to call registration page.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	// @RequestMapping(path = "/register", method = { RequestMethod.GET,
	// RequestMethod.POST })
	// public String register(Model model) {
	// List<CourseSectionTeacher> result =
	// courseSectionTeacherService.getAllCST();
	// model.addAttribute("cst", result);
	// return "registration";
	// }

	/**
	 * Controller to call login from web page service.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	// @RequestMapping(path = "/register/submit", method = { RequestMethod.GET,
	// RequestMethod.POST })
	// public ModelAndView processRegistration(
	// @ModelAttribute("registrationPojo") @Valid RegistrationPojo
	// registrationPojo, BindingResult bindingResult,
	// ModelMap model) {
	// boolean validation = true;
	// if (bindingResult.hasErrors()) {
	// ModelAndView mav = new ModelAndView();
	// if (bindingResult.hasFieldErrors("fullName")) {
	// model.addAttribute("fullNameError",
	// bindingResult.getFieldError("fullName").getDefaultMessage());
	// validation = false;
	// }
	// if (bindingResult.hasFieldErrors("section")) {
	// model.addAttribute("sectionError",
	// bindingResult.getFieldError("section").getDefaultMessage());
	// validation = false;
	// }
	// if (bindingResult.hasFieldErrors("rollNumber")) {
	// model.addAttribute("rollNumberError",
	// bindingResult.getFieldError("rollNumber").getDefaultMessage());
	// validation = false;
	// }
	// if (bindingResult.hasFieldErrors("batch")) {
	// model.addAttribute("batchError",
	// bindingResult.getFieldError("batch").getDefaultMessage());
	// validation = false;
	// }
	// if (bindingResult.hasFieldErrors("userName")) {
	// model.addAttribute("userNameError",
	// bindingResult.getFieldError("userName").getDefaultMessage());
	// validation = false;
	// }
	// if (bindingResult.hasFieldErrors("email")) {
	// model.addAttribute("emailError",
	// bindingResult.getFieldError("email").getDefaultMessage());
	// validation = false;
	// }
	// if (bindingResult.hasFieldErrors("mobileNumber")) {
	// model.addAttribute("mobileNumberError",
	// bindingResult.getFieldError("mobileNumber").getDefaultMessage());
	// validation = false;
	// }
	// if (!validation) {
	// model.addAttribute("validation", false);
	// model.addAttribute("fullName", registrationPojo.getFullName());
	// model.addAttribute("section", registrationPojo.getSection());
	// model.addAttribute("rollNumber", registrationPojo.getRollNumber());
	// model.addAttribute("batch", registrationPojo.getBatch());
	// model.addAttribute("userName", registrationPojo.getUserName());
	// model.addAttribute("email", registrationPojo.getEmail());
	// model.addAttribute("mobileNumber", registrationPojo.getMobileNumber());
	// }
	// List<CourseSectionTeacher> result =
	// courseSectionTeacherService.getAllCST();
	// model.addAttribute("cst", result);
	// mav.addObject("registrationPojo", registrationPojo);
	// mav.setViewName("registration");
	// return mav;
	// }
	//
	// Student student = registrationService.register(registrationPojo);
	// if (student != null) {
	// model.addAttribute("attr",
	// encryptDecrypt(Long.toString(student.getId())));
	// } else {
	// model.addAttribute("attr", "-1");
	// }
	// return new ModelAndView("redirect:/web", model);
	// }

	/**
	 * Controller to call change password page.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/changepassword", method = { RequestMethod.GET })
	public String changepassword(Model model, HttpSession httpSession) {
		if (httpSession.getAttribute("user") != null && httpSession.getAttribute("user") instanceof Teacher)
			return "facultychangepassword";
		else
			return "facultylogin";
	}

	/**
	 * Controller to call change password page.
	 * 
	 * @param request
	 * @param httpreq
	 * @return
	 */
	@RequestMapping(path = "/changepassword", method = { RequestMethod.POST })
	public ModelAndView changepassword(
			@ModelAttribute("changepasswordPojo") @Valid ChangePasswordPojo changePasswordPojo,
			BindingResult bindingResult, ModelMap model) {
		boolean validation = true;
		if (bindingResult.hasErrors()) {
			ModelAndView mav = new ModelAndView();
			if (bindingResult.hasFieldErrors("userName")) {
				model.addAttribute("userNameError", bindingResult.getFieldError("userName").getDefaultMessage());
				validation = false;
			}
			if (teacherService.login(changePasswordPojo.getUserName(), changePasswordPojo.getOldPassword()) == null) {
				model.addAttribute("oldPasswordError", "Old Password invalid");
				validation = false;
			}
			if (bindingResult.hasFieldErrors("newPassword")) {
				model.addAttribute("newPasswordError", bindingResult.getFieldError("newPassword").getDefaultMessage());
				validation = false;
			}
			if (bindingResult.hasFieldErrors("confirmPassword")) {
				model.addAttribute("confirmPasswordError",
						bindingResult.getFieldError("confirmPassword").getDefaultMessage());
				validation = false;
			}
			if (bindingResult.hasFieldErrors("conditionTrue")) {
				model.addAttribute("isConditionTrue", bindingResult.getFieldError("conditionTrue").getDefaultMessage());
				validation = false;
			}
			if (!validation) {
				setFieldsForError(changePasswordPojo, model);
			}
			mav.addObject("changePasswordPojo", changePasswordPojo);
			mav.setViewName("facultychangepassword");
			return mav;
		} else {
			ModelAndView mav = new ModelAndView();
			if (teacherService.login(changePasswordPojo.getUserName(), changePasswordPojo.getOldPassword()) == null) {
				model.addAttribute("oldPasswordError", "Old Password invalid");
				setFieldsForError(changePasswordPojo, model);
				mav.addObject("changePasswordPojo", changePasswordPojo);
				mav.setViewName("facultychangepassword");
				return mav;
			}

			if (teacherService.changePassword(changePasswordPojo.getUserName(), changePasswordPojo.getNewPassword())) {
				model.addAttribute("changePasswordSuccess", true);
			} else {
				model.addAttribute("changePasswordError", true);
			}
			return new ModelAndView("facultychangepassword", model);
		}
	}

	private void setFieldsForError(ChangePasswordPojo changePasswordPojo, ModelMap model) {
		model.addAttribute("validation", false);
		model.addAttribute("userName", changePasswordPojo.getUserName());
		model.addAttribute("oldPassword", "");
		model.addAttribute("newPassword", "");
		model.addAttribute("confirmPassword", "");
	}

}
