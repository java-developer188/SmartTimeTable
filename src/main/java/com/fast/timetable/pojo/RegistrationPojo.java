package com.fast.timetable.pojo;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Range;


public class RegistrationPojo {

	private Integer id;
	
	@Range(min=2011,max=2020,message = "Batch must be in the range 2011-2018")
	private Integer batch;
	
	@NotNull
	@Size(max= 15 , message = "Roll number cannot exceed 15 characters")
	private String rollNumber;
	
	@NotNull(message = "Name must not be empty")
	@Size(min=2, max= 255 , message = "Full Name must be between 2 and 255 characters")
	private String fullName;
	
	@NotNull(message = "UserName must not be empty")
	@Size(min=6, max= 20 , message = "UserName must be between 6 and 20 characters")
	private String userName;
	
	@NotNull(message = "Section must not be empty")
	@Size(min=1, max= 3 , message = "Section length must not exceed 3 characters")
	@Pattern(regexp="(([A-Z]{2}|[a-z]{2}|[A-Z][a-z]|[a-z][A-Z])[0-9])|(([A-Z]|[a-z])(?![A-Z]|[a-z]))" , message = "Value must be like A,Gr1,GR2")
	private String section;
	
	@NotNull
	@Size(min=11, max= 11 , message = "Phone number must be of 11 characters")
	private String mobileNumber;

	@NotNull
	@Email
	private String email;
	
	private List<Long> courses;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBatch() {
		return batch;
	}

	public void setBatch(Integer batch) {
		this.batch = batch;
	}

	public String getRollNumber() {
		return rollNumber;
	}

	public void setRollNumber(String rollNumber) {
		this.rollNumber = rollNumber;
	}
	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	

	public List<Long> getCourses() {
		return courses;
	}

	public void setCourses(List<Long> courses) {
		this.courses = courses;
	}

	
	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

}
