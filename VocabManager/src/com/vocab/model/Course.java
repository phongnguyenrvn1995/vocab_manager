package com.vocab.model;

public class Course{
	private int course_id;
	private String course_name;
	private String course_description;
	private String course_date_creat;
	private int course_status;
	public int getCourse_id() {
		return course_id;
	}
	public void setCourse_id(int course_id) {
		this.course_id = course_id;
	}
	public String getCourse_name() {
		return course_name;
	}
	public void setCourse_name(String course_name) {
		this.course_name = course_name;
	}
	public String getCourse_description() {
		return course_description;
	}
	public void setCourse_description(String course_description) {
		this.course_description = course_description;
	}
	public String getCourse_date_creat() {
		return course_date_creat;
	}
	public void setCourse_date_creat(String course_date_creat) {
		this.course_date_creat = course_date_creat;
	}
	public int getCourse_status() {
		return course_status;
	}
	public void setCourse_status(int course_status) {
		this.course_status = course_status;
	}
}
