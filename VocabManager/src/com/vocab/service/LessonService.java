package com.vocab.service;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vocab.consts.APIConsts;
import com.vocab.model.Lesson;
import com.vocab.model.Response;
import com.vocab.utils.HttpUtils;

public class LessonService {
	public static void main(String[] ar) {
		System.out.println("OK");
		delete(2222);
	}
	
	public static int getsCount(String q, String courseID, String statusID){
		q = URLEncoder.encode(q, StandardCharsets.UTF_8);
		String url = APIConsts.BASE_URL + APIConsts.API_LESSON_GETS_COUNT;
		
		url += "?" + APIConsts.KEY_SEARCH_STR + q + 
				"&" + APIConsts.KEY_COURSE_ID + courseID +
				"&" + APIConsts.KEY_STATUS_ID + statusID;
		
		String method = HttpMethod.GET;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "";
		String count = HttpUtils.request(url, method, contentType, msg);
		System.out.println(count);
		return Integer.parseInt(count);
	}
	
	public static List<Lesson> gets(String q, String courseID, String statusID, int ...limitAndOffset){
		q = URLEncoder.encode(q, StandardCharsets.UTF_8);
		String url = APIConsts.BASE_URL + APIConsts.API_LESSON_GETS;
		
		if(limitAndOffset.length == 2) {
			url += "/" + limitAndOffset[0] + "/" + limitAndOffset[1];
		}
		
		url += "?" + APIConsts.KEY_SEARCH_STR + q + 
				"&" + APIConsts.KEY_COURSE_ID + courseID +
				"&" + APIConsts.KEY_STATUS_ID + statusID;
		
		String method = HttpMethod.GET;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "";
		String json = HttpUtils.request(url, method, contentType, msg);
		System.out.println(json);
		
		Type listType = new TypeToken<ArrayList<Lesson>>(){}.getType();
		
		List<Lesson> list = new Gson().fromJson(json, listType);
		return list;
	}
	

	public static Response save(Lesson lesson){
		String url = APIConsts.BASE_URL + APIConsts.API_LESSON_SAVE;
		String method = HttpMethod.PUT;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "lesson_name=" + lesson.getLesson_name() +
				"&lesson_course=" + lesson.getLesson_course() +
				"&lesson_status=" + lesson.getLesson_status();
		
		
		String json = HttpUtils.request(url, method, contentType, msg);
		System.out.println(json);
				
		Response response = new Gson().fromJson(json, Response.class);
		System.out.println(response.getResponse_description());
		return response;
	}

	public static Response update(Lesson lesson){
		String url = APIConsts.BASE_URL + APIConsts.API_LESSON_UPDATE;
		String method = HttpMethod.POST;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "lesson_id=" + lesson.getLesson_id() +
				"&lesson_name=" + lesson.getLesson_name() +
				"&lesson_course=" + lesson.getLesson_course() +
				"&lesson_status=" + lesson.getLesson_status();
		
		
		String json = HttpUtils.request(url, method, contentType, msg);
		System.out.println(json);
				
		Response response = new Gson().fromJson(json, Response.class);
		System.out.println(response.getResponse_description());
		return response;
	}

	public static Response delete(int id){
		String url = APIConsts.BASE_URL + APIConsts.API_LESSON_DELETE + id;
		String method = HttpMethod.DELETE;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		
		
		String json = HttpUtils.request(url, method, contentType, "");
		System.out.println(json);
				
		Response response = new Gson().fromJson(json, Response.class);
		System.out.println(response.getResponse_description());
		return response;
	}
}
