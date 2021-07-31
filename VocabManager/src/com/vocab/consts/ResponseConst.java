package com.vocab.consts;

public class ResponseConst {
	public static final int SUCCESS = 0;
	public static final int DEL_COURSE_ERROR_EXISTING_LESSON_IN_COURSE = 1;
	public static final int DEL_LESSON_ERROR_EXISTING_VOCAB_IN_LESSON = 2;

	public static final int DEL_STATUS_ERROR_EXISTING_COURSE_USES_STATUS = 3;
	public static final int DEL_STATUS_ERROR_EXISTING_LESSON_USES_STATUS = 4;

	public static final int DEL_VOCAB_TYPE_ERROR_EXISTING_VOCAB_USES_TYPE = 5;

	public static final int ERROR_STATUS_DOES_NOT_EXIST = 6;
	public static final int ERROR_COURSE_DOES_NOT_EXIST = 7;
	public static final int ERROR_LESSON_DOES_NOT_EXIST = 8;
	public static final int ERROR_TYPE_DOES_NOT_EXIST = 9;
	
	public static final int ERROR_DB_ERROR = 99;
}
