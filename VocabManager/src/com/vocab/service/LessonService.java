package com.vocab.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vocab.consts.APIConsts;
import com.vocab.model.Lesson;
import com.vocab.utils.HttpUtils;

public class LessonService {
	public static void main(String[] ar) {
		System.out.println("OK");
		List<Lesson> l = gets("10011", "", "");
		for(Lesson i : l) {
			System.out.println(i.getLesson_name());
		}
		
		System.out.println(getsCount("10011", "", ""));
	}
	
	public static int getsCount(String q, String courseID, String statusID){
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
}
