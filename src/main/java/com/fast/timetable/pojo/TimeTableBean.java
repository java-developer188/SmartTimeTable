package com.fast.timetable.pojo;

public class TimeTableBean implements java.io.Serializable{

		private int cstId;
		
		private String day;
		
		private String time;
		
		private String room;
		
		public TimeTableBean() {
		}

		public int getCstId() {
			return cstId;
		}

		public void setCstId(int cstId) {
			this.cstId = cstId;
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

	}
