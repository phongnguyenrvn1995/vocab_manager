package com.vocab.model;


public class Lesson{
	private int lesson_id;
	private int lesson_course;
	private String lesson_name;
	private int lesson_status;
	public int getLesson_id() {
		return lesson_id;
	}
	public void setLesson_id(int lesson_id) {
		this.lesson_id = lesson_id;
	}
	public int getLesson_course() {
		return lesson_course;
	}
	public void setLesson_course(int lesson_course) {
		this.lesson_course = lesson_course;
	}
	public String getLesson_name() {
		return lesson_name;
	}
	public void setLesson_name(String lesson_name) {
		this.lesson_name = lesson_name;
	}
	public int getLesson_status() {
		return lesson_status;
	}
	public void setLesson_status(int lesson_status) {
		this.lesson_status = lesson_status;
	}
}
