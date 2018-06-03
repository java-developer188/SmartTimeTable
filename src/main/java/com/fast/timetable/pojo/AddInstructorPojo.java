package com.fast.timetable.pojo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

public class AddInstructorPojo {
	
	@NotNull(message = "Date must be selected")
	@NotEmpty(message = "Date must be selected")
	private String date;
	
	@NotNull(message = "Course must be selected")
	private Long cst;

	private Long teacher;
	
	@NotNull(message = "Time slot must be selected")
	private String timeslot;
	
	@NotNull(message = "Room must be selected")
	private String room;
	
	@Pattern(regexp="[A-Za-z\\s]*",message="Invalid name")
	private String guest;
	
	private Long id;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Long getCst() {
		return cst;
	}

	public void setCst(Long cst) {
		this.cst = cst;
	}

	public Long getTeacher() {
		return teacher;
	}

	public void setTeacher(Long teacher) {
		this.teacher = teacher;
	}

	public String getTimeslot() {
		return timeslot;
	}

	public void setTimeslot(String timeslot) {
		this.timeslot = timeslot;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getGuest() {
		return guest;
	}

	public void setGuest(String guest) {
		this.guest = guest;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
}
