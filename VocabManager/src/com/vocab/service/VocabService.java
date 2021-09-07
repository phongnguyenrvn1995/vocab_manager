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
import com.vocab.model.Vocab;
import com.vocab.utils.HttpUtils;

public class VocabService {

	public static void main(String[] ar) {
		System.out.println("OK");
//		System.out.println(gets("en 1", "", "").size());
//		System.out.println(getsCount("en ", "", ""));
	}

	public static int getsCount(String q, String typeID, String lessonID){
		q = URLEncoder.encode(q, StandardCharsets.UTF_8);
		String url = APIConsts.BASE_URL + APIConsts.API_VOCAB_GETS_COUNT;
		
		url += "?" + APIConsts.KEY_SEARCH_STR + q + 
				"&" + APIConsts.KEY_TYPE_ID + typeID +
				"&" + APIConsts.KEY_LESSON_ID + lessonID;

		String method = HttpMethod.GET;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "";
		String count = HttpUtils.request(url, method, contentType, msg);
		System.out.println(count);
		return Integer.parseInt(count);
	}

	public static List<Vocab> gets(String q, String typeID, String lessonID, int ...limitAndOffset){
		q = URLEncoder.encode(q, StandardCharsets.UTF_8);
		String url = APIConsts.BASE_URL + APIConsts.API_VOCAB_GETS;
		
		if(limitAndOffset.length == 2) {
			url += "/" + limitAndOffset[0] + "/" + limitAndOffset[1];
		}
		
		url += "?" + APIConsts.KEY_SEARCH_STR + q + 
				"&" + APIConsts.KEY_TYPE_ID + typeID +
				"&" + APIConsts.KEY_LESSON_ID + lessonID;
		
		String method = HttpMethod.GET;
		String contentType = MediaType.APPLICATION_FORM_URLENCODED;
		String msg = "";
		String json = HttpUtils.request(url, method, contentType, msg);
		System.out.println(json);
		
		Type listType = new TypeToken<ArrayList<Vocab>>(){}.getType();
		
		List<Vocab> list = new Gson().fromJson(json, listType);
		return list;
	}
	

}
