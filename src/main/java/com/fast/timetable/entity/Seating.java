package com.fast.timetable.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "seating")
public class Seating implements java.io.Serializable {

	private static final long serialVersionUID = 8756132782088536600L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String date;

	private String time;

	private String room;

	@OneToOne
	private CSTStudent courseSectionTeacherStudent;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public CSTStudent getCourseSectionTeacherStudent() {
		return courseSectionTeacherStudent;
	}

	public void setCourseSectionTeacherStudent(CSTStudent courseSectionTeacherStudent) {
		this.courseSectionTeacherStudent = courseSectionTeacherStudent;
	}

	
}
