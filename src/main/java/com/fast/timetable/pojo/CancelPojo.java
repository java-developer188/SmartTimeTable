package com.fast.timetable.pojo;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

public class CancelPojo {

	
	
	@NotNull(message = "Date must be selected")
	@NotEmpty(message = "Date must be selected")
	private String date;
	
	@NotNull(message = "Course must be selected")
	private Long cst;
	
	@NotNull(message = "Time slot must be selected")
	private String timeslot;
	
	private String room;
	
	private String type;

	


	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getCst() {
		return cst;
	}

	public void setCst(Long cst) {
		this.cst = cst;
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
	
	

}
