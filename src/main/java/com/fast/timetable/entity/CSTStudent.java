package com.fast.timetable.entity;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "cst_student")
public class CSTStudent implements java.io.Serializable {

	private static final long serialVersionUID = 4027614859915842193L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne
	private CourseSectionTeacher courseSectionTeacher;
	
	@OneToOne
	private Student student;
	
	private Integer totalAttend;
	
	private Date lastAttend;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public CourseSectionTeacher getCourseSectionTeacher() {
		return courseSectionTeacher;
	}

	public void setCourseSectionTeacher(CourseSectionTeacher courseSectionTeacher) {
		this.courseSectionTeacher = courseSectionTeacher;
	}

	public Integer getTotalAttend() {
		return totalAttend;
	}

	public void setTotalAttend(Integer totalAttend) {
		this.totalAttend = totalAttend;
	}

	public Date getLastAttend() {
		return lastAttend;
	}

	public void setLastAttend(Date lastAttend) {
		this.lastAttend = lastAttend;
	}
	
	

}
