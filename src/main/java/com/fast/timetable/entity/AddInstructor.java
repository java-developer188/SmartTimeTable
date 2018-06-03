package com.fast.timetable.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "add_instructor")
public class AddInstructor {
	
	public static final String STATUS_PENDING	=	"P";
	public static final String STATUS_ACCEPT	=	"A";
	public static final String STATUS_REJECT	=	"R";
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private Date date;
	
	@OneToOne
	private CourseSectionTeacher courseSectionTeacher;

	@OneToOne
	private Teacher teacher;
	
	private String time;
	
	private String room;
	
	private String guest;
	
	private String status;
	
	@OneToOne
	private Teacher invitedBy;
	

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public CourseSectionTeacher getCourseSectionTeacher() {
		return courseSectionTeacher;
	}

	public void setCourseSectionTeacher(CourseSectionTeacher courseSectionTeacher) {
		this.courseSectionTeacher = courseSectionTeacher;
	}
	

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "invited_by")
	public Teacher getInvitedBy() {
		return invitedBy;
	}

	public void setInvitedBy(Teacher invitedBy) {
		this.invitedBy = invitedBy;
	}

	

	
}
