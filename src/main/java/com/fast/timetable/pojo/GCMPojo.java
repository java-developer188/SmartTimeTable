package com.fast.timetable.pojo;

public class GCMPojo {

	private String to;
	private GCMPojo.Notification notification;

	public GCMPojo(String to, String message, String title) {
		this.to = to;
		this.notification = new Notification(message, title);
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public GCMPojo.Notification getNotification() {
		return notification;
	}

	public void setNotification(GCMPojo.Notification notification) {
		this.notification = notification;
	}

	private static class Notification {

		String text;
		String title;

		public Notification(String text, String title) {
			super();
			this.text = text;
			this.title = title;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}
}
