package com.fast.timetable.entity;

import javax.persistence.*;

@Entity
@Table(name = "time_table")
public class TimeTable implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String day;

	private String time;

	private String room;

	@OneToOne
	private CourseSectionTeacher courseSectionTeacher;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
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

	public CourseSectionTeacher getCourseSectionTeacher() {
		return courseSectionTeacher;
	}

	public void setCourseSectionTeacher(CourseSectionTeacher courseSectionTeacher) {
		this.courseSectionTeacher = courseSectionTeacher;
	}

}
