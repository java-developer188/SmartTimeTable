package com.fast.timetable.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Student {


		@Id
		@GeneratedValue(strategy=GenerationType.AUTO)
		private Long id;
		
		private int batch;
		
		private String rollNumber;
		
		private String fullName;
		
		private String section;
		
		private String mobileNumber;
		
		private String email;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public int getBatch() {
			return batch;
		}

		public void setBatch(int batch) {
			this.batch = batch;
		}

		public String getRollNumber() {
			return rollNumber;
		}

		public void setRollNumber(String rollNumber) {
			this.rollNumber = rollNumber;
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

		public String getMobileNumber() {
			return mobileNumber;
		}

		public void setMobileNumber(String mobileNumber) {
			this.mobileNumber = mobileNumber;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
		

}
