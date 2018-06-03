package com.fast.timetable.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "cancel_makeup_class")
public class CancelMakeupClass {
	
	public static final String MAKE_UP = "M";
	public static final String CANCEL  = "C";
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private Date date;
	
	@OneToOne
	private CourseSectionTeacher courseSectionTeacher;
	
	private String time;
	
	private String room;
	
	private String action;
	

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	
	public String getTime() {
		return time;
	}

	public void setTime(String timeslot) {
		this.time = timeslot;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CourseSectionTeacher getCourseSectionTeacher() {
		return courseSectionTeacher;
	}

	public void setCourseSectionTeacher(CourseSectionTeacher courseSectionTeacher) {
		this.courseSectionTeacher = courseSectionTeacher;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
