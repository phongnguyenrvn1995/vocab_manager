package com.vocab.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vocab.consts.APIConsts;
import com.vocab.model.Course;
import com.vocab.model.Response;
import com.vocab.utils.HttpUtils;

public class CourseService {
	public static void main(String[] ar) {
		System.out.println("OK");
		System.out.println(getsCount("name"));
	}
	
	public static int getsCount(String q){
		String url = APIConsts.BASE_URL + APIConsts.API_COURSE_GETS_COUNT;
		url += "?" + APIConsts.KEY_SEARCH_STR + q;
		
		String method = HttpMethod.GET;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "";
		String count = HttpUtils.request(url, method, contentType, msg);
		System.out.println(count);
		return Integer.parseInt(count);
	}

	
	public static List<Course> gets(String q, int ...limitAndOffset){
		String url = APIConsts.BASE_URL + APIConsts.API_COURSE_GETS;
		
		if(limitAndOffset.length == 2) {
			url += "/" + limitAndOffset[0] + "/" + limitAndOffset[1];
		}
		
		url += "?" + APIConsts.KEY_SEARCH_STR + q;
		
		String method = HttpMethod.GET;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "";
		String json = HttpUtils.request(url, method, contentType, msg);
		System.out.println(json);
		
		Type listType = new TypeToken<ArrayList<Course>>(){}.getType();
		
		List<Course> list = new Gson().fromJson(json, listType);
		return list;
	}
	

	public static Response save(Course course){
		String url = APIConsts.BASE_URL + APIConsts.API_COURSE_SAVE;
		String method = HttpMethod.PUT;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "course_name=" + course.getCourse_name() +
				"&course_description=" + course.getCourse_description() +
				"&course_date_creat=" + course.getCourse_date_creat() +
				"&course_status=" + course.getCourse_status();
		
		
		String json = HttpUtils.request(url, method, contentType, msg);
		System.out.println(json);
				
		Response response = new Gson().fromJson(json, Response.class);
		System.out.println(response.getResponse_description());
		return response;
	}

}
