package com.fast.timetable.pojo;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fast.timetable.entity.Teacher;
import com.fast.timetable.service.TeacherService;

public class ChangePasswordPojo {
	
	@Autowired
	TeacherService teacherService;
	
	@NotNull(message = "UserName cannot be empty")
	@Size(min=2, max= 20 , message = "UserName must be between 2 and 20 characters")
	private String userName;
	
	private String oldPassword;
	
	@NotNull(message = "Password cannot be empty")
	@Size(min= 8 , message = "Password must have at least 8 characters")
	private String newPassword;
	
	@NotNull(message = "Confirm Password cannot be empty")
	private String confirmPassword;
	

	@AssertTrue(message="Two passwords must be same")
	public boolean isConditionTrue(){
		return this.newPassword.equals(this.confirmPassword);
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}

	

	public String getOldPassword() {
		return oldPassword;
	}


	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}


	public String getNewPassword() {
		return newPassword;
	}


	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}


	public String getConfirmPassword() {
		return confirmPassword;
	}


	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	
	
}
