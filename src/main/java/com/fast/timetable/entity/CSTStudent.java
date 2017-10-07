package com.fast.timetable.entity;

import javax.persistence.*;

@Entity
@Table(name = "cst_student")
public class CSTStudent implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne
	private CourseSectionTeacher courseSectionTeacher;
	
	@OneToOne
	private Student student;

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

}
