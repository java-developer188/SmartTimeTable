package com.fast.timetable.pojo;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChangePasswordPojo {
	
	@NotNull(message = "UserName cannot be empty")
	@Size(min=6, max= 20 , message = "UserName must be between 6 and 20 characters")
	private String userName;
	
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
